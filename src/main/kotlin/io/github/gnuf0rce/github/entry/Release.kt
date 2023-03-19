/*
 * Copyright 2021-2023 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import io.ktor.http.*
import kotlinx.serialization.*
import java.time.*

@Serializable
@SerialName("Release")
public data class Release(
    @SerialName("assets")
    val assets: List<Asset>,
    @SerialName("assets_url")
    val assetsUrl: String,
    @SerialName("author")
    override val owner: Owner?,
    @SerialName("body")
    override val body: String?,
    @SerialName("body_text")
    override val text: String?,
    @SerialName("body_html")
    override val html: String?,
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
    val name: String?,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("prerelease")
    val prerelease: Boolean,
    @Contextual
    @SerialName("published_at")
    val publishedAt: OffsetDateTime? = null,
    @SerialName("reactions")
    override val reactions: Reactions? = null,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("tarball_url")
    val tarballUrl: String?,
    @SerialName("target_commitish")
    val targetCommitish: String,
    @SerialName("upload_url")
    val uploadUrl: String,
    @SerialName("url")
    override val url: String,
    @SerialName("zipball_url")
    val zipballUrl: String?
) : Entry, LifeCycle, WebPage, Content, Product {

    /**
     * @see publishedAt
     */
    override val closedAt: OffsetDateTime?
        get() = publishedAt

    @Deprecated("Release No Merged", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null

    /**
     * @see assets
     * @see createdAt
     */
    override val updatedAt: OffsetDateTime
        get() = assets.maxOfOrNull { it.updatedAt } ?: publishedAt ?: createdAt

    @Serializable
    public data class Asset(
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
        val size: Long,
        @SerialName("state")
        val state: ReleaseState,
        @Contextual
        @SerialName("updated_at")
        override val updatedAt: OffsetDateTime,
        @SerialName("uploader")
        override val owner: Owner?,
        @Contextual
        @SerialName("url")
        override val url: String
    ) : Entry, LifeCycle, Product {

        public val uploader: User?
            get() = owner as? User

        @Deprecated("Asset No Closed", ReplaceWith("null"))
        override val closedAt: OffsetDateTime?
            get() = null

        @Deprecated("Asset No Merged", ReplaceWith("null"))
        override val mergedAt: OffsetDateTime?
            get() = null
    }

    public val status: String = when {
        draft -> "draft"
        prerelease -> "prerelease"
        else -> "complete"
    }
}