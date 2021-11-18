package io.github.gnuf0rce.mirai.plugin.command

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.*
import net.mamoe.mirai.console.command.*

object GitHubStatsCommand : CompositeCommand(
    owner = GitHubHelperPlugin,
    "stats",
    description = "Stats Notice"
), GitHubCommand {

    @SubCommand
    suspend fun UserCommandSender.card(name: String) {
        val user = github.user(name).get()

        sendMessage(user.toMessage(subject))
    }
}