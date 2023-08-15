/*
 * Copyright 2021-2023 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import io.github.gnuf0rce.github.*
import kotlinx.serialization.*
import java.time.*

@Serializable
@SerialName("Commit")
public data class Commit(
    @SerialName("author")
    override val author: Owner?,
    @SerialName("comments_url")
    val commentsUrl: String,
    @SerialName("commit")
    val detail: Detail,
    @SerialName("committer")
    val committer: Owner?,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("parents")
    val parents: List<Tree>,
    @SerialName("sha")
    override val sha: String,
    @SerialName("url")
    override val url: String,
    @SerialName("stats")
    val stats: Stats = Stats(),
    @SerialName("files")
    val files: List<File> = emptyList()
) : Entry, Record, LifeCycle, WebPage, Product {

    override val owner: Owner?
        get() = author

    public override val graphUrl: String
        get() = "https://opengraph.githubassets.com/0/${FULL_REGEX.find(htmlUrl)?.value}/commit/${sha}"

    override val ownerNameOrLogin: String
        get() = author?.nameOrLogin ?: detail.author?.email ?: "ghost"

    @Deprecated("Commit No Closed", ReplaceWith("null"))
    override val closedAt: OffsetDateTime?
        get() = null

    @Deprecated("Commit No Merged", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null

    @Deprecated("Commit No Updated", ReplaceWith("null"))
    override val updatedAt: OffsetDateTime
        get() = createdAt.plusSeconds(detail.commentCount.toLong())

    override val createdAt: OffsetDateTime
        get() = detail.author?.date ?: OffsetDateTime.MIN

    @Serializable
    public data class Tree(
        @SerialName("sha")
        override val sha: String,
        @SerialName("html_url")
        override val htmlUrl: String = "",
        @SerialName("url")
        override val url: String
    ) : Record, WebPage

    @Serializable
    public data class Detail(
        @SerialName("author")
        val author: GitUser?,
        @SerialName("comment_count")
        val commentCount: Int,
        @SerialName("committer")
        val committer: GitUser?,
        @SerialName("message")
        val message: String,
        @SerialName("tree")
        val tree: Tree,
        @SerialName("url")
        val url: String,
        @SerialName("verification")
        val verification: Verification
    )

    @Serializable
    public data class Verification(
        @SerialName("payload")
        val payload: String?,
        @SerialName("reason")
        val reason: String,
        @SerialName("signature")
        val signature: String?,
        @SerialName("verified")
        val verified: Boolean
    )

    @Serializable
    public data class Stats(
        @SerialName("additions")
        val additions: Int = 0,
        @SerialName("deletions")
        val deletions: Int = 0,
        @SerialName("total")
        val total: Int = 0
    )

    @Serializable
    public data class File(
        @SerialName("additions")
        val additions: Int,
        @SerialName("blob_url")
        val blobUrl: String,
        @SerialName("changes")
        val changes: Int,
        @SerialName("contents_url")
        val contentsUrl: String,
        @SerialName("deletions")
        val deletions: Int,
        @SerialName("filename")
        val filename: String,
        @SerialName("previous_filename")
        val previousFilename: String = "",
        @SerialName("patch")
        val patch: String,
        @SerialName("raw_url")
        val rawUrl: String,
        @SerialName("sha")
        override val sha: String,
        @SerialName("status")
        val status: FileStatus
    ) : Record, WebPage {

        override val url: String
            get() = rawUrl

        override val htmlUrl: String
            get() = blobUrl
    }
}