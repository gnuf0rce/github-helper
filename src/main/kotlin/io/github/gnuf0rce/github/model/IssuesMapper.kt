package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.OffsetDateTime

open class IssuesMapper(parent: Url, override val github: GithubClient) : GithubMapper(parent, "issues") {

    @Serializable
    data class Context(
        val filter: IssueFilter = IssueFilter.assigned,
        val state: STATE = STATE.open,
        val sort: IssueSort = IssueSort.created,
        val direction: Direction = Direction.desc,
        @Contextual
        val since: OffsetDateTime? = null,
//        collab: Boolean,
//        orgs: Boolean,
//        owned: Boolean,
//        pulls: Boolean,
    )

    open suspend fun list(page: Int, per: Int = 30, context: Context? = null) = page<Context, Issue>(page, per, context)

    // TODO context
    open suspend fun new(context: JsonObject) = post<JsonObject, Issue>(context)

    open suspend fun get(index: Int) = get<Issue>("$index")

    open suspend fun patch(index: Int, context: JsonObject) = patch<JsonObject, Issue>(context, "$index")

    open suspend fun lock(index: Int, context: JsonObject) = put<JsonObject, Issue>(context, "$index/lock")

    open suspend fun unlock(index: Int): Issue = delete("$index/lock")
}