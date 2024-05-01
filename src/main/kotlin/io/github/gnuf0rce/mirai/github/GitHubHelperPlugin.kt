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
import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*

@PublishedApi
internal object GitHubHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.gnuf0rce.github-helper",
        name = "github-helper",
        version = "1.5.0"
    ) {
        author("cssxsh")

        dependsOn("xyz.cssxsh.mirai.plugin.mirai-selenium-plugin", ">= 2.1.0", true)
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-administrator", true)
    }
) {
    init {
        System.setProperty(IGNORE_UNKNOWN_KEYS, "true")
    }

    override fun PluginComponentStorage.onLoad() {
        runAfterStartup {
            for (subscriber in GitHubSubscriber) subscriber.start()
        }
    }

    private val commands: List<Command> by services()
    private val data: List<PluginData> by services()
    private val config: List<PluginConfig> by services()

    override fun onEnable() {

        GitHubConfig.reload()
        GitHubRepoTaskData.reload()
        GitHubTaskData.reload()

        for (command in commands) command.register()
        for (data in data) data.reload()
        for (config in config) config.reload()

        logger.info { "url auto reply: /perm add u* ${ReplierPermission.id}" }

        globalEventChannel().subscribeMessages {
            for ((regex, replier) in UrlRepliers) {
                regex findingReply replier
            }
        }
        val target = resolveConfigFile("update.dict.json")
        logger.info { "1.3.0 起提供从 github 更新<其他插件>的功能, 如有需要, 请编辑:\n ${target.toPath().toUri()}" }
        GitHubReleasePluginUpdater.reload(target)
        if (GitHubConfig.update) {
            GitHubReleasePluginUpdater.update()
        }
    }

    override fun onDisable() {
        for (command in commands) command.unregister()
        for (subscriber in GitHubSubscriber) subscriber.stop()
        coroutineContext.cancelChildren()
    }
}