/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github

internal val FULL_REGEX = """([\w-.]+)/([\w-.]+)""".toRegex()

public fun GitHubClient.repo(owner: String, repo: String): GitHubRepo = GitHubRepo(
    owner = owner,
    repo = repo,
    github = this
)

public fun GitHubClient.repo(full: String): GitHubRepo {
    val (owner, repo) = requireNotNull(FULL_REGEX.find(full)) { "Not Found FullName." }.destructured
    return GitHubRepo(owner = owner, repo = repo, github = this)
}

public fun GitHubClient.user(login: String): GitHubUser = GitHubUser(user = login, github = this)

public fun GitHubClient.organization(login: String): GitHubOrganization = GitHubOrganization(org = login, github = this)

public fun GitHubClient.current(): GitHubCurrent = GitHubCurrent(github = this)