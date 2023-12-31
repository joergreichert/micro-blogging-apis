package de.l.joergreichert.outintheopen.bluesky.to

import java.time.ZonedDateTime

data class Profile (
    val did: String,
    val handle: String,
    val displayName: String,
    val description: String,
    val avatar: String,
    val banner: String,
    val followsCount: Int,
    val followersCount: Int,
    val postsCount: Int,
    val indexedAt: ZonedDateTime,
    val labels: List<Label>
)