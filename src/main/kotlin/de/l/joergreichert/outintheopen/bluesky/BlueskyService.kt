package de.l.joergreichert.outintheopen.bluesky

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import de.l.joergreichert.outintheopen.bluesky.to.*
import de.l.joergreichert.outintheopen.config.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import org.springframework.web.reactive.function.client.WebClientResponseException.BadRequest
import reactor.core.publisher.Mono
import java.io.File
import java.io.FileWriter
import java.net.URI
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Consumer


@Service
class BlueskyService @Autowired constructor(
    val webClientBuilder: WebClient.Builder,
    val objectMapper: ObjectMapper,
    val appProperties: AppProperties
) {

    fun getUserId(username: String): Mono<String> {
        val name = if (username.endsWith(".bsky.social")) username else "$username.bsky.social"
        return webClientBuilder.build().get().uri(
            "https://bsky.social/xrpc/com.atproto.identity.resolveHandle?handle=${name}"
        ).retrieve().bodyToMono(String::class.java)
    }

    fun getAppAccessToken(): Mono<String> {
        val body = objectMapper.writeValueAsString(
            appProperties.bluesky.let { AuthRequest(identifier = it.accountId, password = it.password) }
        )
        return webClientBuilder.build()
            .post().uri("https://bsky.social/xrpc/com.atproto.server.createSession")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map::class.java)
            .map { result ->
                val accessToken = result["accessJwt"].toString()
                val refreshToken = result["refreshJwt"].toString()
                BlueskyTokenStore.accessToken = accessToken
                BlueskyTokenStore.refreshToken = refreshToken
                accessToken
            }
    }

    private fun getAccessToken(givenAccessToken: String?): Mono<String> {
        givenAccessToken?.let { BlueskyTokenStore.accessToken = givenAccessToken }
        return givenAccessToken?.let { Mono.just(givenAccessToken) } ?: getAppAccessToken()
    }

    fun getProfile(givenAccessToken: String? = null, userId: String? = null): Mono<Profile>? {
        return getAccessToken(givenAccessToken).flatMap { accessToken ->
            val actor = userId ?: appProperties.bluesky.accountId
            val url = "https://bsky.social/xrpc/app.bsky.actor.getProfile?actor=${actor}"
            webClientBuilder
                .filter(ExchangeFilterFunction.ofRequestProcessor {
                    clientRequest -> println(clientRequest.url());
                    clientRequest.headers().forEach { t, u -> println("t: $t, u: $u") };
                    Mono.just(clientRequest)
                })
                .build().get().uri(url)
                .headers { h -> h.setBearerAuth(accessToken) }
                .retrieve()
                .bodyToMono(Profile::class.java)
                .doOnError { error -> println("error: " + error.message) }
        }
    }

    fun listFollowers(
        givenAccessToken: String? = null,
        userId: String? = null,
        limit: Int? = 100,
        cursor: String? = null,
        targetFile: String? = null
    ): Mono<Followers?> {
        val processBody: Consumer<Followers> = Consumer { body: Followers ->
            FileWriter(File(targetFile ?: "${rootFolder()}/bluesky-followers.txt")).use { fw ->
                body.followers?.let { fw.write(body.followers.joinToString("\n")) }
            }
        }
        return genericCall(
            pathSegment = "app.bsky.graph.getFollowers",
            returnType = Followers::class.java,
            processBody = processBody,
            givenAccessToken = givenAccessToken,
            userId = userId,
            limit = limit,
            cursor = cursor
        )
    }

    fun listFollowing(
        givenAccessToken: String? = null,
        userId: String? = null,
        limit: Int? = 100,
        cursor: String? = null,
        targetFile: String? = null
    ): Mono<Follows?> {
        val processBody: Consumer<Follows> = Consumer { body: Follows ->
            FileWriter(File(targetFile ?: "${rootFolder()}/bluesky-following.txt")).use { fw ->
                body.follows?.let { fw.write(body.follows.joinToString("\n")) }
            }
        }
        return genericCall(
            pathSegment = "app.bsky.graph.getFollows",
            returnType = Follows::class.java,
            processBody = processBody,
            givenAccessToken = givenAccessToken,
            userId = userId,
            limit = limit,
            cursor = cursor
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
            userId,
            givenAccessToken,
            "app.bsky.feed.getAuthorFeed",
            null,
            mutableListOf(),
            since,
            until
        ).map { list ->
            FileWriter(File(targetFile ?: "${rootFolder()}/bluesky-statuses.txt")).use {
                it.write(list.joinToString("\n\n"))
            }
            list
        }
    }

    // https://docs.bsky.app/docs/api/app-bsky-feed-get-actor-likes
    fun listLikes(
        givenAccessToken: String? = null, userId: String? = null, targetFile: String? = null,
        since: LocalDate?, until: LocalDate?
    ): Mono<List<String>> {
        return internalStatuses(
            userId,
            givenAccessToken,
            "app.bsky.feed.getActorLikes",
            null,
            mutableListOf(),
            since,
            until
        ).map { list ->
            FileWriter(File(targetFile ?: "${rootFolder()}/bluesky-likes.txt")).use {
                it.write(list.joinToString("\n\n"))
            }
            list
        }
    }

    // https://docs.bsky.app/docs/api/app-bsky-feed-getBookmarked
    fun listBookmarks(
        givenAccessToken: String? = null, userId: String? = null, targetFile: String? = null,
        since: LocalDate?, until: LocalDate?
    ): Mono<List<String>> {
        return internalStatuses(
            userId,
            givenAccessToken,
            "app.bsky.feed.getBookmarked",
            null,
            mutableListOf(),
            since,
            until
        ).map { list ->
            FileWriter(File(targetFile ?: "${rootFolder()}/bluesky-bookmarks.txt")).use {
                it.write(list.joinToString("\n\n"))
            }
            list
        }
    }

    private fun <T> genericCall(
        pathSegment: String,
        returnType: Class<T>,
        processBody: Consumer<T>,
        givenAccessToken: String? = null,
        userId: String? = null,
        limit: Int? = 50,
        cursor: String? = null
    ): Mono<T?> {
        return getAccessToken(givenAccessToken).flatMap { accessToken ->
            webClientBuilder
                .filter(ExchangeFilterFunction.ofRequestProcessor {
                        clientRequest -> println(clientRequest.url());
                    clientRequest.headers().forEach { t, u -> println("t: $t, u: $u") };
                    try {
                        Mono.just(clientRequest)
                    } catch (e: Exception) {
                        Mono.error(e)
                    }
                })
                .build().get().uri { uriBuilder ->
                uriBuilder
                    .scheme("https")
                    .host("bsky.social")
                    .path("/xrpc/$pathSegment")
                    .queryParam("actor", (userId ?: appProperties.bluesky.accountId))
                    .queryParam("limit", limit).let {
                        if (cursor != null) it.queryParam("cursor", cursor) else it
                    }
                    .build()
            }
                .headers { h -> h.setBearerAuth(accessToken) }
                .retrieve()

                .bodyToMono(returnType).map { body ->
                    body?.let {
                        processBody.accept(body)
                    }
                    body
                }
                .onErrorResume { e -> Mono.error(IllegalStateException("request failed: " +
                        (e as BadRequest).getResponseBodyAsString(
                    java.nio.charset.Charset.forName("UTF-8")), e)) }
        }
    }

    private fun internalStatuses(
        userId: String? = null,
        givenAccessToken: String? = null,
        pathSegment: String,
        cursor: String?,
        visited: MutableList<String>,
        since: LocalDate?,
        until: LocalDate?,
    ): Mono<List<String>> {
        return getAccessToken(givenAccessToken).flatMap { accessToken ->
            webClientBuilder.build().get().uri { uriBuilder ->
                uriBuilder
                    .scheme("https")
                    .host("bsky.social")
                    .path("/xrpc/$pathSegment")
                    .queryParam("actor", (userId ?: appProperties.bluesky.accountId))
                    .queryParam("cursor", cursor)
                    .queryParam("limit", 50)
                    .build()
            }
                .headers { h -> h.setBearerAuth(accessToken) }
                .retrieve()
                .toEntity(Likes::class.java).flatMap { response ->
                    val cursor = response.body.cursor
                    if (response.body.feed?.isNotEmpty() == true) {
                        handleLink((userId ?: appProperties.bluesky.accountId), cursor, pathSegment, visited, givenAccessToken, since, until).map { processed ->
                            val listOfLists = mutableListOf<String>()
                            listOfLists.addAll(processed)
                            val res = handleBody(response, since, until, listOfLists.size)
                            listOfLists.addAll(res)
                            listOfLists
                        }
                    } else {
                        Mono.just(emptyList<String>())
                    }
                }.doOnError {
                    val type2: CollectionType = objectMapper.typeFactory.constructCollectionType(
                        MutableList::class.java, JsonNode::class.java
                    )
                    val paramType2: ParameterizedTypeReference<MutableList<JsonNode>> =
                        ParameterizedTypeReference.forType(type2)
                    webClientBuilder.build().get().uri { uriBuilder ->
                        uriBuilder
                            .scheme("https")
                            .host("bsky.social")
                            .path("/xrpc/$pathSegment")
                            .queryParam("actor", (userId ?: appProperties.bluesky.accountId))
                            .queryParam("limit", 50)
                            .build()
                    }.headers { h -> h.setBearerAuth(accessToken) }.retrieve()
                        .toEntity(paramType2).flatMap { response ->
                            val cursor = response.body.filter { it.get("cursor") != null }.map { it.get("cursor").asText() }.firstOrNull()
                            if (response.body.filter { it.get("feeds") != null }.map { it.get("feeds") }.firstOrNull() !== null) {
                                handleLink((userId ?: appProperties.bluesky.accountId), cursor, pathSegment, visited, givenAccessToken, since, until).map { processed ->
                                    val listOfLists = mutableListOf<String>()
                                    listOfLists.addAll(processed)
                                    val res = handleBody2(response, since, until, listOfLists.size, pathSegment)
                                    listOfLists.addAll(res)
                                    listOfLists
                                }
                            } else {
                                Mono.just(emptyList<String>())
                            }
                        }
                }
        }
    }

    private fun handleBody(
        response: ResponseEntity<Likes>,
        since: LocalDate?,
        until: LocalDate?,
        currentSize: Int
    ): List<String> {
        val decimalFormat = DecimalFormat("000")
        val simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return response.body?.feed?.reversed()?.mapIndexed { index, tweet ->
            val createdAt = try {
                LocalDateTime.from(simpleDateFormat.parse(tweet.post?.record?.createdAt.toString()))
            } catch (_: Exception) {
                null
            }
            if (createdAt != null && ((since == null || createdAt.toLocalDate().isAfter(since)) &&
                (until == null || createdAt.toLocalDate().isBefore(until)))
            ) {
                listOfNotNull(
                    "${decimalFormat.format(currentSize + index + 1)}. ${tweet.post?.record?.createdAt}: ${tweet.post?.record?.text}",
                    tweet.post?.record?.text,
                    "https://bsky.app/profile/${tweet.post?.author?.handle}/post/${URI.create(tweet.post?.uri.toString()).path.split("/").last()}"
                ).joinToString("\n")
            } else null
        }?.filterNotNull() ?: emptyList()
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
        return response.body?.reversed()?.mapIndexed { index, tweet ->
            try {
                val tweetCasted = objectMapper.readValue(tweet.asText(), Feed::class.java)
                val createdAt = LocalDateTime.from(simpleDateFormat.parse(tweetCasted.post?.record?.createdAt.toString()))
                if ((since == null || createdAt.toLocalDate().isAfter(since)) &&
                    (until == null || createdAt.toLocalDate().isBefore(until))
                ) {
                    listOfNotNull(
                        "${decimalFormat.format(currentSize + index + 1)}. ${tweetCasted.post?.record?.createdAt}: ${tweetCasted.post?.record?.text}",
                        tweetCasted.post?.record?.text,
                        "https://bsky.app/profile/${tweetCasted.post?.author?.handle}/post/${URI.create(tweetCasted.post?.uri.toString()).path.split("/").last()}"
                    ).joinToString("\n")
                } else null
            } catch (e3: Exception) {
                println("Parse error for " + url + ": " + e3.message)
                null
            }
        }?.filterNotNull() ?: emptyList()
    }

    private fun handleLink(
        userId: String,
        cursor: String?,
        url: String,
        visited: MutableList<String>,
        givenAccessToken: String?,
        since: LocalDate?,
        until: LocalDate?,
    ): Mono<List<String>> {
        if (cursor != null) {
            return internalStatuses(userId, givenAccessToken, url, cursor, visited, since, until)
        }
        return Mono.just(emptyList())
    }

    private fun rootFolder() = "/tmp"
}

data class AuthRequest(
    val identifier: String,
    val password: String
)