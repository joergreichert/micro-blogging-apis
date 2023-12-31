package de.l.joergreichert.outintheopen.bluesky.to

import java.time.ZonedDateTime


@JvmRecord
data class Feeds(
    val feed: List<Feed>,
    val cursor: String
)


@JvmRecord
data class Feed(
    val post: PostView,
    val reason: Reason,
    val reply: Reply
)

@JvmRecord
data class PostView(
    val uri: String,
    val cid: String,
    val author: Author,
    val embed: Embed,
    val record: Record,
    val replyCount: Int,
    val repostCount: Int,
    val likeCount: Int,
    val indexedAt: ZonedDateTime,
    val viewer: Viewer,
    val labels: List<Label>
)

@JvmRecord
data class Author(
    val did: String,
    val handle: String,
    val displayName: String,
    val avatar: String,
    val viewer: Viewer,
    val labels: List<Label>
)

@JvmRecord
data class Reason(
    val `$type`: String,
    val by: Author,
    val indexedAt: ZonedDateTime
)

@JvmRecord
data class Reply(
    val root: PostView,
    val handle: String,
    val parent: PostView
)

@JvmRecord
data class Embed(
    val `$type`: String,
    val record: Record,
    val media: Media,
    val external: External,
    val images: List<Images>
)

@JvmRecord
data class External(
    val uri: String,
    val title: String,
    val description: String,
    val thumb: Any
)

@JvmRecord
data class Media(
    val `$type`: String,
    val images: List<Images>
)

@JvmRecord
data class Images(
    val thumb: String,
    val fullsize: String,
    val alt: String,
    val image: Image
)

@JvmRecord
data class Image(
    val `$type`: String,
    val ref: Ref,
    val mimeType: String,
    val size: Int
)

@JvmRecord
data class Ref(val `$link`: String)