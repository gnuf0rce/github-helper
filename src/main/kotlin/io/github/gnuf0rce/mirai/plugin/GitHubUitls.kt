package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.data.*
import io.ktor.client.features.*
import io.ktor.http.*
import net.mamoe.mirai.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.utils.*
import java.io.*
import java.net.*
import java.time.*

internal val logger by lazy {
    val open = System.getProperty("io.github.gnuf0rce.mirai.plugin.logger", "${true}").toBoolean()
    if (open) GitHubHelperPlugin.logger else SilentLogger
}

internal val ImageFolder by lazy {
    val dir = System.getProperty("io.github.gnuf0rce.mirai.plugin.dir")
    (if (dir.isNullOrBlank()) GitHubHelperPlugin.dataFolder else File(dir)).resolve("image")
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

internal fun Contact(id: Long) = Bot.instancesSequence.flatMap { it.groups + it.friends }.first { it.id == id }

internal fun CommandSender.Contact() = requireNotNull(subject) { "无法从当前环境获取联系人" }