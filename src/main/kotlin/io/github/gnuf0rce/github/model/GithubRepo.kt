package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.ktor.http.*

class GithubRepo(val owner: String, val repo: String, override val github: GithubClient = GithubClient()) :
    WithGithubClient, RepoMapper(Url("https://api.github.com/repos/${owner}/${repo}"), github)