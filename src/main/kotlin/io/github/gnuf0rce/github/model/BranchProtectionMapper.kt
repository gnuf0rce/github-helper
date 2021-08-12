package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.GithubClient
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject

open class BranchProtectionMapper(parent: Url, branch: String, override val github: GithubClient) :
    GithubMapper(parent, "${branch}/protection") {

    open suspend fun get() = get<JsonObject>()

    open suspend fun put(context: JsonObject) = put<JsonObject, JsonObject>(context)

    open suspend fun delete() = delete<Unit>()

    open suspend fun admin() = get<JsonObject>("enforce_admins")

    open suspend fun admin(open: Boolean) = open<Unit>(open, "enforce_admins")

    open suspend fun reviews() = get<JsonObject>("required_pull_request_reviews")

    open suspend fun reviews(context: JsonObject) =
        patch<JsonObject, JsonObject>(context, "required_pull_request_reviews")

    open suspend fun reviews_delete() = delete<Unit>("required_pull_request_reviews")

    // open suspend fun signatures():
}