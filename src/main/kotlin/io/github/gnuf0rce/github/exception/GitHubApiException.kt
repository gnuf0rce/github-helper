package io.github.gnuf0rce.github.exception

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.client.features.*
import kotlinx.serialization.*

class GitHubApiException(cause: ClientRequestException) : IllegalStateException(cause) {
    val json: ApiError by lazy {
        val text = cause.message.orEmpty().substringAfter("Text: \"").removeSuffix("\"")
        GitHubJson.decodeFromString(text)
    }

    override val message: String by lazy {
        "${json.message} ${json.documentationUrl?.let { "doc: $it" } ?: ""}"
    }
}