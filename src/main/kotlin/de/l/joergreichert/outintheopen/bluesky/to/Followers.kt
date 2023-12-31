package de.l.joergreichert.outintheopen.bluesky.to

@JvmRecord
data class Followers(
    val subject: Actor? = null,
    val followers: List<Actor>? = null,
    val cursor: String? = null
)

@JvmRecord
data class Follows(
    val subject: Actor? = null,
    val follows: List<Actor>? = null,
    val cursor: String? = null
)
