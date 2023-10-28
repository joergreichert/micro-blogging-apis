package de.l.joergreichert.outintheopen.mastodon

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import de.l.joergreichert.outintheopen.config.AppProperties
import de.l.joergreichert.outintheopen.mastodon.to.MastodonAccount
import de.l.joergreichert.outintheopen.mastodon.to.MastodonStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
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
import java.time.format.DateTimeFormatter


@Service
class MastodonService @Autowired constructor(
    val objectMapper: ObjectMapper,
    val restTemplate: RestTemplate,
    val appProperties: AppProperties
) {

    fun getAppAccessToken(): String {
        val headers = HttpHeaders()
        headers.setBasicAuth(appProperties.mastodon.consumerKey, appProperties.mastodon.consumerSecret)
        val request = HttpEntity<String>(headers)
        val response = restTemplate.exchange(
            "https://${appProperties.mastodon.website}/oauth/token?redirect_uri=urn:ietf:wg:oauth:2.0:oob&grant_type=client_credentials&client_id=${appProperties.mastodon.consumerKey}&client_secret=${appProperties.mastodon.consumerSecret}",
            HttpMethod.POST, request, Map::class.java
        )
        val accessToken = response.body["access_token"].toString()
        MastodonTokenStore.accessToken = accessToken
        return accessToken
    }

    fun getCodeLoginUrl(): String =
        "https://${appProperties.mastodon.website}/oauth/authorize?redirect_uri=urn:ietf:wg:oauth:2.0:oob&client_id=${appProperties.mastodon.clientId}&response_type=code&scope=read"

    fun getUserAppAccessTokenForCode(code: String): String =
        "https://${appProperties.mastodon.website}/oauth/token?redirect_uri=urn:ietf:wg:oauth:2.0:oob&grant_type=authorization_code&client_id=${appProperties.mastodon.clientId}&client_secret=${appProperties.mastodon.clientSecret}&code=$code"

    private fun getAccessToken(givenAccessToken: String?): String {
        givenAccessToken?.let { MastodonTokenStore.accessToken = givenAccessToken }
        return MastodonTokenStore.accessToken ?: getAppAccessToken().let { MastodonTokenStore.accessToken = it; it }
    }

    fun listFollowers(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null
    ): List<String> {
        val headers = HttpHeaders()
        headers.setBearerAuth(getAccessToken(givenAccessToken))
        val request = HttpEntity<String>(headers)
        val type: CollectionType = objectMapper.typeFactory.constructCollectionType(
            MutableList::class.java, MastodonAccount::class.java
        )
        val paramType: ParameterizedTypeReference<MutableList<MastodonAccount>> =
            ParameterizedTypeReference.forType(type)
        val response = restTemplate.exchange(
            "https://${appProperties.mastodon.website}/api/v1/accounts/${appProperties.mastodon.accountId}/followers",
            HttpMethod.GET, request, paramType
        )
        val list = response.body?.map { "${it.username} (${it.displayName})" } ?: emptyList()
        FileWriter(File(targetFile ?: "${rootFolder()}mastodon-followers.txt")).use {
            it.write(list.joinToString("\n"))
        }
        return list
    }

    fun listFollowing(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null
    ): List<String> {
        val headers = HttpHeaders()
        headers.setBearerAuth(getAccessToken(givenAccessToken))
        val request = HttpEntity<String>(headers)
        val type: CollectionType = objectMapper.typeFactory.constructCollectionType(
            MutableList::class.java, MastodonAccount::class.java
        )
        val paramType: ParameterizedTypeReference<MutableList<MastodonAccount>> =
            ParameterizedTypeReference.forType(type)
        val response = restTemplate.exchange(
            "https://${appProperties.mastodon.website}/api/v1/accounts/${appProperties.mastodon.accountId}/following",
            HttpMethod.GET, request, paramType
        )
        val list = response.body?.map { "${it.username} (${it.displayName})" } ?: emptyList()
        FileWriter(File(targetFile ?: "${rootFolder()}mastodon-following.txt")).use {
            it.write(list.joinToString("\n"))
        }
        return list
    }

    fun listStatuses(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null,
        since: LocalDate?
    ): List<String> {
        val accessToken = getAccessToken(givenAccessToken)
        val list = internalStatuses(
            "https://${appProperties.mastodon.website}/api/v1/accounts/${appProperties.mastodon.accountId}/statuses",
            accessToken,
            mutableListOf(),
            since
        )
        FileWriter(File(targetFile ?: "${rootFolder()}mastodon-statuses.txt")).use {
            it.write(list.joinToString("\n"))
        }
        return list
    }

    fun listBookmarks(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null,
        since: LocalDate?
    ): List<String> {
        val accessToken = getAccessToken(givenAccessToken)
        val list = internalStatuses(
            "https://${appProperties.mastodon.website}/api/v1/bookmarks",
            accessToken,
            mutableListOf(),
            since
        )
        FileWriter(File(targetFile ?: "${rootFolder()}mastodon-bookmarks.txt")).use {
            it.write(list.joinToString("\n"))
        }
        return list
    }

    private fun internalStatuses(
        url: String,
        accessToken: String,
        visited: MutableList<String>,
        since: LocalDate?
    ): List<String> {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val request = HttpEntity<String>(headers)
        val type: CollectionType = objectMapper.typeFactory.constructCollectionType(
            MutableList::class.java, MastodonStatus::class.java
        )
        val paramType: ParameterizedTypeReference<MutableList<MastodonStatus>> =
            ParameterizedTypeReference.forType(type)
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET, request, paramType
        )
        val linkHeader = response.headers["Link"]
        val joinedList = mutableListOf<String>()
        if (linkHeader != null) {
            val parts = linkHeader[0].split(";")
            if (parts.isNotEmpty()) {
                val prevUrl = parts[0].replace("<", "").replace(">", "")
                if (prevUrl != url && !visited.contains(prevUrl)) {
                    visited.add(url)
                    try {
                        joinedList.addAll(internalStatuses(prevUrl, accessToken, visited, since))
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            }
        }
        val decimalFormat = DecimalFormat("000")
        val simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val list = response.body?.reversed()?.mapIndexed { index, tweet ->
            val createdAt = LocalDateTime.from(simpleDateFormat.parse(tweet.createdAt))
            if (since == null || createdAt.toLocalDate().isAfter(since)) {
                listOfNotNull(
                    "${decimalFormat.format(joinedList.size + index + 1)}. ${tweet.createdAt}: ${tweet.content}",
                    tweet.reblog?.content ?: tweet.content,
                    tweet.reblog?.uri ?: tweet.uri
                ).joinToString("\n")
            } else null
        }?.filterNotNull() ?: emptyList()
        joinedList.addAll(list)
        return joinedList
    }

    fun listLikes(
        givenAccessToken: String? = null, userId: String? = null, targetFile: String? = null,
        since: LocalDate?
    ): List<String> {
        val accessToken = getAccessToken(givenAccessToken)
        val list = internalStatuses(
            "https://${appProperties.mastodon.website}/api/v1/favourites?limit=100",
            accessToken,
            mutableListOf(),
            since
        )
        FileWriter(File(targetFile ?: "${rootFolder()}mastodon-likes.txt")).use {
            it.write(list.joinToString("\n"))
        }
        return list
    }

    private fun rootFolder() = "/tmp"
}