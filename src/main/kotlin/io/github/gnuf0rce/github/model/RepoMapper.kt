package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.ktor.http.*
import kotlinx.serialization.json.*

open class RepoMapper(parent: Url, override val github: GithubClient) : GithubMapper(parent, "") {

    open suspend fun get() = get<JsonObject>()

    open suspend fun patch(context: JsonObject) = patch<JsonObject, JsonObject>(context)

    open suspend fun delete() = delete<Unit>()

    open suspend fun fixes(open: Boolean) = open<JsonObject>(open, "automated-security-fixes")

    open suspend fun contributors(page: Int, per: Int = 30, anon: Boolean = false) =
        page<Map<String, Boolean>, JsonObject>(page, per, mapOf("anon" to anon), "contributors")

    open suspend fun dispatches(context: JsonObject) = post<JsonObject, Unit>(context, "dispatches")

    open suspend fun languages() = get<Map<String, Long>>("languages")

    open suspend fun tags(page: Int, per: Int = 30) = page<JsonObject>(page, per, "tags")

    open suspend fun teams(page: Int, per: Int = 30) = page<JsonObject>(page, per, "teams")

    open suspend fun topics(page: Int, per: Int = 30) = get<Map<String, List<String>>>("topics")["names"]

    open suspend fun topics(names: List<String>) =
        put<Map<String, List<String>>, Map<String, List<String>>>(mapOf("names" to names), "topics")["names"]

    open suspend fun transfer(context: JsonObject) = post<JsonObject, JsonObject>(context, "transfer")

    open suspend fun alerts() = get<Unit>("vulnerability-alerts")

    open suspend fun alerts(open: Boolean) = open<Unit>(open, "vulnerability-alerts")

    open suspend fun generate(context: JsonObject) = post<JsonObject, JsonObject>(context, "generate")

    open val issues by lazy { IssuesMapper(base, github) }

    open val pulls by lazy { PullsMapper(base, github) }

    open val autolinks by lazy { AutoLinksMapper(base, github) }

    open val branches by lazy { BranchesMapper(base, github) }

    open val collaborators by lazy { CollaboratorsMapper(base, github) }

    open val comments by lazy { CommentsMapper(base, github) }

    open val releases by lazy { ReleasesMapper(base, github) }

    open suspend fun commits(page: Int, per: Int = 30) = page<JsonObject>(page, per, "commits")

    open fun commit(sha: String) = CommitMapper(base, sha, github)

    open fun context(path: String) = ContentMapper(base, path, github)

    open suspend fun readme(dir: String = "") = get<JsonObject>("readme/$dir")
}
