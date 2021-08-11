package io.github.gnuf0rce.github

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.io.*
import java.net.*

open class GithubClient(open val token: String? = System.getenv("GITHUB_TOKEN")) : CoroutineScope, Closeable {

    protected open val proxy: Proxy? = null

    protected open val timeout: Long = 30 * 1000L

    protected open val client = HttpClient(OkHttp) {
        BrowserUserAgent()
        ContentEncoding {
            gzip()
            deflate()
            identity()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        Json {
            serializer = KotlinxSerializer(GithubJson)
            accept(ContentType.parse("application/vnd.github.v3+json"))
        }
        defaultRequest {
            header(HttpHeaders.Authorization, token?.let { "token $it" })
        }
        ResponseObserver {
            // TODO record rate-limit
        }
        engine {
            config {
                proxy(this@GithubClient.proxy)
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
            runCatching {
                block(client)
            }.onSuccess {
                return@supervisorScope it
            }.onFailure { throwable ->
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