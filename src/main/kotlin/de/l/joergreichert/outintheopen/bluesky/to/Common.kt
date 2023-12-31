package de.l.joergreichert.outintheopen.bluesky.to

import java.time.ZonedDateTime

@JvmRecord
data class Viewer(
    val muted: Boolean,
    val blockedBy: Boolean,
    val following: String,
    val followedBy: String,
    val like: String
)

@JvmRecord
data class Label(
    val src: String,
    val uri: String,
    val `val`: String,
    val neg: Boolean,
    val cts: ZonedDateTime
)

@JvmRecord
data class Actor(
    val did: String,
    val handle: String,
    val displayName: String,
    val description: String,
    val avatar: String,
    val indexedAt: ZonedDateTime,
    val viewer: Viewer,
    val labels: List<Label>
)
