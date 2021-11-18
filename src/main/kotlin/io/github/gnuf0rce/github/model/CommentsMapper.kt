package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*
import kotlinx.serialization.json.*

open class CommentsMapper(parent: Url, override val github: GitHubClient) :
    GitHubMapper(parent, "comments") {

    open suspend fun list(page: Int, per: Int = 30) = page<JsonObject>(page, per)

    open suspend fun get(sha: String) = get<Commit>(sha)

    open suspend fun patch(sha: String, context: JsonObject) = patch<JsonObject, Commit>(context, sha)

    open suspend fun delete(sha: String) = delete<Unit>(sha)
}