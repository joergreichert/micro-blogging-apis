package de.l.joergreichert.outintheopen.mastodon.to

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class MastodonStatus(
    val id: String?,
    @JsonProperty("created_at")
    val createdAt: String?,
    @JsonProperty("in_reply_to_id")
    val in_reply_to_id: String?,
    @JsonProperty("in_reply_to_account_id")
    val inReplyToAccountId: String?,
    val sensitive: Boolean?,
    @JsonProperty("spoiler_text")
    val spoilerText: String?,
    val visibility: String?,
    val language: String?,
    val uri: String?,
    val url: String?,
    @JsonProperty("replies_count")
    val repliesCount: Int?,
    @JsonProperty("reblogs_count")
    val reblogsCount: Int?,
    @JsonProperty("favourites_count")
    val favouritesCount: Int?,
    @JsonProperty("edited_at")
    val editedAt: String?,
    val favourited: Boolean?,
    val reblogged: Boolean?,
    val muted: Boolean?,
    val bookmarked: Boolean?,
    val content: String?,
    val reblog: Reblog?,
    val application: Any?,
    val account: MastodonAccount?,
    @JsonProperty("media_attachments")
    val mediaAttachments: List<*>?,
    val mentions: List<*>?,
    val tags: List<*>?,
    val emojis: List<*>?,
    val card: MastodonCard?,
    val poll: Any?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Reblog(
    val id: String?,
    @JsonProperty("created_at")
    val created_at: String?,
    @JsonProperty("in_reply_to_id")
    val inReplyToId: String?,
    @JsonProperty("in_reply_to_account_id")
    val inReplyToAccountId: String?,
    val sensitive: Boolean?,
    @JsonProperty("spoiler_text")
    val spoilerText: String?,
    val visibility: String?,
    val language: String?,
    val uri: String?,
    val url: String?,
    @JsonProperty("replies_count")
    val repliesCount: Int?,
    @JsonProperty("reblogs_count")
    val reblogsCount: Int?,
    @JsonProperty("favourites_count")
    val favouritesCount: Int?,
    @JsonProperty("edited_at")
    val editedAt: String?,
    val content: String?,
    val reblog: Reblog?,
    val account: MastodonAccount?,
    @JsonProperty("media_attachments")
    val mediaAttachments: List<MastodonMediaAttachment>?,
    val mentions: List<*>?,
    val tags: List<MastodonTag>?,
    val emojis: List<*>?,
    val card: MastodonCard?,
    val poll: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MastodonMediaAttachment(
    val id: String?,
    val type: String?,
    val url: String?,
    @JsonProperty("preview_url")
    val previewUrl: String?,
    @JsonProperty("remote_url")
    val remoteUrl: String?,
    @JsonProperty("preview_remote_url")
    val previewRemoteUrl: String?,
    @JsonProperty("text_url")
    val textUrl: String?,
    val meta: MastodonMediaAttachmentMeta?,
    val description: String?,
    val blurhash: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MastodonMediaAttachmentMeta(
    val focus: MastodonMediaAttachmentMetaFocus?,
    val original: MastodonMediaAttachmentMetaDimension?,
    val small: MastodonMediaAttachmentMetaDimension?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MastodonMediaAttachmentMetaFocus(
    val x: Float?,
    val y: Float?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MastodonMediaAttachmentMetaDimension(
    val width: Int?,
    val height: Int?,
    val size: String?,
    val aspect: Float?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MastodonTag(
    val name: String?,
    val url: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MastodonCard(
    val url: String?,
    val title: String?,
    val description: String?,
    val type: String?,
    @JsonProperty("author_name")
    val authorName: String?,
    @JsonProperty("author_url")
    val authorUrl: String?,
    @JsonProperty("provider_name")
    val providerName: String?,
    @JsonProperty("provider_url")
    val providerUrl: String?,
    val html: String?,
    val width: Int?,
    val height: Int?,
    val image: String?,
    @JsonProperty("embed_url")
    val embedUrl: String?,
    val blurhash: String?
)