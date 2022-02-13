package io.github.gnuf0rce.github


internal val FULL_REGEX = """([\w-]+)/([\w-]+)""".toRegex()

fun GitHubClient.repo(owner: String, repo: String) = GitHubRepo(owner = owner, repo = repo, github = this)

fun GitHubClient.repo(full: String): GitHubRepo {
    val (owner, repo) = requireNotNull(FULL_REGEX.find(full)) { "Not Found FullName." }.destructured
    return GitHubRepo(owner = owner, repo = repo, github = this)
}

fun GitHubClient.user(login: String) = GitHubUser(user = login, github = this)

fun GitHubClient.organization(login: String) = GitHubOrganization(org = login, github = this)

fun GitHubClient.current() = GitHubCurrent(github = this)