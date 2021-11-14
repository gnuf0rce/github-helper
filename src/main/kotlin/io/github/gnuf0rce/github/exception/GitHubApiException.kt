package io.github.gnuf0rce.github.exception

import io.github.gnuf0rce.github.entry.*
import io.ktor.client.features.*

class GitHubApiException(override val cause: ResponseException, val json: ApiError) : IllegalStateException(cause) {

    override val message: String get() = json.message
}