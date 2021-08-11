package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.data.*
import io.ktor.client.features.*
import io.ktor.http.*
import net.mamoe.mirai.utils.*
import java.io.*
import java.net.*

internal val logger by GitHubHelperPlugin::logger

internal val ImageFolder get() = GitHubHelperPlugin.dataFolder.resolve("image")

internal val github = object : GithubClient() {

    override val proxy: Proxy by lazy {
        GitHubConfig.proxy.takeIf { it.isNotBlank() }?.let(::Url)?.toProxy() ?: Proxy.NO_PROXY
    }

    override val token: String? by lazy {
        GitHubConfig.token.takeIf { it.isNotBlank() }
    }

    override val ignore: (Throwable) -> Boolean = {
        when (it) {
            is IOException,
            is HttpRequestTimeoutException -> {
                logger.warning { "RssHttpClient Ignore $it" }
                true
            }
            else -> {
                false
            }
        }
    }
}

internal val Url.filename get() = encodedPath.substringAfterLast('/')

internal fun Url.toProxy(): Proxy {
    val type = when (protocol) {
        URLProtocol.SOCKS -> Proxy.Type.SOCKS
        URLProtocol.HTTP -> Proxy.Type.HTTP
        else -> throw IllegalArgumentException("不支持的代理类型, $protocol")
    }
    return Proxy(type, InetSocketAddress(host, port))
}