package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.model.*
import io.ktor.http.*

/**
 * @see [OrganizationMapper]
 */
class GitHubOrganization(val org: String, override val github: GitHubClient = GitHubClient()) :
    WithGithubClient, OrganizationMapper(Url("https://api.github.com/orgs/${org}"), github)