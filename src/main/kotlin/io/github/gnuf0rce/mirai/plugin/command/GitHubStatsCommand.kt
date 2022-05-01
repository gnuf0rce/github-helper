package io.github.gnuf0rce.mirai.plugin.command

import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import org.openqa.selenium.*
import java.util.*

object GitHubStatsCommand : CompositeCommand(
    owner = GitHubHelperPlugin,
    "stats",
    description = "User Stats"
), GitHubCommand {

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
    suspend fun UserCommandSender.card(name: String) {
        val message = try {
            user(name).card(subject)
        } catch (cause: Throwable) {
            cause.rawMessage.toPlainText()
        }
        sendMessage(message)
    }

    @SubCommand
    suspend fun UserCommandSender.contribution(name: String) {
        val message = try {
            user(name).contribution(subject)
        } catch (cause: Throwable) {
            cause.rawMessage.toPlainText()
        }
        sendMessage(message)
    }

    @SubCommand
    suspend fun UserCommandSender.trophy(name: String) {
        val message = try {
            user(name).trophy(subject)
        } catch (cause: Throwable) {
            cause.rawMessage.toPlainText()
        }
        sendMessage(message)
    }
}