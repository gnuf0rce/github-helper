package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.mirai.plugin.command.*
import io.github.gnuf0rce.mirai.plugin.data.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*

object GitHubHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.gnuf0rce.github-helper",
        name = "github-helper",
        version = "1.0.1",
    ) {
        author("cssxsh")
    }
) {
    override fun onEnable() {
        GitHubConfig.reload()
        GitHubRepoTaskData.reload()

        GitHubRepoIssueCommand.register()
        GitHubRepoIssueCommand.subscriber.start()
        GitHubRepoPullCommand.register()
        GitHubRepoPullCommand.subscriber.start()
        GitHubRepoReleaseCommand.register()
        GitHubRepoReleaseCommand.subscriber.start()
        GitHubRepoCommitCommand.register()
        GitHubRepoCommitCommand.subscriber.start()
    }

    override fun onDisable() {
        GitHubRepoIssueCommand.unregister()
        GitHubRepoIssueCommand.subscriber.stop()
        GitHubRepoPullCommand.unregister()
        GitHubRepoPullCommand.subscriber.stop()
        GitHubRepoReleaseCommand.unregister()
        GitHubRepoReleaseCommand.subscriber.stop()
        GitHubRepoCommitCommand.unregister()
        GitHubRepoCommitCommand.subscriber.stop()
    }
}