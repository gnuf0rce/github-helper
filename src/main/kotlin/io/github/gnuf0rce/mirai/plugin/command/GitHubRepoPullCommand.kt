package io.github.gnuf0rce.mirai.plugin.command

import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.plugin.*
import io.github.gnuf0rce.mirai.plugin.data.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*

object GitHubRepoPullCommand : CompositeCommand(
    owner = GitHubHelperPlugin,
    "repo-pull",
    description = "Repo Pull Notice"
) {
    private val subscriber = object : GitHubSubscriber<Pull>(primaryName, GitHubHelperPlugin) {
        override val tasks: MutableMap<String, GitHubTask> by GitHubRepoTaskData::pulls

        override val regex: Regex = REPO_REGEX

        override suspend fun GitHubTask.load(per: Int) = repo.pulls.list(page = 0, per = per)
    }

    @SubCommand
    suspend fun CommandSender.add(repo: String, contact: Contact = Contact()) {
        subscriber.add(repo, contact.id)
        sendMessage("$repo with pull 添加完成")
    }

    @SubCommand
    suspend fun CommandSender.remove(repo: String, contact: Contact = Contact()) {
        subscriber.remove(repo, contact.id)
        sendMessage("$repo with pull 移除完成")
    }

    @SubCommand
    suspend fun CommandSender.interval(repo: String, millis: Long) {
        subscriber.interval(repo, millis)
        sendMessage("$repo interval ${millis}ms with pull")
    }

    @SubCommand
    suspend fun CommandSender.list(contact: Contact = Contact()) {
        sendMessage(subscriber.list(contact.id))
    }
}