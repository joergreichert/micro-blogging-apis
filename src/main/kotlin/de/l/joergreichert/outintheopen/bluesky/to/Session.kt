package de.l.joergreichert.outintheopen.bluesky.to

@JvmRecord
data class Session(
    val did: String,
    val handle: String,
    val jwt: Jwt
)

@JvmRecord
data class Jwt(
    val access: String? = null,
    val refresh: String? = null
)

@JvmRecord
data class User(
    val identifier: String? = null,
    val password: String? = null
)