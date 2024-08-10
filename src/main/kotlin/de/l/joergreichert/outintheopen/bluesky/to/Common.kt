package de.l.joergreichert.outintheopen.bluesky.to

import java.time.ZonedDateTime

@JvmRecord
data class Viewer(
    val muted: Boolean,
    val blockedBy: Boolean,
    val following: String? = null,
    val followedBy: String? = null,
    val like: String? = null
)

@JvmRecord
data class Label(
    val src: String? = null,
    val uri: String? = null,
    val `val`: String? = null,
    val neg: Boolean? = null,
    val cts: ZonedDateTime? = null,
    val cid: String? = null,
)

@JvmRecord
data class Actor(
    val did: String? = null,
    val handle: String? = null,
    val displayName: String? = null,
    val description: String? = null,
    val avatar: String? = null,
    val indexedAt: ZonedDateTime,
    val viewer: Viewer? = null,
    val labels: List<Label>? = null
)
