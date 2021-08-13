package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.ktor.http.*
import kotlinx.serialization.json.*

open class CommentsMapper(parent: Url, override val github: GitHubClient) : GitHubMapper(parent, "comments") {

    open suspend fun list(page: Int, per: Int = 30) = page<JsonObject>(page, per)

    open suspend fun get(id: Long) = get<JsonObject>("$id")

    open suspend fun patch(id: Long, context: JsonObject) = patch<JsonObject, JsonObject>(context, "$id")

    open suspend fun delete(id: Long) = delete<Unit>("$id")
}