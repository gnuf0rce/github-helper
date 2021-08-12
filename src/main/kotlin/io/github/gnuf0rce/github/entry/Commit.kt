package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
data class Commit(
    @SerialName("author")
    override val user: Creator,
    @SerialName("comments_url")
    val commentsUrl: String,
    @SerialName("commit")
    val commit: Commit,
    @SerialName("committer")
    val committer: Creator,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("parents")
    val parents: List<Tree>,
    @SerialName("sha")
    override val sha: String,
    @SerialName("url")
    override val url: String
) : Entry, Record, LifeCycle, WithUserInfo {
    @Deprecated("Commit No Close", ReplaceWith("null"))
    override val closedAt: OffsetDateTime?
        get() = null

    override val updatedAt: OffsetDateTime
        get() = commit.author.date

    override val createdAt: OffsetDateTime
        get() = commit.author.date

    @Serializable
    data class Tree(
        @SerialName("sha")
        override val sha: String,
        @SerialName("url")
        override val url: String
    ) : Record

    @Serializable
    data class Commit(
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