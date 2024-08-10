package de.l.joergreichert.outintheopen.bluesky.to

import java.time.ZonedDateTime


@JvmRecord
data class Feeds(
    val feed: List<Feed>? = null,
    val cursor: String? = null
)


@JvmRecord
data class Feed(
    val post: PostView? = null,
    val reason: Reason? = null,
    val reply: Reply? = null
)

@JvmRecord
data class PostView(
    val uri: String? = null,
    val cid: String? = null,
    val author: Author? = null,
    val embed: Embed? = null,
    val record: de.l.joergreichert.outintheopen.bluesky.to.Record? = null,
    val replyCount: Int? = null,
    val repostCount: Int? = null,
    val likeCount: Int? = null,
    val indexedAt: ZonedDateTime? = null,
    val viewer: Viewer? = null,
    val labels: List<Label>? = null
)

@JvmRecord
data class Author(
    val did: String? = null,
    val handle: String? = null,
    val displayName: String? = null,
    val avatar: String? = null,
    val viewer: Viewer? = null,
    val labels: List<Label>? = null,
    val createdAt: String? = null,
    val associated: Any? = null,
)

@JvmRecord
data class Reason(
    val `$type`: String? = null,
    val by: Author? = null,
    val indexedAt: ZonedDateTime? = null
)

@JvmRecord
data class Reply(
    val root: PostView? = null,
    val handle: String? = null,
    val parent: PostView? = null
)

@JvmRecord
data class Embed(
    val `$type`: String? = null,
    val record: de.l.joergreichert.outintheopen.bluesky.to.Record? = null,
    val media: Media? = null,
    val external: External? = null,
    val images: List<Images>? = null
)

@JvmRecord
data class External(
    val uri: String? = null,
    val title: String? = null,
    val description: String? = null,
    val thumb: Any? = null
)

@JvmRecord
data class Media(
    val `$type`: String? = null,
    val images: List<Images>? = null
)

@JvmRecord
data class Images(
    val thumb: String? = null,
    val fullsize: String? = null,
    val alt: String? = null,
    val image: Image? = null,
    val aspectRatio: Any? = null,
)

@JvmRecord
data class Image(
    val `$type`: String? = null,
    val ref: Ref? = null,
    val mimeType: String? = null,
    val size: Int? = null
)

@JvmRecord
data class Ref(val `$link`: String? = null)

@JvmRecord
data class Record(
    val `$type`: String? = null,
    val createdAt: String? = null,
    val embed: Any? = null,
    val facets: Any? = null,
    val langs: Any? = null,
    val text: Any? = null,
)