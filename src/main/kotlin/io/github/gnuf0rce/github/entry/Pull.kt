package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
data class Pull(
    @SerialName("active_lock_reason")
    val activeLockReason: String? = null,
    @SerialName("assignee")
    val assignee: Coder? = null,
    @SerialName("assignees")
    val assignees: List<Coder> = emptyList(),
    @SerialName("author_association")
    val authorAssociation: Association,
    @SerialName("auto_merge")
    val autoMerge: String?,
    @SerialName("base")
    val base: About,
    @SerialName("body")
    val body: String,
    @Contextual
    @SerialName("closed_at")
    override val closedAt: OffsetDateTime?,
    @SerialName("comments_url")
    val commentsUrl: String,
    @SerialName("commits_url")
    val commitsUrl: String,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("diff_url")
    val diffUrl: String,
    @SerialName("draft")
    val draft: Boolean,
    @SerialName("head")
    val head: About,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("issue_url")
    val issueUrl: String,
    @SerialName("labels")
    val labels: List<Label> = emptyList(),
    @SerialName("_links")
    val links: Links, // Map<String, Link>
    @SerialName("locked")
    val locked: Boolean,
    @SerialName("merge_commit_sha")
    val mergeCommitSha: String,
    @Contextual
    @SerialName("merged_at")
    val mergedAt: OffsetDateTime? = null,
    @SerialName("milestone")
    val milestone: Milestone? = null,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("number")
    val number: Int,
    @SerialName("patch_url")
    val patchUrl: String,
    @SerialName("requested_reviewers")
    val requestedReviewers: List<Coder> = emptyList(),
    @SerialName("requested_teams")
    val requestedTeams: List<RequestedTeam> = emptyList(),
    @SerialName("review_comment_url")
    val reviewCommentUrl: String,
    @SerialName("review_comments_url")
    val reviewCommentsUrl: String,
    @SerialName("state")
    val state: STATE,
    @SerialName("statuses_url")
    val statusesUrl: String,
    @SerialName("title")
    val title: String,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    val url: String,
    @SerialName("user")
    override val user: Coder
) : Entry, LifeCycle, WithUserInfo