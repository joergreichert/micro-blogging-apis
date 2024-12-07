package de.l.joergreichert.outintheopen.bluesky.to

import java.time.ZonedDateTime

// https://docs.bsky.app/docs/api/app-bsky-feed-get-actor-likes
@JvmRecord
data class Likes(
    val uri: String? = null,
    val cursor: String? = null,
    val feed: List<Feed>? = null
)