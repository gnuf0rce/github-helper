package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
data class Issue(
    @SerialName("active_lock_reason")
    val activeLockReason: String?,
    @SerialName("assignee")
    val assignee: Coder?,
    @SerialName("assignees")
    val assignees: List<Coder> = emptyList(),
    @SerialName("author_association")
    val authorAssociation: Association,
    @SerialName("body")
    val body: String?,
    @Contextual
    @SerialName("closed_at")
    override val closedAt: OffsetDateTime?,
    @SerialName("closed_by")
    val closedBy: Coder? = null,
    @SerialName("comments")
    val comments: Int,
    @SerialName("comments_url")
    val commentsUrl: String,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("draft")
    val draft: Boolean = false,
    @SerialName("events_url")
    val eventsUrl: String,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("labels")
    val labels: List<Label> = emptyList(),
    @SerialName("labels_url")
    val labelsUrl: String,
    @SerialName("locked")
    val locked: Boolean,
    @Contextual
    @SerialName("merged_at")
    override val mergedAt: OffsetDateTime? = null,
    @SerialName("milestone")
    val milestone: Milestone? = null,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("number")
    val number: Int,
    @SerialName("performed_via_github_app")
    val performedViaGithubApp: String? = null,
    @SerialName("pull_request")
    val pullRequest: PullRequest? = null,
    @SerialName("reactions")
    val reactions: Reactions? = null,
    @SerialName("repository_url")
    val repositoryUrl: String,
    @SerialName("state")
    val state: State = State.open,
    @SerialName("timeline_url")
    val timelineUrl: String,
    @SerialName("title")
    val title: String,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String,
    @SerialName("user")
    val user: Coder
) : Entry, LifeCycle, HtmlPage