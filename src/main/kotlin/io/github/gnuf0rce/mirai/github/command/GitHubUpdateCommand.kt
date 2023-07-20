package io.github.gnuf0rce.mirai.github.command

import io.github.gnuf0rce.mirai.github.*
import net.mamoe.mirai.console.command.*

public object GitHubUpdateCommand : SimpleCommand(
    owner = GitHubHelperPlugin,
    "update",
    description = "Update Plugin of GitHub Release"
) {
    @Handler
    public fun ConsoleCommandSender.handle() {
        try {
            GitHubReleasePluginUpdater.update()
        } catch (cause: Throwable) {
            logger.warning(name, cause)
        }
    }
}