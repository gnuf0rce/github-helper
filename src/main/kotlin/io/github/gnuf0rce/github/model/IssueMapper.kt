package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.time.OffsetDateTime

open class IssueMapper(private val base: String, override val github: GithubClient): WithGithubClient {

    @Serializable
    data class Context(
        val filter: IssueFilter = IssueFilter.assigned,
        val state: STATE = STATE.open,
        val sort: IssueSort = IssueSort.created,
        val direction: Direction = Direction.desc,
        @Serializable(OffsetDateTimeSerializer::class)
        val since: OffsetDateTime? = null,
//        collab: Boolean,
//        orgs: Boolean,
//        owned: Boolean,
//        pulls: Boolean,
    )

    open suspend fun list(page: Int, per: Int = 30, context: Context? = null): List<Issue> = http(base) {
        method = HttpMethod.Get
        context(context)
        parameter("per_page", per)
        parameter("page", page)
    }

    // TODO context
    open suspend fun new(context: JsonObject): Issue = http(base) {
        method = HttpMethod.Post
        context(context)
    }

    open suspend fun get(index: Int): Issue = http("$base/$index") {
        method = HttpMethod.Get
    }

    open suspend fun patch(index: Int, context: JsonObject): Issue = http("$base/$index") {
        method = HttpMethod.Patch
        context(context)
    }

    open suspend fun lock(index: Int, context: JsonObject): Issue = http("$base/$index/lock") {
        method = HttpMethod.Put
        context(context)
    }

    open suspend fun unlock(index: Int): Issue = http("$base/$index/lock") {
        method = HttpMethod.Delete
    }
}