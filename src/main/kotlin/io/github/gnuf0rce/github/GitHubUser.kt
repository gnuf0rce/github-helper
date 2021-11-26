package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.model.*
import io.ktor.http.*

/**
 * @see [UserMapper]
 */
class GitHubUser(val user: String, override val github: GitHubClient = GitHubClient()) :
    WithGithubClient, UserMapper(Url("https://api.github.com/users/${user}"), github)