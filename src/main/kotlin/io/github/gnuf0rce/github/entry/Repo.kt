/*
 * Copyright 2021-2024 dsstudio Technologies and contributors.
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
@SerialName("Repo")
public data class Repo(
    @SerialName("allow_auto_merge")
    val allowAutoMerge: Boolean = false,
    @SerialName("allow_forking")
    val allowForking: Boolean = false,
    @SerialName("allow_merge_commit")
    val allowMergeCommit: Boolean = false,
    @SerialName("allow_rebase_merge")
    val allowRebaseMerge: Boolean = false,
    @SerialName("allow_squash_merge")
    val allowSquashMerge: Boolean = false,
    @SerialName("allow_update_branch")
    val allowUpdateBranch: Boolean = false,
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
    @SerialName("code_of_conduct")
    val codeOfConduct: CodeOfConduct? = null,
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
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("default_branch")
    val defaultBranch: String,
    @SerialName("delete_branch_on_merge")
    val deleteBranchOnMerge: Boolean = false,
    @SerialName("deployments_url")
    val deploymentsUrl: String,
    @SerialName("description")
    val description: String?,
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
    @SerialName("has_discussions")
    val hasDiscussions: Boolean = false,
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
    val homepage: String?,
    @SerialName("hooks_url")
    val hooksUrl: String,
    @SerialName("html_url")
    override val htmlUrl: String,
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
    val language: String?,
    @SerialName("languages_url")
    val languagesUrl: String,
    @SerialName("license")
    val license: License?,
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
    override val owner: Owner?,
    @SerialName("permissions")
    val permissions: Map<String, Boolean> = emptyMap(),
    @SerialName("private")
    val `private`: Boolean = false,
    @SerialName("pulls_url")
    val pullsUrl: String,
    @Contextual
    @SerialName("pushed_at")
    val pushedAt: OffsetDateTime,
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
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String,
    @SerialName("visibility")
    val visibility: Visibility = Visibility.public,
    @SerialName("watchers")
    val watchers: Int,
    @SerialName("watchers_count")
    val watchersCount: Int,
    @SerialName("organization")
    val organization: Owner? = null,
    @SerialName("role_name")
    val roleName: String = "",
    @SerialName("web_commit_signoff_required")
    val webCommitSignoffRequired: Boolean = false,
    @SerialName("use_squash_pr_title_as_default")
    val useSquashPrTitleAsDefault: Boolean = false,
    @SerialName("squash_merge_commit_message")
    val squashMergeCommitMessage: String = "",
    @SerialName("squash_merge_commit_title")
    val squashMergeCommitTitle: String = "",
    @SerialName("merge_commit_message")
    val mergeCommitMessage: String = "",
    @SerialName("merge_commit_title")
    val mergeCommitTitle: String = "",
    @SerialName("security_and_analysis")
    val securityAndAnalysis: Map<String, RepoSecurityAndAnalysis> = emptyMap(),
    @SerialName("custom_properties")
    val customProperties: Map<String, String> = emptyMap()
) : Entry, LifeCycle, WebPage, Product {

    public override val graphUrl: String
        get() = "https://opengraph.githubassets.com/${updatedAt.toEpochSecond()}/${fullName}"

    override val closedAt: OffsetDateTime?
        get() = if (archived || disabled) updatedAt else null

    /**
     * @see [pushedAt]
     */
    override val mergedAt: OffsetDateTime
        get() = pushedAt

    @Serializable
    public data class CodeOfConduct(
        @SerialName("key")
        val key: String,
        @SerialName("name")
        val name: String,
        @SerialName("url")
        val url: String,
        @SerialName("body")
        override val body: String,
        @SerialName("body_text")
        override val text: String,
        @SerialName("body_html")
        override val html: String,
        @SerialName("html_url")
        override val htmlUrl: String,
        @SerialName("reactions")
        override val reactions: Reactions? = null
    ) : Content, WebPage
}