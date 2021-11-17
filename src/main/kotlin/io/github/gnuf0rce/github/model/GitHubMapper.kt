package io.github.gnuf0rce.github.model

import io.ktor.http.*

sealed class GitHubMapper(parent: Url, path: String) : WithGithubClient {
    final override val base: Url = parent.resolve(path)
}