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
import io.github.gnuf0rce.mirai.plugin.command.*
import io.github.gnuf0rce.mirai.plugin.data.*
import net.mamoe.mirai.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.*

public object GitHubHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.gnuf0rce.github-helper",
        name = "github-helper",
        version = "1.1.11-M1",
    ) {
        author("cssxsh")

        dependsOn("xyz.cssxsh.mirai.plugin.mirai-selenium-plugin", true)
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-administrator", true)
    }
) {

    override fun PluginComponentStorage.onLoad() {
        System.setProperty(IGNORE_UNKNOWN_KEYS, "true")
    }

    private fun waitOnline(block: () -> Unit) {
        if (Bot.instances.isEmpty()) {
            globalEventChannel().subscribeOnce<BotOnlineEvent> { block() }
        } else {
            block()
        }
    }

    override fun onEnable() {
        GitHubConfig.reload()
        GitHubRepoTaskData.reload()
        GitHubTaskData.reload()

        for (command in GitHubCommand) command.register()

        waitOnline {
            for (subscriber in GitHubSubscriber) subscriber.start()
        }

        logger.info { "url auto reply: /perm add u* ${ReplierPermission.id}" }

        globalEventChannel().subscribeMessages {
            for ((regex, replier) in UrlRepliers) {
                regex findingReply replier
            }
        }
    }

    override fun onDisable() {
        for (command in GitHubCommand) command.unregister()
        for (subscriber in GitHubSubscriber) subscriber.stop()
    }
}