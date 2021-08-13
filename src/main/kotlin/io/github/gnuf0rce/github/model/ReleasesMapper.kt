package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * 1. https://api.github.com/repos/{owner}/{repo}/releases
 */
open class ReleasesMapper(parent: Url, override val github: GitHubClient) : GitHubMapper(parent, "releases") {
    open suspend fun list(page: Int, per: Int = 30, context: JsonObject? = null) =
        page<JsonObject, Release>(page, per, context)

    open suspend fun latest() = get<JsonObject>("latest")

    open suspend fun new(context: JsonObject) = post<JsonObject, Release>(context)
}