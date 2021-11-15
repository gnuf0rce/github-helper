package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.data.*
import io.ktor.client.features.*
import io.ktor.http.*
import net.mamoe.mirai.utils.*
import java.io.*
import java.net.*
import java.time.*

const val LOGGER_PROPERTY = "io.github.gnuf0rce.mirai.plugin.logger"

const val IMAGE_FOLDER_PROPERTY = "io.github.gnuf0rce.mirai.plugin.image"

const val REPLY_TYPE_PROPERTY = "io.github.gnuf0rce.mirai.plugin.reply"

internal val logger by lazy {
    val open = System.getProperty(LOGGER_PROPERTY, "${true}").toBoolean()
    if (open) GitHubHelperPlugin.logger else SilentLogger
}

internal val ImageFolder by lazy {
    val path = System.getProperty(IMAGE_FOLDER_PROPERTY)
    (if (path.isNullOrBlank()) GitHubHelperPlugin.dataFolder else File(path)).resolve("image")
}

internal val reply by lazy {
    System.getProperty(REPLY_TYPE_PROPERTY)
        ?.let(MessageType::valueOf)
        ?: GitHubConfig.reply
}

internal val github by lazy {
    object : GitHubClient(token = GitHubConfig.token.takeIf { it.isNotBlank() }) {
        init {
            proxy = GitHubConfig.proxy.takeIf { it.isNotBlank() }?.let(::Url)?.toProxy()
            timeout = GitHubConfig.timeout * 1000
        }

        override val ignore: (Throwable) -> Boolean = {
            when (it) {
                is IOException,
                is HttpRequestTimeoutException -> {
                    logger.warning { "HttpClient Ignore $it" }
                    true
                }
                else -> {
                    false
                }
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

internal val offset by lazy {
    OffsetDateTime.now().offset
}
