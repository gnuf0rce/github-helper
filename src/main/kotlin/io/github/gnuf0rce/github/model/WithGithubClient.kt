/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*

public typealias Temp = JsonObject

public interface WithGithubClient {
    public val base: Url
    public val github: GitHubClient
}

internal fun Url.resolve(path: String): Url {
    return when {
        path.isEmpty() -> this
        encodedPath.endsWith("/") -> copy(encodedPath = encodedPath + path)
        else -> copy(encodedPath = "$encodedPath/$path")
    }
}

internal suspend inline fun <reified R> WithGithubClient.rest(
    path: String = "",
    crossinline block: HttpRequestBuilder.() -> Unit
): R = github.useHttpClient { client ->
    client.request(
        url = base.resolve(path),
        block = block
    )
}

internal suspend inline fun <reified R> WithGithubClient.get(
    path: String = ""
): R = rest(path) {
    method = HttpMethod.Get
}

internal suspend inline fun <reified R> WithGithubClient.page(
    page: Int,
    per: Int,
    path: String = "",
    block: MutableMap<String, Any?>.() -> Unit = {}
): List<R> = page(page, per, HashMap<String, Any?>().apply(block), path)

internal suspend inline fun <reified T, reified R> WithGithubClient.page(
    page: Int,
    per: Int,
    context: T?,
    path: String = "",
): List<R> = rest(path) {
    method = HttpMethod.Get
    parameter("per_page", per)
    parameter("page", page)
    context(context)
}

internal suspend inline fun <reified R> WithGithubClient.delete(
    path: String = ""
): R = rest(path) {
    method = HttpMethod.Delete
}

internal suspend inline fun <reified T, reified R> WithGithubClient.post(
    context: T,
    path: String = ""
): R = rest(path) {
    method = HttpMethod.Post
    context(context)
}

internal suspend inline fun <reified T, reified R> WithGithubClient.put(
    context: T,
    path: String = ""
): R = rest(path) {
    method = HttpMethod.Put
    context(context)
}

internal suspend inline fun <reified T, reified R> WithGithubClient.patch(
    context: T,
    path: String = ""
): R = rest(path) {
    method = HttpMethod.Patch
    context(context)
}

internal suspend inline fun <reified R> WithGithubClient.open(
    open: Boolean,
    path: String = ""
): R = rest(path) {
    method = if (open) HttpMethod.Put else HttpMethod.Delete
}

internal inline fun <reified T> HttpRequestBuilder.context(context: T) {
    if (context == null) return
    when (method) {
        HttpMethod.Get, HttpMethod.Delete -> {
            when (context) {
                is Parameters -> {
                    url.parameters.appendAll(context)
                }
                is JsonObject -> {
                    for ((key, value) in context) {
                        parameter(key, (value as? JsonPrimitive)?.content ?: value)
                    }
                }
                is Query -> {
                    for ((key, value) in context.toJsonObject()) {
                        parameter(key, (value as? JsonPrimitive)?.content ?: value)
                    }
                }
                is Map<*, *> -> {
                    for ((key, value) in context) {
                        parameter(key.toString(), value)
                    }
                }
                else -> {
                    throw IllegalArgumentException("${T::class} can not used as parameters in ${url.buildString()}")
                }
            }
        }
        HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch -> {
            body = context
            contentType(ContentType.Application.Json)
        }
        else -> {
            throw IllegalArgumentException("$method Not Context")
        }
    }
}