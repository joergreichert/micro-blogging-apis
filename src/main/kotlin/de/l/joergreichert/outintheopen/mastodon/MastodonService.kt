package de.l.joergreichert.outintheopen.mastodon

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import de.l.joergreichert.outintheopen.config.AppProperties
import de.l.joergreichert.outintheopen.mastodon.to.MastodonAccount
import de.l.joergreichert.outintheopen.mastodon.to.MastodonStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.io.File
import java.io.FileWriter
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
class MastodonService @Autowired constructor(
    val objectMapper: ObjectMapper,
    val webClientBuilder: WebClient.Builder,
    val appProperties: AppProperties
) {

    fun getAppAccessToken(): Mono<String> {
        return webClientBuilder.build()
            .post().uri { uriBuilder ->
                uriBuilder
                    .scheme("https")
                    .host(appProperties.mastodon.website)
                    .path("/oauth/token")
                    .queryParam("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
                    .queryParam("grant_type", "client_credentials")
                    .queryParam("client_id", appProperties.mastodon.consumerKey)
                    .queryParam("client_secret", appProperties.mastodon.consumerSecret)
                    .build()
            }
            .retrieve()
            .bodyToMono(Map::class.java)
            .map { result ->
                val accessToken = result["access_token"].toString()
                MastodonTokenStore.accessToken = accessToken
                accessToken
            }
    }

    fun getCodeLoginUrl(): String =
        "https://${appProperties.mastodon.website}/oauth/authorize?redirect_uri=urn:ietf:wg:oauth:2.0:oob&client_id=${appProperties.mastodon.clientId}&response_type=code&scope=read"

    fun getUserAppAccessTokenForCode(code: String): String =
        "https://${appProperties.mastodon.website}/oauth/token?redirect_uri=urn:ietf:wg:oauth:2.0:oob&grant_type=authorization_code&client_id=${appProperties.mastodon.clientId}&client_secret=${appProperties.mastodon.clientSecret}&code=$code"

    private fun getAccessToken(givenAccessToken: String?): Mono<String> {
        givenAccessToken?.let { MastodonTokenStore.accessToken = givenAccessToken }
        return givenAccessToken?.let { Mono.just(givenAccessToken) } ?: getAppAccessToken()
    }

    fun listFollowers(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null
    ): Mono<List<String>> {
        val type: CollectionType = objectMapper.typeFactory.constructCollectionType(
            MutableList::class.java, MastodonAccount::class.java
        )
        val paramType: ParameterizedTypeReference<MutableList<MastodonAccount>> =
            ParameterizedTypeReference.forType(type)
        val processBody = { body: MutableList<MastodonAccount> ->
            val list = body.map { "${it.username} (${it.displayName})" }
            FileWriter(File(targetFile ?: "${rootFolder()}/mastodon-followers.txt")).use {
                it.write(list.joinToString("\n"))
            }
            list
        }
        return genericCall(
            pathSegment = "followers",
            returnType = paramType,
            processBody = processBody,
            givenAccessToken = givenAccessToken
        )
    }

    fun listFollowing(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null
    ): Mono<List<String>> {
        val type: CollectionType = objectMapper.typeFactory.constructCollectionType(
            MutableList::class.java, MastodonAccount::class.java
        )
        val paramType: ParameterizedTypeReference<MutableList<MastodonAccount>> =
            ParameterizedTypeReference.forType(type)
        val processBody = { body: MutableList<MastodonAccount> ->
            val list = body.map { "${it.username} (${it.displayName})" }
            FileWriter(File(targetFile ?: "${rootFolder()}/mastodon-following.txt")).use {
                it.write(list.joinToString("\n"))
            }
            list
        }
        return genericCall(
            pathSegment = "following",
            returnType = paramType,
            processBody = processBody,
            givenAccessToken = givenAccessToken
        )
    }

    fun listStatuses(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null,
        since: LocalDate?,
        until: LocalDate?
    ): Mono<List<String>> {
        return internalStatuses(
            givenAccessToken,
            "https://${appProperties.mastodon.website}/api/v1/accounts/${appProperties.mastodon.accountId}/statuses",
            mutableListOf(),
            since,
            until
        ).map { list ->
            FileWriter(File(targetFile ?: "${rootFolder()}/mastodon-statuses.txt")).use {
                it.write(list.joinToString("\n"))
            }
            list
        }
    }

    fun listBookmarks(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null,
        since: LocalDate?,
        until: LocalDate?,
    ): Mono<List<String>> {
        return internalStatuses(
            givenAccessToken,
            "https://${appProperties.mastodon.website}/api/v1/bookmarks",
            mutableListOf(),
            since,
            until
        ).map { list ->
            FileWriter(File(targetFile ?: "${rootFolder()}/mastodon-bookmarks.txt")).use {
                it.write(list.joinToString("\n"))
            }
            list
        }
    }

    private fun internalStatuses(
        givenAccessToken: String? = null,
        url: String,
        visited: MutableList<String>,
        since: LocalDate?,
        until: LocalDate?,
    ): Mono<List<String>> {
        val listOfLists = mutableListOf<String>()
        var currentSize = 0
        val type: CollectionType = objectMapper.typeFactory.constructCollectionType(
            MutableList::class.java, MastodonStatus::class.java
        )
        val paramType: ParameterizedTypeReference<MutableList<MastodonStatus>> =
            ParameterizedTypeReference.forType(type)
        return getAccessToken(givenAccessToken).flatMap { accessToken ->
            webClientBuilder.build().get().uri(url).headers { h -> h.setBearerAuth(accessToken) }.retrieve()
                .toEntity(paramType).map { response ->
                    val linkHeader = response.headers["Link"]
                    currentSize =
                        handleLink(linkHeader, url, visited, listOfLists, givenAccessToken, since, until, currentSize)
                    val res = handleBody(response, since, until, currentSize)
                    listOfLists.addAll(res)
                    listOfLists
                }.doOnError {
                    val type2: CollectionType = objectMapper.typeFactory.constructCollectionType(
                        MutableList::class.java, JsonNode::class.java
                    )
                    val paramType2: ParameterizedTypeReference<MutableList<JsonNode>> =
                        ParameterizedTypeReference.forType(type2)
                    webClientBuilder.build().get().uri(url).headers { h -> h.setBearerAuth(accessToken) }.retrieve()
                        .toEntity(paramType2).map { response ->
                            val linkHeader = response.headers["Link"]
                            currentSize =
                                handleLink(linkHeader, url, visited, listOfLists, givenAccessToken, since, until, currentSize)
                            val res = handleBody2(response, since, until, currentSize, url)
                            listOfLists.addAll(res)
                            listOfLists
                        }
                }
        }
    }

    private fun handleBody(
        response: ResponseEntity<MutableList<MastodonStatus>>,
        since: LocalDate?,
        until: LocalDate?,
        currentSize: Int
    ): List<String> {
        val decimalFormat = DecimalFormat("000")
        val simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val list = response.body?.reversed()?.mapIndexed { index, tweet ->
            val createdAt = LocalDateTime.from(simpleDateFormat.parse(tweet.createdAt.toString()))
            if ((since == null || createdAt.toLocalDate().isAfter(since)) &&
                (until == null || createdAt.toLocalDate().isBefore(until))
            ) {
                listOfNotNull(
                    "${decimalFormat.format(currentSize + index + 1)}. ${tweet.createdAt}: ${tweet.content}",
                    tweet.reblog?.content ?: tweet.content,
                    tweet.reblog?.uri ?: tweet.uri
                ).joinToString("\n")
            } else null
        }?.filterNotNull() ?: emptyList()
        println(list)
        return list
    }

    private fun handleBody2(
        response: ResponseEntity<MutableList<JsonNode>>,
        since: LocalDate?,
        until: LocalDate?,
        currentSize: Int,
        url: String
    ): List<String> {
        val decimalFormat = DecimalFormat("000")
        val simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val list = response.body?.reversed()?.mapIndexed { index, tweet ->
            try {
                val tweetCasted = objectMapper.readValue(tweet.asText(), MastodonStatus::class.java)
                val createdAt = LocalDateTime.from(simpleDateFormat.parse(tweetCasted.createdAt.toString()))
                if ((since == null || createdAt.toLocalDate().isAfter(since)) &&
                    (until == null || createdAt.toLocalDate().isBefore(until))
                ) {
                    listOfNotNull(
                        "${decimalFormat.format(currentSize + index + 1)}. ${tweetCasted.createdAt}: ${tweetCasted.content}",
                        tweetCasted.reblog?.content ?: tweetCasted.content,
                        tweetCasted.reblog?.uri ?: tweetCasted.uri
                    ).joinToString("\n")
                } else null
            } catch (e3: Exception) {
                println("Parse error for " + url + ": " + e3.message)
                null
            }
        }?.filterNotNull() ?: emptyList()
        return list
    }

    private fun handleLink(
        linkHeader: MutableList<String>?,
        url: String,
        visited: MutableList<String>,
        listOfLists: MutableList<String>,
        givenAccessToken: String?,
        since: LocalDate?,
        until: LocalDate?,
        currentSize: Int
    ): Int {
        var currentSize1 = currentSize
        if (linkHeader != null) {
            val parts = linkHeader[0].split(";")
            if (parts.isNotEmpty()) {
                val prevUrl = parts[0].replace("<", "").replace(">", "")
                if (prevUrl != url && !visited.contains(prevUrl)) {
                    visited.add(url)
                    try {
                        internalStatuses(givenAccessToken, prevUrl, visited, since, until).map { list ->
                            currentSize1 += list.size; listOfLists.addAll(list); println(list); list
                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            }
        }
        return currentSize1
    }

    fun listLikes(
        givenAccessToken: String? = null, userId: String? = null, targetFile: String? = null,
        since: LocalDate?, until: LocalDate?
    ): Mono<List<String>> {
        return internalStatuses(
            givenAccessToken,
            "https://${appProperties.mastodon.website}/api/v1/favourites?limit=100",
            mutableListOf(),
            since,
            until
        ).map { list ->
            FileWriter(File(targetFile ?: "${rootFolder()}/mastodon-likes.txt")).use {
                it.write(list.joinToString("\n"))
            }
            list
        }
    }

    private fun <T> genericCall(
        pathSegment: String,
        returnType: ParameterizedTypeReference<MutableList<T>>,
        processBody: (MutableList<T>) -> List<String>,
        givenAccessToken: String? = null
    ): Mono<List<String>> {
        return getAccessToken(givenAccessToken).flatMap { accessToken ->
            webClientBuilder.build().get().uri(
                "https://${appProperties.mastodon.website}/api/v1/accounts/${appProperties.mastodon.accountId}/$pathSegment"
            ).headers { h -> h.setBearerAuth(accessToken) }.retrieve()
                .bodyToMono(returnType).map { body -> processBody.invoke(body) }
        }
    }

    private fun rootFolder() = "/tmp"
}