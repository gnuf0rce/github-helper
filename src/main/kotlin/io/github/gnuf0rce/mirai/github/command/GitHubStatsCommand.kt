/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github.command

import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.model.get
import io.github.gnuf0rce.mirai.github.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import org.openqa.selenium.*
import java.util.*

public object GitHubStatsCommand : CompositeCommand(
    owner = GitHubHelperPlugin,
    "stats",
    description = "User Stats"
) {

    private val cache: MutableMap<String, User> = WeakHashMap()

    private suspend fun user(name: String) = cache.getOrPut(name) { github.user(name).get() }

    private val Throwable.rawMessage: String
        get() {
            logger.warning(this)
            return try {
                if (this is WebDriverException) {
                    rawMessage
                } else {
                    message ?: toString()
                }
            } catch (_: NoClassDefFoundError) {
                message ?: toString()
            }
        }

    @SubCommand
    public suspend fun UserCommandSender.card(name: String) {
        val message = try {
            user(name).card(subject)
        } catch (cause: Exception) {
            cause.rawMessage.toPlainText()
        }
        sendMessage(message)
    }

    @SubCommand
    public suspend fun UserCommandSender.contribution(name: String) {
        val message = try {
            user(name).contribution(subject)
        } catch (cause: Exception) {
            cause.rawMessage.toPlainText()
        }
        sendMessage(message)
    }

    @SubCommand
    public suspend fun UserCommandSender.trophy(name: String) {
        val message = try {
            user(name).trophy(subject)
        } catch (cause: Exception) {
            cause.rawMessage.toPlainText()
        }
        sendMessage(message)
    }
}