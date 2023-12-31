package de.l.joergreichert.outintheopen.bluesky.to

import java.time.ZonedDateTime

@JvmRecord
data class Likes(
    val uri: String? = null,
    val cursor: String? = null,
    val likes: List<Like>? = null
)

@JvmRecord
data class Like(
    val createdAt: ZonedDateTime? = null,
    val indexedAt: ZonedDateTime? = null,
    val actor: Actor? = null
)