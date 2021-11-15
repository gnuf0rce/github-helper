package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

sealed interface LifeCycle {
    val createdAt: OffsetDateTime
    val updatedAt: OffsetDateTime
    val mergedAt: OffsetDateTime?
    val closedAt: OffsetDateTime?
}

interface Record {
    val sha: String
    val url: String
}

sealed interface Entry {
    val url: String
    val nodeId: String
}

interface HtmlPage {
    val htmlUrl: String?
}

@Serializable
data class Label(
    @SerialName("color")
    val color: String,
    @SerialName("default")
    val default: Boolean,
    @SerialName("description")
    val description: String,
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("url")
    override val url: String
) : Entry

@Serializable
data class Milestone(
    @Contextual
    @SerialName("closed_at")
    val closedAt: OffsetDateTime?,
    @SerialName("closed_issues")
    val closedIssues: Int,
    @Contextual
    @SerialName("created_at")
    val createdAt: OffsetDateTime,
    @SerialName("creator")
    val creator: Coder,
    @SerialName("description")
    val description: String,
    @Contextual
    @SerialName("due_on")
    val dueOn: OffsetDateTime?,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("labels_url")
    val labelsUrl: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("number")
    val number: Int,
    @SerialName("open_issues")
    val openIssues: Int,
    @SerialName("state")
    val state: String,
    @SerialName("title")
    val title: String,
    @Contextual
    @SerialName("updated_at")
    val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String
) : Entry, HtmlPage

@Serializable
data class PullRequest(
    @SerialName("diff_url")
    val diffUrl: String,
    @SerialName("html_url")
    override val htmlUrl: String,
    @Contextual
    @SerialName("merged_at")
    val mergedAt: OffsetDateTime? = null,
    @SerialName("patch_url")
    val patchUrl: String,
    @SerialName("url")
    override val url: String,
) : Entry, HtmlPage {
    @Deprecated("PullRequest No Id", ReplaceWith("throw NotImplementedError(\"PullRequest.nodeId\")"))
    override val nodeId: String
        get() = throw NotImplementedError("PullRequest.nodeId")
}

@Serializable
data class About(
    @SerialName("label")
    val label: String,
    @SerialName("ref")
    val ref: String,
    @SerialName("repo")
    val repo: Repo? = null,
    @SerialName("sha")
    val sha: String,
    @SerialName("user")
    val user: Coder
)

@Serializable
data class License(
    @SerialName("html_url")
    override val htmlUrl: String? = null,
    @SerialName("key")
    val key: String,
    @SerialName("name")
    val name: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("spdx_id")
    val spdxId: String,
    @SerialName("url")
    override val url: String
) : Entry, HtmlPage

@Serializable
data class Links(
    @SerialName("comments")
    val comments: Link,
    @SerialName("commits")
    val commits: Link,
    @SerialName("html")
    val html: Link,
    @SerialName("issue")
    val issue: Link,
    @SerialName("review_comment")
    val reviewComment: Link,
    @SerialName("review_comments")
    val reviewComments: Link,
    @SerialName("self")
    val self: Link,
    @SerialName("statuses")
    val statuses: Link
) {
    @Serializable
    data class Link(
        @SerialName("href")
        val href: String
    )
}

@Serializable
data class RequestedTeam(
    @SerialName("description")
    val description: String,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("members_url")
    val membersUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("parent")
    val parent: RequestedTeam?,
    @SerialName("permission")
    val permission: String,
    @SerialName("privacy")
    val privacy: String,
    @SerialName("repositories_url")
    val repositoriesUrl: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("url")
    override val url: String
) : Entry, HtmlPage

@Serializable
data class Reactions(
    @SerialName("confused")
    val confused: Int,
    @SerialName("eyes")
    val eyes: Int,
    @SerialName("heart")
    val heart: Int,
    @SerialName("hooray")
    val hooray: Int,
    @SerialName("laugh")
    val laugh: Int,
    @SerialName("rocket")
    val rocket: Int,
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("url")
    val url: String,
    @SerialName("+1")
    val plus: Int,
    @SerialName("-1")
    val minus: Int
)