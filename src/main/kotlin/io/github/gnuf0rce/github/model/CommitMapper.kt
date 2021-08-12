package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

open class CommitMapper(parent: Url, sha: String, override val github: GithubClient) :
    GithubMapper(parent, "commits/$sha") {

    open suspend fun comments(page: Int, per: Int = 30) = page<JsonObject>(page, per, "comments")

    open suspend fun comment(context: JsonObject) = post<JsonObject, JsonObject>(context, "comments")

    open suspend fun heads() = get<JsonArray>("branches-where-head")

    open suspend fun pulls() = get<JsonArray>("pulls")

    // XXX
    open suspend fun ref() = get<JsonObject>("ref")

    open suspend fun compare(base: String, head: String) = get<JsonObject>("compare/${base}...${head}")

    open suspend fun community() = get<JsonObject>("community/profile")
}