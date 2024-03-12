/*
 * Copyright 2021-2024 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.exception.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import java.io.*
import java.net.*
import kotlin.coroutines.*

public open class GitHubClient(public open val token: String?) : CoroutineScope, Closeable {

    protected open val proxy: Proxy? = null

    protected open val timeout: Long = 30 * 1000L

    protected open val doh: String = ""

    protected open val ipv6: Boolean = false

    protected open val auth: Auth.() -> Unit = {
        val tokens = token?.let { BearerTokens(accessToken = it, refreshToken = "") }
        bearer {
            loadTokens {
                tokens
            }
            sendWithoutRequest { request ->
                if (request.url.host == "api.github.com") {
                    request.accept(ContentType.parse("application/vnd.github.v3.full+json"))
                    request.header("X-GitHub-Api-Version", System.getProperty(GITHUB_API_VERSION))
                    true
                } else {
                    false
                }
            }
        }
    }

    protected open val client: HttpClient = HttpClient(OkHttp) {
        BrowserUserAgent()
        ContentEncoding()
        install(HttpTimeout) {
            socketTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            requestTimeoutMillis = null
        }
        install(ContentNegotiation) {
            json(json = GitHubJson)
        }
        Auth(block = auth)
        expectSuccess = true
        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause, _ ->
                if (cause is ClientRequestException && "documentation_url" in cause.message) {
                    throw GitHubApiException(
                        cause, try {
                            cause.response.call.save().response.body()
                        } catch (_: Exception) {
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
                doh(urlString = this@GitHubClient.doh, ipv6 = this@GitHubClient.ipv6)
            }
        }
    }

    override val coroutineContext: CoroutineContext get() = client.coroutineContext

    override fun close(): Unit = client.close()

    protected open val ignore: (Throwable) -> Boolean = { it is IOException }

    protected open val maxIgnoreCount: Int = 20

    public suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        var count = 0
        val exception = CancellationException("github http api")
        while (isActive) {
            try {
                return@supervisorScope block(client)
            } catch (cause: Throwable) {
                if (ignore(cause).not()) {
                    cause.addSuppressed(exception = exception)
                    throw cause
                }
                exception.addSuppressed(exception = cause)
                if (++count > maxIgnoreCount) break
            }
        }
        throw exception
    }
}