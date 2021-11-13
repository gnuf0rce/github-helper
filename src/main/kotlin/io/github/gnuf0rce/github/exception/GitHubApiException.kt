package io.github.gnuf0rce.github.exception

import io.github.gnuf0rce.github.entry.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import kotlinx.coroutines.*

class GitHubApiException(override val cause: ClientRequestException) : IllegalStateException(cause) {
    val json: ApiError = runBlocking { cause.response.receive() }

    override val message: String get() = json.message
}