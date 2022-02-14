package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.exception.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.io.*
import java.net.*

open class GitHubClient(open val token: String?) : CoroutineScope, Closeable {

    protected var proxy: Proxy? = null

    protected var timeout: Long = 30 * 1000L

    protected open val client = HttpClient(OkHttp) {
        BrowserUserAgent()
        ContentEncoding()
        install(HttpTimeout) {
            socketTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            requestTimeoutMillis = null
        }
        Json {
            serializer = KotlinxSerializer(GitHubJson)
            accept(ContentType.Application.GitHubJson)
        }
        defaultRequest {
            accept(ContentType.Text.Html)
            accept(ContentType.Text.Plain)
            accept(ContentType.Application.GitHubJson)
            header(HttpHeaders.Authorization, token?.let { "token $it" })
        }
        HttpResponseValidator {
            handleResponseException { cause ->
                if (cause is ClientRequestException && "documentation_url" in cause.message) {
                    throw GitHubApiException(
                        cause, try {
                            cause.response.call.save().response.receive()
                        } catch (_: Throwable) {
                            val json = cause.message.substringAfter("Text: \"").removeSuffix("\"")
                            GitHubJson.decodeFromString(ApiError.serializer(), json)
                        }
                    )
                }
            }
        }
        RateLimit {
            notice = { _, _ ->
                //
            }
        }
        engine {
            config {
                proxy(this@GitHubClient.proxy)
            }
        }
    }

    override val coroutineContext get() = client.coroutineContext

    override fun close() = client.close()

    protected open val ignore: (Throwable) -> Boolean = { it is IOException || it is HttpRequestTimeoutException }

    protected open val maxIgnoreCount = 20

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        var count = 0
        while (isActive) {
            try {
                return@supervisorScope block(client)
            } catch (throwable: Throwable) {
                if (isActive && ignore(throwable)) {
                    if (++count > maxIgnoreCount) {
                        throw throwable
                    }
                } else {
                    throw throwable
                }
            }
        }
        throw CancellationException(null, null)
    }
}