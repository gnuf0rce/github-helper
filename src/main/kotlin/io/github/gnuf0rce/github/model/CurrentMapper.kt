package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * 1. [https://api.github.com/issues]
 * 2. [https://api.github.com/user]
 * 3. [https://api.github.com/rate_limit]
 */
open class CurrentMapper(parent: Url, override val github: GitHubClient = GitHubClient()) :
    GitHubMapper(parent, "") {

    suspend fun issues(page: Int, per: Int = 30, context: JsonObject? = null) =
        page<JsonObject, Issue>(page, per, context, "issues")

    suspend fun user() = get<Owner>("user")

    suspend fun rate() = get<RateLimit>("rate_limit")
}