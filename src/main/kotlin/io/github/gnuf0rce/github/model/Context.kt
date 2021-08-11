package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.Issue
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*

interface WithGithubClient {
    val github: GithubClient
}

suspend inline fun <reified T> WithGithubClient.http(url: String, crossinline block: HttpRequestBuilder.() -> Unit): T {
    return github.useHttpClient { client -> client.request(url, block) }
}

internal fun HttpRequestBuilder.context(context: Any?) {
    if (context == null) return
    val json = context as? JsonObject ?: GithubJson.encodeToJsonElement(context).jsonObject
    when (method) {
        HttpMethod.Get, HttpMethod.Head, HttpMethod.Delete -> {
            json.forEach { (key, element) ->
                parameter(key, element.jsonPrimitive.content)
            }
        }
        HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch -> {
            body = context
            contentType(ContentType.Application.Json)
        }
        else -> {
            body
        }
    }
}