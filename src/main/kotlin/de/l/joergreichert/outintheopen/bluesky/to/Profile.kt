package de.l.joergreichert.outintheopen.bluesky.to

import java.time.ZonedDateTime

data class Profile (
    val did: String? = null,
    val handle: String? = null,
    val displayName: String? = null,
    val description: String? = null,
    val avatar: String? = null,
    val banner: String? = null,
    val followsCount: Int? = null,
    val followersCount: Int? = null,
    val postsCount: Int? = null,
    val indexedAt: ZonedDateTime? = null,
    val labels: List<Label>? = null
)