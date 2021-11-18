package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.OffsetDateTime

/**
 * 1. [https://api.github.com/repos/{owner}/{repo}/issues]
 */
open class IssuesMapper(parent: Url, override val github: GitHubClient) :
    GitHubMapper(parent, "issues") {

    @Serializable
    data class Context(
        @SerialName("filter")
        val filter: IssueFilter = IssueFilter.assigned,
        @SerialName("state")
        val state: State = State.open,
        @SerialName("sort")
        val sort: IssueSort = IssueSort.created,
        @SerialName("direction")
        val direction: Direction = Direction.desc,
        @Contextual
        @SerialName("since")
        val since: OffsetDateTime? = null
    )

    open suspend fun list(page: Int, per: Int = 30, context: Context? = null) = page<Context, Issue>(page, per, context)

    // TODO context
    open suspend fun new(context: JsonObject) = post<JsonObject, Issue>(context)

    open suspend fun get(index: Int) = get<Issue>("$index")

    open suspend fun patch(index: Int, context: JsonObject) = patch<JsonObject, Issue>(context, "$index")

    open suspend fun lock(index: Int, context: JsonObject) = put<JsonObject, Issue>(context, "$index/lock")

    open suspend fun unlock(index: Int): Issue = delete("$index/lock")
}