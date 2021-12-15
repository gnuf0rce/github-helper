package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.data.*
import io.ktor.client.features.*
import io.ktor.http.*
import net.mamoe.mirai.utils.*
import org.openqa.selenium.remote.*
import xyz.cssxsh.mirai.plugin.*
import java.io.*
import java.net.*

const val LOGGER_PROPERTY = "io.github.gnuf0rce.mirai.plugin.logger"

const val IMAGE_FOLDER_PROPERTY = "io.github.gnuf0rce.mirai.plugin.image"

const val REPLY_TYPE_PROPERTY = "io.github.gnuf0rce.mirai.plugin.reply"

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

/**
 * @see [REPLY_TYPE_PROPERTY]
 * @see [MessageType]
 */
internal val reply by lazy {
    System.getProperty(REPLY_TYPE_PROPERTY)
        ?.let(MessageType::valueOf)
        ?: GitHubConfig.reply
}

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

internal val selenium: Boolean by lazy {
    try {
        MiraiSeleniumPlugin.setup()
    } catch (exception: NoClassDefFoundError) {
        logger.warning { "相关类加载失败，请安装 https://github.com/cssxsh/mirai-selenium-plugin $exception" }
        false
    }
}

// XXX: ...
internal inline fun <reified T> useRemoteWebDriver(block: (RemoteWebDriver) -> T): T {
    val driver = MiraiSeleniumPlugin.driver()
    return try {
        block(driver)
    } finally {
        driver.quit()
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
