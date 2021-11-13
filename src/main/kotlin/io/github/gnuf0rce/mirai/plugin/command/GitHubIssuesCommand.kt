package io.github.gnuf0rce.mirai.plugin.command

import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.plugin.*
import io.github.gnuf0rce.mirai.plugin.data.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*

object GitHubIssuesCommand : CompositeCommand(
    owner = GitHubHelperPlugin,
    "issues",
    description = "Issues Notice"
), GitHubCommand {
    private val subscriber = object : GitHubSubscriber<Issue>(primaryName, GitHubHelperPlugin) {
        override val tasks: MutableMap<String, GitHubTask> by GitHubTaskData::issues

        override val regex: Regex? = null

        override suspend fun GitHubTask.load(per: Int) = current.issues(page = 0, per = per)
    }

    @SubCommand
    suspend fun CommandSender.add(contact: Contact = Contact()) {
        subscriber.add("current", contact.id)
        sendMessage("current with issue 添加完成")
    }

    @SubCommand
    suspend fun CommandSender.remove(contact: Contact = Contact()) {
        subscriber.remove("current", contact.id)
        sendMessage("current with issue 移除完成")
    }

    @SubCommand
    suspend fun CommandSender.interval(millis: Long) {
        subscriber.interval("current", millis)
        sendMessage("current interval ${millis}ms with issue")
    }

    @SubCommand
    suspend fun CommandSender.list(contact: Contact = Contact()) {
        sendMessage(subscriber.list(contact.id))
    }

    @SubCommand
    suspend fun CommandSender.build(contact: Contact = Contact()) {
        sendMessage(subscriber.build("current", contact.id))
    }
}