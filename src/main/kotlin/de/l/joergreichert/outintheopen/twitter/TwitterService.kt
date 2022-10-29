package de.l.joergreichert.outintheopen.twitter

import com.github.scribejava.core.pkce.PKCE
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.auth.TwitterOAuth20Service
import com.twitter.clientlib.model.ResourceUnauthorizedProblem
import de.l.joergreichert.outintheopen.config.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileWriter
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class TwitterService @Autowired constructor(val appProperties: AppProperties) {

    fun triggerAuth(): String {
        val credentials = createTwitterCredentialsOAuth2()
        val service = TwitterOAuth20Service(
                credentials.twitterOauth2ClientId,
                credentials.twitterOAuth2ClientSecret,
                "http://localhost:5000/callback",
                "offline.access tweet.read users.read"
        )
        AuthStore.codeVerifier = generateCodeVerifier()
        AuthStore.codeChallenge = generateCodeChallenge(AuthStore.codeVerifier!!)
        val pkce = PKCE().apply {
            codeChallenge = AuthStore.codeChallenge
            codeChallengeMethod = PKCECodeChallengeMethod.S256
            codeVerifier = AuthStore.codeVerifier
        }
        val secretState = "state-${UUID.randomUUID()}"
        val authorizationUrl = service.getAuthorizationUrl(pkce, secretState)
        //template.postForLocation(authorizationUrl, null, emptyMap<String, Any>())
        return authorizationUrl
    }

    private fun createTwitterCredentialsOAuth2() = TwitterCredentialsOAuth2(
            appProperties.twitter.clientId,
            appProperties.twitter.clientSecret,
            TokenStore.accessToken,
            TokenStore.refreshToken,
    )

    fun storeAccessToken(code: String) {
        val credentials = createTwitterCredentialsOAuth2()
        val service = TwitterOAuth20Service(
                credentials.twitterOauth2ClientId,
                credentials.twitterOAuth2ClientSecret,
                "http://twitter.com",
                "offline.access tweet.read users.read"
        )
        try {
            val pkce = PKCE().apply {
                codeChallenge = AuthStore.codeChallenge
                codeChallengeMethod = PKCECodeChallengeMethod.S256
                codeVerifier = AuthStore.codeVerifier
            }
            val accessToken = service.getAccessToken(pkce, code)
            TokenStore.accessToken = accessToken.accessToken
            TokenStore.refreshToken = accessToken.refreshToken
        } catch (e: Exception) {
            System.err.println("Error while getting the access token:\n $e")
            e.printStackTrace()
        }
    }

    private fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val codeVerifier = ByteArray(32)
        secureRandom.nextBytes(codeVerifier)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier)
    }

    fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(charset("US-ASCII"))
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        val digest = messageDigest.digest()
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    fun listLikes(accessToken: String? = null): List<String> {
        accessToken?.let { TokenStore.accessToken = accessToken }
        val credentials = createTwitterCredentialsOAuth2()
        val apiInstance = TwitterApi(credentials)
        val tweetFields = setOf("author_id", "id", "created_at")
        try {
            // findTweetById
            var pageToken: String? = null
            val maxDate = OffsetDateTime.of(LocalDateTime.of(2022, 10, 1, 0, 0, 0), ZoneOffset.ofHours(1))
            val list = mutableListOf<String>()
            do {
                var stop = false
                val result =
                        apiInstance.tweets().usersIdLikedTweets("83401212").paginationToken(pageToken).maxResults(100)
                                .tweetFields(tweetFields).execute()
                if (result.errors?.size?.let { it > 0 } == true) {
                    stop = true
                    println("Error:")
                    result.errors!!.forEach { e ->
                        println(e.toString())
                        if (e is ResourceUnauthorizedProblem) {
                            println(e.title + " " + e.detail)
                        }
                    }
                } else {
                    val decimalFormat = DecimalFormat("000")
                    val simpleDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                    for ((index, tweet) in result.data!!.withIndex()) {
                        if (tweet.createdAt?.isBefore(maxDate) == true) stop = true
                        if (stop) break
                        val author =
                                apiInstance.users().findUserById(tweet.authorId).userFields(setOf("username", "name")).execute()
                        val authorName = author.data?.username
                        val content = """
                            ${decimalFormat.format(index)}. ${simpleDateFormat.format(tweet.createdAt)}}: ${tweet.text}
                            ${tweet.entities?.toJson()}
                            https://twitter.com/${authorName}/status/${tweet.id}                        
                        """.trimIndent()
                        list.add(content)
                    }
                }
                pageToken = if (!stop) result.meta?.nextToken else null
            } while (pageToken != null)
            FileWriter(File("${rootFolder()}likes.txt")).use {
                it.write(list.joinToString("---\n"))
            }
            return list
        } catch (e: ApiException) {
            System.err.println("Status code: " + e.code)
            System.err.println("Reason: " + e.responseBody)
            System.err.println("Response headers: " + e.responseHeaders)
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    fun listFollowers(accessToken: String? = null): List<String> {
        accessToken?.let { TokenStore.accessToken = accessToken }
        val credentials = createTwitterCredentialsOAuth2()
        val apiInstance = TwitterApi(credentials)
        val userFields = setOf("username", "name")
        try {
            var pageToken: String? = null
            val list = mutableListOf<String>()
            do {
                val result =
                        apiInstance.users().listGetFollowers("83401212").paginationToken(pageToken).maxResults(100)
                                .userFields(userFields).execute()
                if (result.errors?.size?.let { it > 0 } == true) {
                    println("Error:")
                    result.errors!!.forEach { e ->
                        println(e.toString())
                        if (e is ResourceUnauthorizedProblem) {
                            println(e.title + " " + e.detail)
                        }
                    }
                } else {
                    for (user in result.data!!) {
                        list.add(user.username)
                    }
                }
                pageToken = result.meta?.nextToken
            } while (pageToken != null)
            FileWriter(File("${rootFolder()}followers.txt")).use {
                it.write(list.joinToString("---\n"))
            }
            return list
        } catch (e: ApiException) {
            System.err.println("Status code: " + e.code)
            System.err.println("Reason: " + e.responseBody)
            System.err.println("Response headers: " + e.responseHeaders)
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    fun listFollowing(accessToken: String? = null) {
        accessToken?.let { TokenStore.accessToken = accessToken }
        val credentials = createTwitterCredentialsOAuth2()
        val apiInstance = TwitterApi(credentials)
        val tweetFields = setOf("name")
        try {
            // findTweetById
            var pageToken: String? = null
            val list = mutableListOf<String>()
            do {
                val result =
                        apiInstance.users().usersIdFollowing("83401212").paginationToken(pageToken).maxResults(100)
                                .tweetFields(tweetFields).execute()
                if (result.errors?.size?.let { it > 0 } == true) {
                    println("Error:")
                    result.errors!!.forEach { e ->
                        println(e.toString())
                        if (e is ResourceUnauthorizedProblem) {
                            println(e.title + " " + e.detail)
                        }
                    }
                } else {
                    for (user in result.data!!) {
                        list.add(user.username)
                    }
                }
                pageToken = result.meta?.nextToken
            } while (pageToken != null)
            FileWriter(File("${rootFolder()}likes.txt")).use {
                it.write(list.joinToString("---\n"))
            }
        } catch (e: ApiException) {
            System.err.println("Status code: " + e.code)
            System.err.println("Reason: " + e.responseBody)
            System.err.println("Response headers: " + e.responseHeaders)
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rootFolder() = "/home/joerg/Schreibtisch"
}