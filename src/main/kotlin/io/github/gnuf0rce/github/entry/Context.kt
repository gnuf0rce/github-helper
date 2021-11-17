package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

sealed interface LifeCycle {
    val createdAt: OffsetDateTime
    val updatedAt: OffsetDateTime
    val mergedAt: OffsetDateTime?
    val closedAt: OffsetDateTime?
}

sealed interface Record {
    val sha: String
    val url: String
}

sealed interface Entry {
    val url: String
    val nodeId: String
}

sealed interface HtmlPage {
    val htmlUrl: String?
}

sealed interface Content {
    val body: String?
}

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