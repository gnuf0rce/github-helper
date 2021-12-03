package io.github.gnuf0rce.mirai.plugin.command

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.warning

object GitHubReadmeCommand : SimpleCommand(
    owner = GitHubHelperPlugin,
    "readme",
    description = "Readme.md"
), GitHubCommand {

    @Handler
    suspend fun UserCommandSender.handle(name: String) {
        val message = try {
            github.repo(if ('/' in name) name else "${name}/${name}").readme().toMessage(subject)
        } catch (cause: Throwable) {
            logger.warning({ "readme with $name 获取失败" }, cause)
            "readme with $name 获取失败".toPlainText()
        }

        sendMessage(message)
    }
}