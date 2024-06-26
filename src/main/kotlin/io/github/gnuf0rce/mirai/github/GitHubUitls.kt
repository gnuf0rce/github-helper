/*
 * Copyright 2021-2024 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.github.data.*
import io.ktor.http.*
import kotlinx.coroutines.*
import net.mamoe.mirai.utils.*
import org.openqa.selenium.remote.*
import xyz.cssxsh.mirai.selenium.*
import java.io.*
import java.net.*
import java.util.*
import kotlin.coroutines.*

@PublishedApi
internal const val IMAGE_FOLDER_PROPERTY: String = "io.github.gnuf0rce.mirai.github.image"

@PublishedApi
internal const val CACHE_FOLDER_PROPERTY: String = "io.github.gnuf0rce.mirai.github.cache"

/**
 * @see [GitHubHelperPlugin.logger]
 */
internal val logger by lazy {
    try {
        GitHubHelperPlugin.logger
    } catch (_: ExceptionInInitializerError) {
        MiraiLogger.Factory.create(GitHubSubscriber::class)
    }
}

/**
 * @see [IMAGE_FOLDER_PROPERTY]
 * @see [GitHubHelperPlugin.dataFolder]
 */
internal val ImageFolder by lazy {
    File(System.getProperty(IMAGE_FOLDER_PROPERTY, ".image"))
}

/**
 * @see [CACHE_FOLDER_PROPERTY]
 * @see [GitHubHelperPlugin.dataFolder]
 */
internal val CacheFolder by lazy {
    File(System.getProperty(CACHE_FOLDER_PROPERTY, ".cache"))
}


internal const val UserAvatarSize = 50

internal const val TextMaxLength = 200

/**
 * @see [GitHubClient]
 * @see [GitHubConfig]
 */
internal val github by lazy {
    object : GitHubClient(token = GitHubConfig.token.ifEmpty { null }) {
        override val proxy: Proxy? get() = GitHubConfig.proxy.ifEmpty { null }?.let(::Url)?.toProxy()
        override val timeout: Long get() =  GitHubConfig.timeout * 1000
        override val doh: String get() = GitHubConfig.doh
        override val ipv6: Boolean get() = GitHubConfig.ipv6

        override val coroutineContext: CoroutineContext =
            CoroutineName(name = "github-client") + SupervisorJob() + CoroutineExceptionHandler { context, throwable ->
                logger.warning({ "$throwable in $context" }, throwable)
            }

        override val ignore: (Throwable) -> Boolean = {
            when (it) {
                is UnknownHostException,
                is NoRouteToHostException -> false
                is SocketTimeoutException -> {
                    logger.warning { "HttpClient Ignore ${it.message}" }
                    true
                }
                is IOException -> {
                    val message = it.message
                    when {
                        message == null -> Unit
                        message == "Connection reset" -> {
                            logger.warning { "HttpClient Ignore ${it.message}" }
                        }
                        message.startsWith("Failed to connect to") -> {
                            logger.warning { "HttpClient Ignore ${it.message}" }
                        }
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}

internal val repos: MutableMap<String, GitHubRepo> = WeakHashMap()

internal fun repo(full: String): GitHubRepo = repos.getOrPut(full) { github.repo(full) }

internal fun repo(owner: String, repo: String): GitHubRepo = repos.getOrPut("$owner/$repo") { github.repo(owner, repo) }

internal val selenium: Boolean by lazy {
    try {
        MiraiSeleniumPlugin.setup()
    } catch (error: NoClassDefFoundError) {
        logger.warning { "相关类加载失败，请安装 https://github.com/cssxsh/mirai-selenium-plugin $error" }
        false
    }
}

internal inline fun <reified T> useRemoteWebDriver(block: (RemoteWebDriver) -> T): T {
    val driver = MiraiSeleniumPlugin.driver()
    return try {
        block(driver)
    } finally {
        driver.quit()
    }
}

internal fun Url.toProxy(): Proxy {
    val type = when (protocol) {
        URLProtocol.SOCKS -> Proxy.Type.SOCKS
        URLProtocol.HTTP -> Proxy.Type.HTTP
        else -> throw IllegalArgumentException("不支持的代理类型, $protocol")
    }
    return Proxy(type, InetSocketAddress(host, port))
}
