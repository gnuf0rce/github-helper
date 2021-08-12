package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*
import kotlinx.serialization.json.*

open class CollaboratorsMapper(parent: Url, override val github: GithubClient) :
    GithubMapper(parent, "collaborators") {

    open suspend fun list(page: Int, per: Int = 30, affiliation: Affiliation = Affiliation.all) =
        page<Map<String, Affiliation>, JsonObject>(page, per, mapOf("affiliation" to affiliation), "contributors")

    // TODO context
    open suspend fun new(context: JsonObject) = post<JsonObject, JsonObject>(context)
}