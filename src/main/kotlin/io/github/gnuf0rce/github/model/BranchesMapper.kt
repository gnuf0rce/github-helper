package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * 1. https://api.github.com/repos/{owner}/{repo}/branches
 */
open class BranchesMapper(parent: Url, override val github: GitHubClient) : GitHubMapper(parent, "branches") {
    open suspend fun list(protected: Boolean, page: Int, per: Int = 30) =
        page<Map<String, Boolean>, JsonObject>(page, per, mapOf("protected" to protected))

    open suspend fun get(name: String) = get<JsonObject>(name)

    open suspend fun protection(branch: String) = BranchProtectionMapper(base, branch, github)
}