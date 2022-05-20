/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.plugin.command

import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.plugin.*
import io.github.gnuf0rce.mirai.plugin.data.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import java.time.*

public object GitHubRepoPullCommand : CompositeCommand(
    owner = GitHubHelperPlugin,
    "repo-pull",
    description = "Repo Pull Notice"
), GitHubCommand {
    private val subscriber = object : GitHubSubscriber<Pull>(primaryName, GitHubHelperPlugin) {
        override val tasks: MutableMap<String, GitHubTask> by GitHubRepoTaskData::pulls

        override suspend fun GitHubTask.load(per: Int, since: OffsetDateTime): List<Pull> {
            return repo.pulls.list(per = per).filter { it.updatedAt > since }
        }
    }

    @SubCommand
    public suspend fun CommandSender.add(repo: String, contact: Contact = Contact()) {
        subscriber.add(repo, contact.id)
        sendMessage("$repo with pull 添加完成")
    }

    @SubCommand
    public suspend fun CommandSender.remove(repo: String, contact: Contact = Contact()) {
        subscriber.remove(repo, contact.id)
        sendMessage("$repo with pull 移除完成")
    }

    @SubCommand
    public suspend fun CommandSender.interval(repo: String, millis: Long) {
        subscriber.interval(repo, millis)
        sendMessage("$repo interval ${millis}ms with pull")
    }

    @SubCommand
    public suspend fun CommandSender.list(contact: Contact = Contact()) {
        sendMessage(subscriber.list(contact.id))
    }

    @SubCommand
    public suspend fun CommandSender.build(repo: String, contact: Contact = Contact()) {
        sendMessage(subscriber.build(repo, contact.id))
    }
}