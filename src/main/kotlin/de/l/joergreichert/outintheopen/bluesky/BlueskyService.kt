package de.l.joergreichert.outintheopen.bluesky

import de.l.joergreichert.outintheopen.bluesky.to.*
import de.l.joergreichert.outintheopen.config.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileWriter
import java.time.LocalDate


@Service
class BlueskyService @Autowired constructor(
    val restTemplate: RestTemplate,
    val appProperties: AppProperties
) {


    fun getProfile(userId: String? = null): Profile? {
        val map = mapOf(
            "actor" to (userId ?: appProperties.bluesky.accountId),
        )
        val url = "https://api.bsky.app/xrpc/app.bsky.actor.getProfile?actor={actor}"
        val response = restTemplate.getForEntity(url, Profile::class.java, map)
        return response.body
    }

    fun listFollowers(
        userId: String? = null,
        limit: Int? = 100,
        cursor: String? = null,
        targetFile: String? = null
    ): Followers? {
        val map = mapOf(
            "actor" to (userId ?: appProperties.bluesky.accountId),
            "limit" to limit,
            "cursor" to cursor
        )
        val url = "https://api.bsky.app/xrpc/app.bsky.graph.getFollowers?actor={actor}&limit={limit}&cursor={cursor}"
        val response = restTemplate.getForEntity(url, Followers::class.java, map)
        val body = response.body
        body?.let {
            FileWriter(File(targetFile ?: "${rootFolder()}bluesky-followers.txt")).use { fw ->
                body.followers?.let { fw.write(body.followers.joinToString("\n")) }
            }
        }
        return body
    }

    fun listFollowing(
        userId: String? = null,
        limit: Int? = 100,
        cursor: String? = null,
        targetFile: String? = null
    ): Follows? {
        val map = mapOf(
            "actor" to (userId ?: appProperties.bluesky.accountId),
            "limit" to limit,
            "cursor" to cursor
        )
        val url = "https://api.bsky.app/xrpc/app.bsky.graph.getFollows?actor={actor}&limit={limit}&cursor={cursor}"
        val response = restTemplate.getForEntity(url, Follows::class.java, map)
        val body = response.body
        body?.let {
            FileWriter(File(targetFile ?: "${rootFolder()}bluesky-following.txt")).use { fw ->
                body.follows?.let { fw.write(it.joinToString("\n")) }
            }
        }
        return body
    }

    fun listStatuses(
        userId: String? = null,
        targetFile: String? = null,
        limit: Int? = 50,
        cursor: String? = null,
        since: LocalDate?
    ): Feeds? {
        val map = mapOf(
            "actor" to (userId ?: appProperties.bluesky.accountId),
            "limit" to limit,
            "cursor" to cursor
        )
        val url = "https://api.bsky.app/xrpc/app.bsky.feed.getAuthorFeed?actor={actor}&limit={limit}&cursor={cursor}"
        val response = restTemplate.getForEntity(url, Feeds::class.java, map)
        val body = response.body
        body?.let {
            FileWriter(File(targetFile ?: "${rootFolder()}bluesky-feeds.txt")).use { fw ->
                body.feed?.let { fw.write(body.feed.joinToString("\n")) }
            }
        }
        return body
    }

    fun listLikes(
        userId: String? = null, targetFile: String? = null,
        limit: Int? = 50,
        cursor: String? = null,
        since: LocalDate?
    ): Likes? {
        val map = mapOf(
            "actor" to (userId ?: appProperties.bluesky.accountId),
            "limit" to limit,
            "cursor" to cursor
        )
        val url = "https://api.bsky.app/xrpc/app.bsky.feed.getActorLikes?actor={actor}&limit={limit}&cursor={cursor}"
        val response = restTemplate.getForEntity(url, Likes::class.java, map)
        val body = response.body
        body?.let {
            FileWriter(File(targetFile ?: "${rootFolder()}bluesky-likes.txt")).use {fw ->
                body.likes?.let { fw.write(body.likes.joinToString("\n")) }
            }
        }
        return body
    }

    private fun rootFolder() = "/tmp"
}