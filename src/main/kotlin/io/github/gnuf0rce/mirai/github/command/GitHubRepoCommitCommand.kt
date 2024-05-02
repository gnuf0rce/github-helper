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

public object GitHubRepoCommitCommand : CompositeCommand(
    owner = GitHubHelperPlugin,
    primaryName = "repo-commit",
    description = "Repo Commit Notice"
) {
    private val subscriber = object : GitHubSubscriber<Commit>(primaryName) {
        override val tasks: MutableMap<String, GitHubTask> by GitHubRepoTaskData::commits
        override val regex: Regex = """([\w-.]+)/([\w-.]+)(?::(.+))?""".toRegex()

        override suspend fun GitHubTask.load(per: Int, since: OffsetDateTime): List<Commit> {
            val tree = id.substringAfter(':').ifEmpty { null }
            return repo.commits(per = per, sha = tree).filter { it.createdAt > since }
        }
    }

    @SubCommand
    public suspend fun CommandSender.add(repo: String, contact: Contact = context()) {
        subscriber.add(repo, contact.id)
        sendMessage("$repo with commit 添加完成")
    }

    @SubCommand
    public suspend fun CommandSender.remove(repo: String, contact: Contact = context()) {
        subscriber.remove(repo, contact.id)
        sendMessage("$repo with commit 移除完成")
    }

    @SubCommand
    public suspend fun CommandSender.interval(repo: String, millis: Long) {
        subscriber.interval(repo, millis)
        sendMessage("$repo interval ${millis}ms with commit")
    }

    @SubCommand
    public suspend fun CommandSender.format(repo: String, type: Format) {
        subscriber.format(repo, type)
        sendMessage("$repo format $type with commit")
    }

    @SubCommand
    public suspend fun CommandSender.list(contact: Contact = context()) {
        sendMessage(subscriber.list(contact.id))
    }

    @SubCommand
    public suspend fun CommandSender.test(repo: String, type: Format, contact: Contact = context()) {
        sendMessage(subscriber.test(repo, contact.id, type))
    }
}