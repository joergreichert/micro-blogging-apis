package de.l.joergreichert.outintheopen.bluesky.to

@JvmRecord
data class Followers(
    val subject: Actor,
    val followers: List<Actor>,
    val cursor: String
)

@JvmRecord
data class Follows(
    val subject: Actor,
    val follows: List<Actor>,
    val cursor: String
)
