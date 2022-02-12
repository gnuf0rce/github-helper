package io.github.gnuf0rce.mirai.plugin.command

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*

object GitHubStatsCommand : CompositeCommand(
    owner = GitHubHelperPlugin,
    "stats",
    description = "User Stats"
), GitHubCommand {

    @SubCommand
    suspend fun UserCommandSender.card(name: String) {
        val message = try {
            val user = github.user(name).get()
            user.card(subject)
        } catch (cause: Throwable) {
            (cause.message ?: cause.toString()).toPlainText()
        }
        sendMessage(message)
    }

    @SubCommand
    suspend fun UserCommandSender.contribution(name: String) {
        val message = try {
            val user = github.user(name).get()
            user.contribution(subject)
        } catch (cause: Throwable) {
            (cause.message ?: cause.toString()).toPlainText()
        }
        sendMessage(message)
    }

    @SubCommand
    suspend fun UserCommandSender.trophy(name: String) {
        val message = try {
            val user = github.user(name).get()
            user.trophy(subject)
        } catch (cause: Throwable) {
            (cause.message ?: cause.toString()).toPlainText()
        }
        sendMessage(message)
    }
}