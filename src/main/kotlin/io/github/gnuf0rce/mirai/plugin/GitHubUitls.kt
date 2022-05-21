/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.data.*
import io.ktor.http.*
import net.mamoe.mirai.utils.*
import org.openqa.selenium.remote.*
import xyz.cssxsh.mirai.plugin.*
import java.io.*
import java.net.*
import java.util.*
import kotlin.coroutines.*

internal const val LOGGER_PROPERTY = "io.github.gnuf0rce.mirai.plugin.logger"

internal const val IMAGE_FOLDER_PROPERTY = "io.github.gnuf0rce.mirai.plugin.image"

/**
 * @see [LOGGER_PROPERTY]
 * @see [GitHubHelperPlugin.logger]
 */
internal val logger by lazy {
    val open = System.getProperty(LOGGER_PROPERTY, "${true}").toBoolean()
    if (open) GitHubHelperPlugin.logger else SilentLogger
}

/**
 * @see [IMAGE_FOLDER_PROPERTY]
 * @see [GitHubHelperPlugin.dataFolder]
 */
internal val ImageFolder by lazy {
    val path = System.getProperty(IMAGE_FOLDER_PROPERTY)
    (if (path.isNullOrBlank()) GitHubHelperPlugin.dataFolder else File(path)).resolve("image")
}

internal const val UserAvatarSize = 50

internal const val TextMaxLength = 200

/**
 * @see [GitHubClient]
 * @see [GitHubConfig]
 */
internal val github by lazy {
    object : GitHubClient(token = GitHubConfig.token.takeIf { it.isNotBlank() }) {
        init {
            proxy = GitHubConfig.proxy.takeIf { it.isNotBlank() }?.let(::Url)?.toProxy()
            timeout = GitHubConfig.timeout * 1000
        }

        override val coroutineContext: CoroutineContext = try {
            GitHubHelperPlugin.childScopeContext(name = "github-client")
        } catch (_: Throwable) {
            EmptyCoroutineContext.childScopeContext(name = "github-client")
        }

        override val ignore: (Throwable) -> Boolean = {
            when (it) {
                is IOException -> {
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

internal val repos: MutableMap<String, GitHubRepo> = WeakHashMap()

internal fun repo(full: String): GitHubRepo = repos.getOrPut(full) { github.repo(full) }

internal fun repo(owner: String, repo: String): GitHubRepo = repos.getOrPut("$owner/repo") { github.repo(owner, repo) }

internal val selenium: Boolean by lazy {
    try {
        MiraiSeleniumPlugin.setup()
    } catch (exception: NoClassDefFoundError) {
        logger.warning { "相关类加载失败，请安装 https://github.com/cssxsh/mirai-selenium-plugin $exception" }
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
