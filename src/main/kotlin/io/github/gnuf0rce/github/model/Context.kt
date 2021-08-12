package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*

interface WithGithubClient {
    val base: Url
    val github: GithubClient
}

internal fun Url.path(path: String) = copy(encodedPath = encodedPath + "/${path}")

abstract class GithubMapper(parent: Url, path: String) : WithGithubClient {
    final override val base: Url = if (path.isEmpty()) parent else parent.path(path)
}

internal suspend inline fun <reified R> WithGithubClient.rest(
    path: String = "",
    crossinline block: HttpRequestBuilder.() -> Unit
): R = github.useHttpClient { client ->
    client.request(
        url = base.path(path),
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
): List<R> = rest(path) {
    method = HttpMethod.Get
    parameter("per_page", per)
    parameter("page", page)
}

internal suspend inline fun <reified T, reified R> WithGithubClient.page(
    page: Int,
    per: Int,
    context: T? = null,
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
    val json = context as? JsonObject ?: GithubJson.encodeToJsonElement(context).jsonObject
    when (method) {
        HttpMethod.Get, HttpMethod.Delete -> {
            json.forEach { (key, element) ->
                parameter(key, element.jsonPrimitive.content)
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

