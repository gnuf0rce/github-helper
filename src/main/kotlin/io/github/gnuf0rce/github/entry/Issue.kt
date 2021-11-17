package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
data class Issue(
    @SerialName("active_lock_reason")
    val activeLockReason: String?,
    @SerialName("assignee")
    override val assignee: Coder? = null,
    @SerialName("assignees")
    override val assignees: List<Coder> = emptyList(),
    @SerialName("author_association")
    override val authorAssociation: Association,
    @SerialName("body")
    override val body: String?,
    @Contextual
    @SerialName("closed_at")
    override val closedAt: OffsetDateTime?,
    @SerialName("closed_by")
    override val closedBy: Coder? = null,
    @SerialName("comments")
    override val comments: Int = 0,
    @SerialName("comments_url")
    override val commentsUrl: String,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("draft")
    override val draft: Boolean = false,
    @SerialName("events_url")
    val eventsUrl: String,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("labels")
    override val labels: List<ControlRecord.Label> = emptyList(),
    @SerialName("labels_url")
    val labelsUrl: String,
    @SerialName("locked")
    override val locked: Boolean,
    @Contextual
    @SerialName("merged_at")
    override val mergedAt: OffsetDateTime? = null,
    @SerialName("milestone")
    override val milestone: Milestone? = null,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("number")
    override val number: Int,
    @SerialName("performed_via_github_app")
    val performedViaGithubApp: String? = null,
    @SerialName("pull_request")
    val pullRequest: PullRequest? = null,
    @SerialName("reactions")
    val reactions: Reactions? = null,
    @SerialName("repository_url")
    val repositoryUrl: String,
    @SerialName("state")
    override val state: State = State.open,
    @SerialName("timeline_url")
    val timelineUrl: String,
    @SerialName("title")
    override val title: String,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String,
    @SerialName("user")
    override val user: Coder
) : Entry, LifeCycle, HtmlPage, ControlRecord {

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
        val url: String,
    ) : HtmlPage
}