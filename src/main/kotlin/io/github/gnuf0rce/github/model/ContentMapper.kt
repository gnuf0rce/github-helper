package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.GithubClient
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject

open class ContentMapper(parent: Url, path: String, override val github: GithubClient) : GithubMapper(parent, path) {

    open suspend fun get() = get<JsonObject>()

    open suspend fun put(content: JsonObject) = put<JsonObject, JsonObject>(content)

    open suspend fun delete() = delete<JsonObject>()
}