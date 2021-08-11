package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.mirai.plugin.data.*
import net.mamoe.mirai.console.plugin.jvm.*

object GitHubHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.gnuf0rce.github-helper",
        name = "github-helper",
        version = "0.1.0",
    ) {
        author("cssxsh")
    }
) {
    override fun onEnable() {
        GitHubConfig.reload()
        GitHubEventData.reload()

        GitHubSubscriber.start()
    }

    override fun onDisable() {
        GitHubSubscriber.stop()
    }
}