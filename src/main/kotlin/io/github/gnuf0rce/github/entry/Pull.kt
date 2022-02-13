package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
data class Pull(
    @SerialName("active_lock_reason")
    val activeLockReason: String? = null,
    @SerialName("additions")
    val additions: Int = 0,
    @SerialName("assignee")
    override val assignee: User? = null,
    @SerialName("assignees")
    override val assignees: List<User> = emptyList(),
    @SerialName("author_association")
    override val authorAssociation: Association,
    @SerialName("auto_merge")
    val autoMerge: String?,
    @SerialName("base")
    val base: About,
    @SerialName("body")
    override val body: String?,
    @SerialName("changed_files")
    val changedFiles: Int = 0,
    @Contextual
    @SerialName("closed_at")
    override val closedAt: OffsetDateTime?,
    @SerialName("closed_by")
    override val closedBy: User? = null,
    @SerialName("comments")
    override val comments: Int = 0,
    @SerialName("comments_url")
    override val commentsUrl: String,
    @SerialName("commits")
    val commits: Int = 0,
    @SerialName("commits_url")
    val commitsUrl: String,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("deletions")
    val deletions: Int = 0,
    @SerialName("diff_url")
    val diffUrl: String,
    @SerialName("draft")
    override val draft: Boolean = false,
    @SerialName("head")
    val head: About,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("issue_url")
    val issueUrl: String,
    @SerialName("labels")
    override val labels: List<ControlRecord.Label> = emptyList(),
    @SerialName("_links")
    val links: Links, // Map<String, Link>
    @SerialName("locked")
    override val locked: Boolean,
    @SerialName("maintainer_can_modify")
    val maintainerCanModify: Boolean = false,
    @SerialName("mergeable")
    val mergeable: Boolean? = null,
    @SerialName("mergeable_state")
    val mergeableState: MergeableState = MergeableState.unknown,
    @SerialName("merged")
    val merged: Boolean = false,
    @SerialName("merged_by")
    val mergedBy: User? = null,
    @SerialName("merge_commit_sha")
    val mergeCommitSha: String,
    @Contextual
    @SerialName("merged_at")
    override val mergedAt: OffsetDateTime? = null,
    @SerialName("milestone")
    override val milestone: Milestone? = null,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("number")
    override val number: Int,
    @SerialName("patch_url")
    val patchUrl: String,
    @SerialName("reactions")
    override val reactions: Reactions? = null,
    @SerialName("rebaseable")
    val rebaseable: Boolean? = null,
    @SerialName("requested_reviewers")
    val requestedReviewers: List<User> = emptyList(),
    @SerialName("requested_teams")
    val requestedTeams: List<Team> = emptyList(),
    @SerialName("review_comment_url")
    val reviewCommentUrl: String,
    @SerialName("review_comments")
    val reviewComments: Int = 0,
    @SerialName("review_comments_url")
    val reviewCommentsUrl: String,
    @SerialName("state")
    override val state: State = State.open,
    @SerialName("statuses_url")
    val statusesUrl: String,
    @SerialName("title")
    override val title: String,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String,
    @SerialName("user")
    override val user: User,
    @SerialName("repository")
    override val repository: Repo? = null
) : Entry, LifeCycle, HtmlPage, ControlRecord {

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
        val user: User
    ) : Owner.Product {

        override val owner: User
            get() = user
    }

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
    )

    @Serializable
    data class Link(
        @SerialName("href")
        val href: String
    )
}