package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.model.*
import io.ktor.http.*

/**
 * 1. [https://api.github.com/repos/{owner}/{repo}]
 */
class GitHubRepo(val owner: String, val repo: String, override val github: GitHubClient = GitHubClient()) :
    WithGithubClient, RepoMapper(Url("https://api.github.com/repos/${owner}/${repo}"), github)