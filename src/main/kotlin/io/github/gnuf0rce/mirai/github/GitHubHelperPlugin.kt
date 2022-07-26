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
import io.github.gnuf0rce.mirai.github.command.*
import io.github.gnuf0rce.mirai.github.data.*
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
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
        version = "1.2.3",
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

    override fun onEnable() {
        // XXX: mirai console version check
        check(SemVersion.parseRangeRequirement(">= 2.12.0-RC").test(MiraiConsole.version)) {
            "$name $version 需要 Mirai-Console 版本 >= 2.12.0，目前版本是 ${MiraiConsole.version}"
        }

        GitHubConfig.reload()
        GitHubRepoTaskData.reload()
        GitHubTaskData.reload()

        for (command in GitHubCommand) command.register()

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