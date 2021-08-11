package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*

open class PullMapper(private val base: String, override val github: GithubClient): WithGithubClient {
    open suspend fun list(page: Int, per: Int = 30, context: IssueMapper.Context? = null): List<Pull> = http(base) {
        method = HttpMethod.Get
        context(context)
        parameter("per_page", per)
        parameter("page", page)
    }

    // TODO context
    open suspend fun new(context: JsonObject): Pull = http(base) {
        method = HttpMethod.Post
        context(context)
    }

}