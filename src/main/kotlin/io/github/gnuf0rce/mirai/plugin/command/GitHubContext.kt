package io.github.gnuf0rce.mirai.plugin.command

import net.mamoe.mirai.console.command.*

internal fun CommandSender.Contact() = requireNotNull(subject) { "无法从当前环境获取联系人" }