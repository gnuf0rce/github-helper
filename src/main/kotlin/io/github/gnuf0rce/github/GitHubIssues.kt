package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.model.*
import io.ktor.http.*

/**
 * with https://api.github.com/issues
 */
class GitHubIssues(override val github: GitHubClient = GitHubClient()) :
    WithGithubClient, IssuesMapper(Url("https://api.github.com"), github)