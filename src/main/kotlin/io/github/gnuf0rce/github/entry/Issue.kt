package io.github.gnuf0rce.github.entry

import io.github.gnuf0rce.github.*
import kotlinx.serialization.*
import java.time.*

@Serializable
data class Issue(
    @SerialName("active_lock_reason")
    val activeLockReason: String?,
    @SerialName("assignee")
    val assignee: Assignee?,
    @SerialName("assignees")
    val assignees: List<Assignee> = emptyList(),
    @SerialName("author_association")
    val authorAssociation: String,
    @SerialName("body")
    val body: String,
    @SerialName("closed_at")
    @Serializable(OffsetDateTimeSerializer::class)
    override val closedAt: OffsetDateTime?,
    @SerialName("comments")
    val comments: Int,
    @SerialName("comments_url")
    val commentsUrl: String,
    @SerialName("created_at")
    @Serializable(OffsetDateTimeSerializer::class)
    override val createdAt: OffsetDateTime,
    @SerialName("events_url")
    val eventsUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("labels")
    val labels: List<Label> = emptyList(),
    @SerialName("labels_url")
    val labelsUrl: String,
    @SerialName("locked")
    val locked: Boolean,
    @SerialName("milestone")
    val milestone: Milestone? = null,
    @SerialName("node_id")
    val nodeId: String,
    @SerialName("number")
    val number: Int,
    @SerialName("pull_request")
    val pullRequest: PullRequest? = null,
    @SerialName("repository_url")
    val repositoryUrl: String,
    @SerialName("state")
    val state: String,
    @SerialName("title")
    val title: String,
    @SerialName("updated_at")
    @Serializable(OffsetDateTimeSerializer::class)
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    val url: String,
    @SerialName("user")
    val user: Creator
): LifeCycle