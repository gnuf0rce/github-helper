package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * 1. [https://api.github.com/repos/{owner}/{repo}/pulls]
 */
open class PullsMapper(parent: Url, override val github: GitHubClient) : GitHubMapper(parent, "pulls") {

    open suspend fun list(page: Int, per: Int = 30, context: JsonObject? = null) =
        page<JsonObject, Pull>(page, per, context)

    // TODO context
    open suspend fun new(context: JsonObject) = post<JsonObject, Pull>(context)
}