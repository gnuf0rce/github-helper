package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.model.*
import io.ktor.http.*

/**
 * with https://api.github.com/issues
 */
class GitHubCurrent(override val github: GitHubClient = GitHubClient()) :
    WithGithubClient, CurrentMapper(Url("https://api.github.com"))