package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * with https://api.github.com/issues
 */
open class CurrentMapper(parent: Url, override val github: GitHubClient = GitHubClient()) : GitHubMapper(parent, "") {

    suspend fun issues(page: Int, per: Int = 30, context: JsonObject? = null) =
        page<JsonObject, Issue>(page, per, context, "issues")
}