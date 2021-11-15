package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.command.*
import io.github.gnuf0rce.mirai.plugin.data.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*

object GitHubHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.gnuf0rce.github-helper",
        name = "github-helper",
        version = "1.1.3",
    ) {
        author("cssxsh")
    }
) {

    override fun PluginComponentStorage.onLoad() {
        System.setProperty(IGNORE_UNKNOWN_KEYS, "true")
    }

    override fun onEnable() {
        GitHubConfig.reload()
        GitHubRepoTaskData.reload()
        GitHubTaskData.reload()

        for (command in GitHubCommand) {
            command.register()
        }

        globalEventChannel().subscribeOnce<BotOnlineEvent> {
            GitHubSubscriber.start()
        }
    }

    override fun onDisable() {
        for (command in GitHubCommand) {
            command.unregister()
        }
        GitHubSubscriber.stop()
    }
}