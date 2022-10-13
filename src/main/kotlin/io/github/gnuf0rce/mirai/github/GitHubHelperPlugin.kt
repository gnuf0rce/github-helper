/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
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
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*

public object GitHubHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.gnuf0rce.github-helper",
        name = "github-helper",
        version = "1.2.8",
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

    @Suppress("INVISIBLE_MEMBER")
    private inline fun <reified T : Any> services(): Lazy<List<T>> = lazy {
        with(net.mamoe.mirai.console.internal.util.PluginServiceHelper) {
            jvmPluginClasspath.pluginClassLoader
                .findServices<T>()
                .loadAllServices()
        }
    }

    private val commands: List<Command> by services()
    private val data: List<PluginData> by services()
    private val config: List<PluginConfig> by services()

    override fun onEnable() {
        // XXX: mirai console version check
        check(SemVersion.parseRangeRequirement(">= 2.12.0-RC").test(MiraiConsole.version)) {
            "$name $version 需要 Mirai-Console 版本 >= 2.12.0，目前版本是 ${MiraiConsole.version}"
        }

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
    }

    override fun onDisable() {
        for (command in commands) command.unregister()
        for (subscriber in GitHubSubscriber) subscriber.stop()
        coroutineContext.cancelChildren()
    }
}