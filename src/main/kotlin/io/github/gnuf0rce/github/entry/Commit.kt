package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
data class Commit(
    @SerialName("author")
    override val user: Coder,
    @SerialName("comments_url")
    val commentsUrl: String,
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
    override val url: String
) : Entry, Record, LifeCycle, WithUserInfo, HtmlPage {
    @Deprecated("Commit No Close", ReplaceWith("null"))
    override val closedAt: OffsetDateTime?
        get() = null

    override val updatedAt: OffsetDateTime
        get() = detail.author.date

    override val createdAt: OffsetDateTime
        get() = detail.author.date

    @Serializable
    data class Tree(
        @SerialName("sha")
        override val sha: String,
        @SerialName("url")
        override val url: String
    ) : Record

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
        override val url: String,
        @SerialName("verification")
        val verification: Verification
    ): Entry {
        @Deprecated("Detail No Id", ReplaceWith("throw NotImplementedError(\"Detail.nodeId\")"))
        override val nodeId: String
            get() = throw NotImplementedError("Detail.nodeId")
    }

    @Serializable
    data class Verification(
        @SerialName("payload")
        val payload: String?,
        @SerialName("reason")
        val reason: String,
        @SerialName("signature")
        val signature: String?,
        @SerialName("verified")
        val verified: Boolean
    )
}