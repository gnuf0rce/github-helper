/*
 * Copyright 2021-2024 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github.command

import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.github.*
import io.github.gnuf0rce.mirai.github.data.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import java.time.*

public object GitHubIssuesCommand : CompositeCommand(
    owner = GitHubHelperPlugin,
    primaryName = "issues",
    description = "Issues Notice"
) {
    private val subscriber = object : GitHubSubscriber<Issue>(primaryName) {
        override val tasks: MutableMap<String, GitHubTask> by GitHubTaskData::issues

        override val regex: Regex? = null

        override suspend fun GitHubTask.load(per: Int, since: OffsetDateTime): List<Issue> {
            return current.issues(per = per) { this.since = since }.filter { it.updatedAt > since }
        }
    }

    @SubCommand
    public suspend fun CommandSender.add(contact: Contact = context()) {
        subscriber.add("current", contact.id)
        sendMessage("current with issue 添加完成")
    }

    @SubCommand
    public suspend fun CommandSender.remove(contact: Contact = context()) {
        subscriber.remove("current", contact.id)
        sendMessage("current with issue 移除完成")
    }

    @SubCommand
    public suspend fun CommandSender.interval(millis: Long) {
        subscriber.interval("current", millis)
        sendMessage("current interval ${millis}ms with issue")
    }

    @SubCommand
    public suspend fun CommandSender.format(type: Format) {
        subscriber.format("current", type)
        sendMessage("current format $type with issue")
    }

    @SubCommand
    public suspend fun CommandSender.list(contact: Contact = context()) {
        sendMessage(subscriber.list(contact.id))
    }

    @SubCommand
    public suspend fun CommandSender.test(type: Format, contact: Contact = context()) {
        sendMessage(subscriber.test("current", contact.id, type))
    }
}