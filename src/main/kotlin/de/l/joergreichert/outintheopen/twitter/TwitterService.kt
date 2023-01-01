package de.l.joergreichert.outintheopen.twitter

import com.twitter.clientlib.ApiException
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.model.ResourceUnauthorizedProblem
import de.l.joergreichert.outintheopen.config.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileWriter
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
class TwitterService @Autowired constructor(val restTemplate: RestTemplate, val appProperties: AppProperties) {

    fun getAppAccessToken(): String {
        val headers = HttpHeaders()
        headers.setBasicAuth(appProperties.twitter.consumerKey, appProperties.twitter.consumerSecret)
        val request = HttpEntity<String>(headers)
        val response = restTemplate.exchange(
            "https://api.twitter.com/oauth2/token?grant_type=client_credentials",
            HttpMethod.POST, request, Map::class.java
        )
        val accessToken = response.body["access_token"].toString()
        TwitterTokenStore.accessToken = accessToken
        return accessToken
    }

    private fun getAccessToken(givenAccessToken: String?): String {
        givenAccessToken?.let { TwitterTokenStore.accessToken = givenAccessToken }
        return TwitterTokenStore.accessToken ?: getAppAccessToken().let { TwitterTokenStore.accessToken = it; it }
    }

    private fun createTwitterCredentialsOAuth2(givenAccessToken: String?) = TwitterCredentialsOAuth2(
        appProperties.twitter.clientId,
        appProperties.twitter.clientSecret,
        getAccessToken(givenAccessToken),
        TwitterTokenStore.refreshToken,
    )

    fun listLikes(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null,
        since: LocalDate?
    ): List<String> {
        val credentials = createTwitterCredentialsOAuth2(givenAccessToken)
        val apiInstance = TwitterApi(credentials)
        val tweetFields = setOf("author_id", "id", "created_at")
        try {
            // findTweetById
            var pageToken: String? = null
            val maxDate = OffsetDateTime.of(LocalDateTime.of(2022, 12, 1, 0, 0, 0), ZoneOffset.ofHours(1))
            val list = mutableListOf<String>()
            do {
                var stop = false
                val result =
                    apiInstance.tweets().usersIdLikedTweets(userId ?: appProperties.twitter.accountId)
                        .paginationToken(pageToken).maxResults(100)
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
                    try {
                        for ((index, tweet) in result.data!!
                            .sortedWith { a, b -> -(a.createdAt?.compareTo(b.createdAt) ?: 0) }
                            .filter { it.createdAt?.isAfter(maxDate) ?: true }.withIndex()) {
                            try {
                                val author =
                                    apiInstance.users().findUserById(tweet.authorId)
                                        .userFields(setOf("username", "name"))
                                        .execute()
                                Thread.sleep(2000)
                                val authorName = author.data?.username
                                val content = listOfNotNull(
                                    "${decimalFormat.format(index)}. ${simpleDateFormat.format(tweet.createdAt)}}: ${tweet.text}",
                                    tweet.entities?.toJson(),
                                    "https://twitter.com/${authorName}/status/${tweet.id}"
                                ).joinToString("\n")
                                list.add(content)
                            } catch (e: Exception) {
                                println("index: $index" + e.message)
                            }
                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
                pageToken = if (!stop) result.meta?.nextToken else null
            } while (pageToken != null)
            FileWriter(File(targetFile ?: "${rootFolder()}likes.txt")).use {
                it.write(list.joinToString("\n---\n\n"))
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

    fun listFollowers(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null
    ): List<String> {
        val credentials = createTwitterCredentialsOAuth2(givenAccessToken)
        val apiInstance = TwitterApi(credentials)
        val userFields = setOf("username", "name")
        try {
            var pageToken: String? = null
            val list = mutableListOf<String>()
            do {
                val result =
                    apiInstance.users().usersIdFollowers(userId ?: appProperties.twitter.accountId)
                        .paginationToken(pageToken).maxResults(100)
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
                        list.add("${user.username} (${user.name})")
                    }
                }
                pageToken = result.meta?.nextToken
            } while (pageToken != null)
            FileWriter(File(targetFile ?: "${rootFolder()}followers.txt")).use {
                it.write(list.joinToString("\n"))
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

    fun listFollowing(givenAccessToken: String? = null, userId: String? = null, targetFile: String? = null) {
        val credentials = createTwitterCredentialsOAuth2(givenAccessToken)
        val apiInstance = TwitterApi(credentials)
        val userFields = setOf("username", "name")
        try {
            // findTweetById
            var pageToken: String? = null
            val list = mutableListOf<String>()
            do {
                val result =
                    apiInstance.users().usersIdFollowing(userId ?: appProperties.twitter.accountId)
                        .paginationToken(pageToken).maxResults(100)
                        .userFields(userFields).execute()
                Thread.sleep(1000)
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
                        list.add("${user.username} (${user.name})")
                    }
                }
                pageToken = result.meta?.nextToken
            } while (pageToken != null)
            FileWriter(File(targetFile ?: "${rootFolder()}following.txt")).use {
                it.write(list.joinToString("\n"))
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

    private fun rootFolder() = "/tmp"
}