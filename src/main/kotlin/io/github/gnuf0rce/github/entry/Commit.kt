package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
data class Commit(
    @SerialName("author")
    val author: Coder,
    @SerialName("comments_url")
    val commentsUrl: String,
    /**
     * XXX: name
     */
    @SerialName("commit")
    val detail: Detail,
    @SerialName("committer")
    val committer: Coder,
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
) : Entry, Record, LifeCycle, HtmlPage, Coder.Product {

    override val creator: Coder
        get() = author

    @Deprecated("Commit No Close", ReplaceWith("null"))
    override val closedAt: OffsetDateTime?
        get() = null

    @Deprecated("Commit No Merge", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null

    override val updatedAt: OffsetDateTime
        get() = detail.committer.date

    override val createdAt: OffsetDateTime
        get() = detail.author.date

    @Serializable
    data class Tree(
        @SerialName("sha")
        override val sha: String,
        @SerialName("html_url")
        override val htmlUrl: String? = null,
        @SerialName("url")
        override val url: String
    ) : Record, HtmlPage

    @Serializable
    data class Detail(
        @SerialName("author")
        val author: Author,
        @SerialName("comment_count")
        val commentCount: Int,
        @SerialName("committer")
        val committer: Author,
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
    data class Author(
        @Contextual
        @SerialName("date")
        val date: OffsetDateTime,
        @SerialName("email")
        val email: String,
        @SerialName("name")
        val name: String
    )

    @Serializable
    data class Verification(
        @SerialName("payload")
        val payload: String?,
        @SerialName("reason")
        val reason: VerificationReason,
        @SerialName("signature")
        val signature: String?,
        @SerialName("verified")
        val verified: Boolean
    )

    @Serializable
    data class Stats(
        @SerialName("additions")
        val additions: Int = 0,
        @SerialName("deletions")
        val deletions: Int = 0,
        @SerialName("total")
        val total: Int = 0
    )

    @Serializable
    data class File(
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
        @SerialName("patch")
        val patch: String,
        @SerialName("raw_url")
        val rawUrl: String,
        @SerialName("sha")
        override val sha: String,
        @SerialName("status")
        val status: String
    ) : Record {

        override val url: String
            get() = contentsUrl
    }
}