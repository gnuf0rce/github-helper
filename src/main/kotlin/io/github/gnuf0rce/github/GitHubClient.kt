/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
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
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.io.*
import java.net.*
import kotlin.coroutines.*

public open class GitHubClient(public open val token: String?) : CoroutineScope, Closeable {

    protected open val proxy: Proxy? = null

    protected open val timeout: Long = 30 * 1000L

    protected open val doh: String = ""

    protected open val ipv6: Boolean = false

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
        defaultRequest {
            accept(ContentType.Text.Html)
            accept(ContentType.Text.Plain)
            accept(GitHubJsonContentType)
            header(HttpHeaders.Authorization, token?.let { "token $it" })
        }
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
                if (doh.isNotEmpty()) {
                    dns(DnsOverHttps.Builder()
                        .client(OkHttpClient())
                        .url(doh.toHttpUrl())
                        .includeIPv6(ipv6)
                        .build()
                    )
                }
            }
        }
    }

    override val coroutineContext: CoroutineContext get() = client.coroutineContext

    override fun close(): Unit = client.close()

    protected open val ignore: (Throwable) -> Boolean = { it is IOException }

    protected open val maxIgnoreCount: Int = 20

    public suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        var count = 0
        var current: Throwable? = null
        while (isActive) {
            try {
                return@supervisorScope block(client)
            } catch (cause: Throwable) {
                current = cause
                if (isActive && ignore(cause)) {
                    if (++count > maxIgnoreCount) {
                        throw cause
                    }
                } else {
                    throw cause
                }
            }
        }
        throw CancellationException(null, current)
    }
}