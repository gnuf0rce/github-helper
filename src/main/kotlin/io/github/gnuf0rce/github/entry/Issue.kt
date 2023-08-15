/*
 * Copyright 2021-2023 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
@SerialName("Issue")
public data class Issue(
    @SerialName("active_lock_reason")
    val activeLockReason: String?,
    @SerialName("state_reason")
    val stateReason: String?,
    @SerialName("assignee")
    override val assignee: Owner? = null,
    @SerialName("assignees")
    override val assignees: List<Owner> = emptyList(),
    @SerialName("author_association")
    override val association: Association,
    @SerialName("body")
    override val body: String?,
    @SerialName("body_text")
    override val text: String?,
    @SerialName("body_html")
    override val html: String?,
    @Contextual
    @SerialName("closed_at")
    override val closedAt: OffsetDateTime?,
    @SerialName("closed_by")
    override val closedBy: Owner? = null,
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
    override val labels: List<Label> = emptyList(),
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
    @SerialName("pull_request")
    val pullRequest: PullRequest? = null,
    @SerialName("reactions")
    override val reactions: Reactions? = null,
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
    override val user: Owner?,
    @SerialName("repository")
    override val repository: Repo? = null,
    @SerialName("performed_via_github_app")
    val performedViaGithubApp: GithubAppInfo? = null
) : Entry, ControlRecord() {

    override val owner: Owner?
        get() = user

    public override val graphUrl: String
        get() = "https://opengraph.githubassets.com/${updatedAt.toEpochSecond()}/${repository?.fullName}/issues/$number"

    @Serializable
    public data class PullRequest(
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
    ) : WebPage

    override val mergedBy: Owner?
        get() = closedBy
}