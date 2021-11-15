package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.model.*
import io.ktor.http.*

/**
 * 1. [https://api.github.com/issues]
 * 2. [https://api.github.com/user]
 */
class GitHubCurrent(override val github: GitHubClient = GitHubClient()) :
    WithGithubClient, CurrentMapper(Url("https://api.github.com"))