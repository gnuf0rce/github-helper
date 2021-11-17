package io.github.gnuf0rce.github.entry

import io.ktor.http.*
import kotlinx.serialization.*
import java.time.*

@Serializable
data class Release(
    @SerialName("assets")
    val assets: List<Asset>,
    @SerialName("assets_url")
    val assetsUrl: String,
    @SerialName("author")
    val author: Owner,
    @SerialName("body")
    override val body: String?,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("discussion_url")
    val discussionUrl: String? = null,
    @SerialName("draft")
    val draft: Boolean,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("mentions_count")
    val mentionsCount: Int = 0,
    @SerialName("name")
    val name: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("prerelease")
    val prerelease: Boolean,
    @Contextual
    @SerialName("published_at")
    val publishedAt: OffsetDateTime? = null,
    @SerialName("reactions")
    val reactions: Reactions? = null,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("tarball_url")
    val tarballUrl: String,
    @SerialName("target_commitish")
    val targetCommitish: String,
    @SerialName("upload_url")
    val uploadUrl: String,
    @SerialName("url")
    override val url: String,
    @SerialName("zipball_url")
    val zipballUrl: String
) : Entry, LifeCycle, HtmlPage, Content, Owner.Product {

    override val owner: Owner
        get() = author

    /**
     * 1. [publishedAt]
     */
    override val closedAt: OffsetDateTime?
        get() = publishedAt

    @Deprecated("Release No Merge", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null

    override val updatedAt: OffsetDateTime
        get() = assets.maxOfOrNull { it.updatedAt } ?: createdAt

    @Serializable
    data class Asset(
        @SerialName("browser_download_url")
        val browserDownloadUrl: String,
        @Contextual
        @SerialName("content_type")
        val contentType: ContentType,
        @Contextual
        @SerialName("created_at")
        override val createdAt: OffsetDateTime,
        @SerialName("download_count")
        val downloadCount: Int,
        @SerialName("id")
        val id: Long,
        @SerialName("label")
        val label: String?,
        @SerialName("name")
        val name: String,
        @SerialName("node_id")
        override val nodeId: String,
        @SerialName("size")
        val size: Int,
        @SerialName("state")
        val state: AssetState,
        @Contextual
        @SerialName("updated_at")
        override val updatedAt: OffsetDateTime,
        @SerialName("uploader")
        val uploader: Owner,
        @SerialName("url")
        override val url: String
    ) : Entry, LifeCycle {

        @Deprecated("Asset No Close", ReplaceWith("null"))
        override val closedAt: OffsetDateTime?
            get() = null

        @Deprecated("Asset No Merged", ReplaceWith("null"))
        override val mergedAt: OffsetDateTime?
            get() = null
    }
}