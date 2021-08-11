package io.github.gnuf0rce.github.entry

import io.github.gnuf0rce.github.*
import io.ktor.client.request.*
import kotlinx.serialization.*
import java.time.*

@Serializable
data class Pull(
    @SerialName("active_lock_reason")
    val activeLockReason: String? = null,
    @SerialName("assignee")
    val assignee: Assignee? = null,
    @SerialName("assignees")
    val assignees: List<Assignee> = emptyList(),
    @SerialName("author_association")
    val authorAssociation: String,
    @SerialName("auto_merge")
    val autoMerge: String?,
    @SerialName("base")
    val base: About,
    @SerialName("body")
    val body: String,
    @SerialName("closed_at")
    @Serializable(OffsetDateTimeSerializer::class)
    override val closedAt: OffsetDateTime?,
    @SerialName("comments_url")
    val commentsUrl: String,
    @SerialName("commits_url")
    val commitsUrl: String,
    @SerialName("created_at")
    @Serializable(OffsetDateTimeSerializer::class)
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
    val id: Int,
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
    @SerialName("merged_at")
    @Serializable(OffsetDateTimeSerializer::class)
    val mergedAt: OffsetDateTime? = null,
    @SerialName("milestone")
    val milestone: Milestone? = null,
    @SerialName("node_id")
    val nodeId: String,
    @SerialName("number")
    val number: Int,
    @SerialName("patch_url")
    val patchUrl: String,
    @SerialName("requested_reviewers")
    val requestedReviewers: List<Creator> = emptyList(),
    @SerialName("requested_teams")
    val requestedTeams: List<RequestedTeam> = emptyList(),
    @SerialName("review_comment_url")
    val reviewCommentUrl: String,
    @SerialName("review_comments_url")
    val reviewCommentsUrl: String,
    @SerialName("state")
    val state: String,
    @SerialName("statuses_url")
    val statusesUrl: String,
    @SerialName("title")
    val title: String,
    @SerialName("updated_at")
    @Serializable(OffsetDateTimeSerializer::class)
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    val url: String,
    @SerialName("user")
    val user: Creator
): LifeCycle {
    companion object {


        suspend fun repo(
            owner: String,
            repo: String,
            number: Int,
            github: GithubClient
        ): Pull = github.useHttpClient { client ->
            client.get("https://api.github.com/repos/${owner}/${repo}/pulls/${number}")
        }
    }
}