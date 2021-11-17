package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.model.*
import io.ktor.http.*


internal val REPO_REGEX = """([\w-]+)/([\w-]+)""".toRegex()

fun GitHubClient.parse(url: Url): GitHubMapper {
    return when {
        url.encodedPath == "/" -> current()
        url.encodedPath.startsWith("/users/") -> user(name = url.encodedPath.substringAfter("/users/"))
        url.encodedPath.startsWith("/repos/") -> repo(full = url.encodedPath.substringAfter("/repos/"))
        else -> throw IllegalArgumentException("无法解析 $url")
    }
}

fun GitHubClient.repo(owner: String, repo: String) = GitHubRepo(owner = owner, repo = repo, github = this)

fun GitHubClient.repo(full: String): GitHubRepo {
    val (owner, repo) = requireNotNull(REPO_REGEX.find(full)) { "Not Found FullName." }.destructured
    return GitHubRepo(owner = owner, repo = repo, github = this)
}

fun GitHubClient.user(name: String) = GitHubUser(user = name, github = this)

fun GitHubClient.current() = GitHubCurrent(github = this)