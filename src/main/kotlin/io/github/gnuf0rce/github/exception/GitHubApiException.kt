package io.github.gnuf0rce.github.exception

import io.github.gnuf0rce.github.entry.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import kotlinx.coroutines.*
import kotlinx.serialization.*

class GitHubApiException(override val cause: ClientRequestException) : IllegalStateException(cause) {
    @OptIn(ExperimentalSerializationApi::class)
    val json: ApiError = runBlocking { cause.response.receive() }

    override val message: String get() = json.message
}