package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*

open class AutoLinksMapper(parent: Url, override val github: GithubClient) : GithubMapper(parent, "autolinks") {

    open suspend fun list(page: Int, per: Int = 30) = page<AutoLink>(page, per)

    open suspend fun new(context: AutoLink) = post<AutoLink, AutoLink>(context)

    open suspend fun get(id: Int) = get<AutoLink>("$id")

    open suspend fun delete(id: Int) = delete<Unit>("$id")
}