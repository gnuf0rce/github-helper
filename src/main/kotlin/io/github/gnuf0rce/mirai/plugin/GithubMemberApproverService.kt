package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.exception.*
import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.data.*
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.spi.*
import kotlin.collections.*

class GithubMemberApproverService : MemberApprover {
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

            val user = github.user(login = login).get()
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