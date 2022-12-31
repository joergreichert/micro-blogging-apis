package de.l.joergreichert.outintheopen.mastodon.to

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class MastodonAccount(
    val id: String,
    val username: String,
    val acct: String,
    @JsonProperty("display_name")
    val displayName: String?,
    val locked: Boolean?,
    val bot: Boolean?,
    val discoverable: Boolean?,
    val group: Boolean?,
    @JsonProperty("created_at")
    val createdAt: String?,
    val note: String?,
    val url: String?,
    val avatar: String?,
    @JsonProperty("avatar_static")
    val avatarStatic: String?,
    val header: String?,
    @JsonProperty("header_static")
    val headerStatic: String?,
    @JsonProperty("followers_count")
    val followersCount: Int?,
    @JsonProperty("following_count")
    val followingCount: Int?,
    @JsonProperty("statuses_count")
    val statusesCount: Int?,
    @JsonProperty("last_status_at")
    val lastStatusAt: String?,
    val emojis: List<*>?,
    val fields: List<MastodonAccountAccountField>?
)

data class MastodonAccountAccountField(
    val name: String?,
    val value: String?,
    @JsonProperty("verified_at")
    val verifiedAt: String?
)
