package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*

/**
 * 1. [https://api.github.com/orgs/{org}]
 * 2. [https://api.github.com/orgs/{org}/repos]
 */
open class OrganizationMapper(parent: Url, override val github: GitHubClient) :
    GitHubMapper(parent, "") {

    open suspend fun get() = get<Organization>()

    open suspend fun repos(page: Int, per: Int = 30) = page<Repo>(page, per, "repos")
}
