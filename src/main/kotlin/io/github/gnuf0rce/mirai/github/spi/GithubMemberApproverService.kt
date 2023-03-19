/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github.spi

import io.github.gnuf0rce.github.exception.*
import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.github.data.*
import io.github.gnuf0rce.mirai.github.*
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.spi.*
import kotlin.collections.*

public class GithubMemberApproverService : MemberApprover {
    override val id: String = "github-approver"
    override val level: Int by lazy { System.getProperty("io.github.gnuf0rce.mirai.github.approver", "5").toInt() }

    override suspend fun approve(event: MemberJoinEvent): ApproveResult = ApproveResult.Ignore

    override suspend fun approve(event: MemberJoinRequestEvent): ApproveResult {
        val percentage = GitHubConfig.percentages[event.groupId] ?: GitHubConfig.percentage
        if (percentage <= 0) return ApproveResult.Ignore
        val login = event.message.substringAfterLast("答案：").trim()
        val comer = event.fromId
        return try {
            val group = requireNotNull(event.group) { "群获取失败" }
            if (group.members.any { login == it.nameCard }) {
                return ApproveResult.Reject(message = "<${login}> 已存在于 ${group.render()}")
            }

            val user = github.user(login = login).load() as User
            val stats = user.stats()
            if (stats.percentage >= percentage) {
                logger.info { "同意 $comer - $login - ${stats.rank}/${stats.percentage}" }
                group.globalEventChannel().subscribe<MemberJoinEvent> { join ->
                    if (join.member.id == comer && join.group.id == group.id) {
                        group.sendMessage(At(member) + GitHubConfig.sign)
                        join.member.nameCard = login
                        ListeningStatus.STOPPED
                    } else {
                        ListeningStatus.LISTENING
                    }
                }
                ApproveResult.Accept
            } else {
                logger.warning { "拒绝 $comer - $login - ${stats.rank}/${stats.percentage}" }
                ApproveResult.Reject(message = "你的Github账户活跃等级不足: ${stats.rank}/${stats.percentage}")
            }
        } catch (exception: GitHubApiException) {
            logger.warning { "拒绝 $comer - $login - ${exception.json}" }
            ApproveResult.Reject(message = "你的Github账户信息获取失败: <$login>")
        }
    }
}