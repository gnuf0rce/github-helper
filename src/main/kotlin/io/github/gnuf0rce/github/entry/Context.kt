package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

sealed interface LifeCycle {
    val createdAt: OffsetDateTime
    val updatedAt: OffsetDateTime
    val closedAt: OffsetDateTime?
}

interface Record {
    val sha: String
    val url: String
}

interface WithUserInfo {
    val user: UserInfo
}

interface Entry {
    val nodeId: String
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
    val url: String
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
    val htmlUrl: String,
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
    val url: String
) : Entry

@Serializable
data class PullRequest(
    @SerialName("diff_url")
    val diffUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("patch_url")
    val patchUrl: String,
    @SerialName("url")
    val url: String
)

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
data class Repo(
    @SerialName("allow_auto_merge")
    val allowAutoMerge: Boolean = false,
    @SerialName("allow_merge_commit")
    val allowMergeCommit: Boolean = false,
    @SerialName("allow_rebase_merge")
    val allowRebaseMerge: Boolean = false,
    @SerialName("allow_squash_merge")
    val allowSquashMerge: Boolean = false,
    @SerialName("archive_url")
    val archiveUrl: String,
    @SerialName("archived")
    val archived: Boolean,
    @SerialName("assignees_url")
    val assigneesUrl: String,
    @SerialName("blobs_url")
    val blobsUrl: String,
    @SerialName("branches_url")
    val branchesUrl: String,
    @SerialName("clone_url")
    val cloneUrl: String,
    @SerialName("collaborators_url")
    val collaboratorsUrl: String,
    @SerialName("comments_url")
    val commentsUrl: String,
    @SerialName("commits_url")
    val commitsUrl: String,
    @SerialName("compare_url")
    val compareUrl: String,
    @SerialName("contents_url")
    val contentsUrl: String,
    @SerialName("contributors_url")
    val contributorsUrl: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("default_branch")
    val defaultBranch: String,
    @SerialName("delete_branch_on_merge")
    val deleteBranchOnMerge: Boolean = false,
    @SerialName("deployments_url")
    val deploymentsUrl: String,
    @SerialName("description")
    val description: String,
    @SerialName("disabled")
    val disabled: Boolean,
    @SerialName("downloads_url")
    val downloadsUrl: String,
    @SerialName("events_url")
    val eventsUrl: String,
    @SerialName("fork")
    val fork: Boolean,
    @SerialName("forks")
    val forks: Int,
    @SerialName("forks_count")
    val forksCount: Int,
    @SerialName("forks_url")
    val forksUrl: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("git_commits_url")
    val gitCommitsUrl: String,
    @SerialName("git_refs_url")
    val gitRefsUrl: String,
    @SerialName("git_tags_url")
    val gitTagsUrl: String,
    @SerialName("git_url")
    val gitUrl: String,
    @SerialName("has_downloads")
    val hasDownloads: Boolean = false,
    @SerialName("has_issues")
    val hasIssues: Boolean = false,
    @SerialName("has_pages")
    val hasPages: Boolean = false,
    @SerialName("has_projects")
    val hasProjects: Boolean = false,
    @SerialName("has_wiki")
    val hasWiki: Boolean = false,
    @SerialName("homepage")
    val homepage: String,
    @SerialName("hooks_url")
    val hooksUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("is_template")
    val isTemplate: Boolean = false,
    @SerialName("issue_comment_url")
    val issueCommentUrl: String,
    @SerialName("issue_events_url")
    val issueEventsUrl: String,
    @SerialName("issues_url")
    val issuesUrl: String,
    @SerialName("keys_url")
    val keysUrl: String,
    @SerialName("labels_url")
    val labelsUrl: String,
    @SerialName("language")
    val language: String?,//
    @SerialName("languages_url")
    val languagesUrl: String,
    @SerialName("license")
    val license: License,
    @SerialName("merges_url")
    val mergesUrl: String,
    @SerialName("milestones_url")
    val milestonesUrl: String,
    @SerialName("mirror_url")
    val mirrorUrl: String?,
    @SerialName("name")
    val name: String,
    @SerialName("network_count")
    val networkCount: Int = 0,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("notifications_url")
    val notificationsUrl: String,
    @SerialName("open_issues")
    val openIssues: Int,
    @SerialName("open_issues_count")
    val openIssuesCount: Int,
    @SerialName("owner")
    val owner: Coder,
    @SerialName("permissions")
    val permissions: Map<String, Boolean> = emptyMap(),
    @SerialName("private")
    val `private`: Boolean = false,
    @SerialName("pulls_url")
    val pullsUrl: String,
    @SerialName("pushed_at")
    val pushedAt: String,
    @SerialName("releases_url")
    val releasesUrl: String,
    @SerialName("size")
    val size: Int,
    @SerialName("ssh_url")
    val sshUrl: String,
    @SerialName("stargazers_count")
    val stargazersCount: Int,
    @SerialName("stargazers_url")
    val stargazersUrl: String,
    @SerialName("statuses_url")
    val statusesUrl: String,
    @SerialName("subscribers_count")
    val subscribersCount: Int = 0,
    @SerialName("subscribers_url")
    val subscribersUrl: String,
    @SerialName("subscription_url")
    val subscriptionUrl: String,
    @SerialName("svn_url")
    val svnUrl: String,
    @SerialName("tags_url")
    val tagsUrl: String,
    @SerialName("teams_url")
    val teamsUrl: String,
    @SerialName("temp_clone_token")
    val tempCloneToken: String? = null,
    @SerialName("template_repository")
    val templateRepository: Repo? = null,
    @SerialName("topics")
    val topics: List<String> = emptyList(),
    @SerialName("trees_url")
    val treesUrl: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("url")
    val url: String,
    @SerialName("visibility")
    val visibility: Visibility = Visibility.public,
    @SerialName("watchers")
    val watchers: Int,
    @SerialName("watchers_count")
    val watchersCount: Int
): Entry

@Serializable
data class License(
    @SerialName("html_url")
    val htmlUrl: String? = null,
    @SerialName("key")
    val key: String,
    @SerialName("name")
    val name: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("spdx_id")
    val spdxId: String,
    @SerialName("url")
    val url: String
): Entry

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
    val htmlUrl: String,
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
    val url: String
) : Entry