package de.l.joergreichert.outintheopen.bluesky

import com.fasterxml.jackson.databind.ObjectMapper
import de.l.joergreichert.outintheopen.bluesky.to.*
import de.l.joergreichert.outintheopen.config.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import org.springframework.web.reactive.function.client.WebClientResponseException.BadRequest
import reactor.core.publisher.Mono
import java.io.File
import java.io.FileWriter
import java.time.LocalDate
import java.util.function.Consumer


@Service
class BlueskyService @Autowired constructor(
    val webClientBuilder: WebClient.Builder,
    val objectMapper: ObjectMapper,
    val appProperties: AppProperties
) {

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
            cursor = cursor,
            since = null
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
            cursor = cursor,
            since = null
        )
    }

    fun listStatuses(
        givenAccessToken: String? = null,
        userId: String? = null,
        targetFile: String? = null,
        limit: Int? = 50,
        cursor: String? = null,
        since: LocalDate?
    ): Mono<Feeds?> {
        val processBody: Consumer<Feeds> = Consumer { body: Feeds ->
            FileWriter(File(targetFile ?: "${rootFolder()}/bluesky-feeds.txt")).use { fw ->
                body.feed?.let { fw.write(body.feed.joinToString("\n")) }
            }
        }
        return genericCall(
            pathSegment = "app.bsky.feed.getAuthorFeed",
            returnType = Feeds::class.java,
            processBody = processBody,
            givenAccessToken = givenAccessToken,
            userId = userId,
            limit = limit,
            cursor = cursor,
            since = null
        )
    }

    // https://docs.bsky.app/docs/api/app-bsky-feed-get-actor-likes
    fun listLikes(
        givenAccessToken: String? = null,
        userId: String? = null, targetFile: String? = null,
        limit: Int? = 50,
        cursor: String? = null,
        since: LocalDate?
    ): Mono<Likes?> {
        val processBody: Consumer<Likes> = Consumer { body: Likes ->
            FileWriter(File(targetFile ?: "${rootFolder()}/bluesky-likes.txt")).use { fw ->
                body.feed?.let { fw.write(body.feed.joinToString("\n")) }
            }
        }
        return genericCall(
            pathSegment = "app.bsky.feed.getActorLikes",
            returnType = Likes::class.java,
            processBody = processBody,
            givenAccessToken = givenAccessToken,
            userId = userId,
            limit = limit,
            cursor = cursor,
            since = null
        )
    }

    private fun <T> genericCall(
        pathSegment: String,
        returnType: Class<T>,
        processBody: Consumer<T>,
        givenAccessToken: String? = null,
        userId: String? = null,
        limit: Int? = 50,
        cursor: String? = null,
        since: LocalDate?,
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

    private fun rootFolder() = "/tmp"
}

data class AuthRequest(
    val identifier: String,
    val password: String
)