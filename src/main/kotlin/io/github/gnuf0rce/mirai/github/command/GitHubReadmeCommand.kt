/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github.command

import io.github.gnuf0rce.mirai.github.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.warning

public object GitHubReadmeCommand : SimpleCommand(
    owner = GitHubHelperPlugin,
    "readme",
    description = "Readme.md"
), GitHubCommand {

    @Handler
    public suspend fun UserCommandSender.handle(name: String) {
        val message = try {
            repo(if ('/' in name) name else "${name}/${name}").readme().toMessage(subject)
        } catch (cause: Exception) {
            logger.warning({ "readme with $name 获取失败" }, cause)
            "readme with $name 获取失败".toPlainText()
        }

        sendMessage(message)
    }
}