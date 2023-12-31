package de.l.joergreichert.outintheopen.bluesky.to

import java.time.ZonedDateTime

@JvmRecord
data class Likes(
    val uri: String,
    val cursor: String,
    val likes: List<Like>
)

@JvmRecord
data class Like(
    val createdAt: ZonedDateTime,
    val indexedAt: ZonedDateTime,
    val actor: Actor
)