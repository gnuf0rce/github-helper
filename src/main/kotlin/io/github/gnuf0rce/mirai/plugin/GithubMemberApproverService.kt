package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.exception.*
import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.plugin.data.*
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.spi.*
import java.util.*
import kotlin.collections.*

class GithubMemberApproverService : MemberApprover {
    override val id: String = "github"
    override val level: Int = 5
    private val cache: MutableList<(MemberJoinEvent) -> Boolean> = ArrayList()
    private val logins: MutableMap<Long, String> = WeakHashMap()

    override suspend fun approve(event: MemberJoinEvent): ApproveResult {
        try {
            if (cache.removeIf { it.invoke(event) }) {
                event.group.sendMessage(At(event.member) + GitHubConfig.sign)
                logins[event.member.id]?.let {
                    event.member.nameCard = it
                }
                return ApproveResult.Accept
            }
        } catch (_: Throwable) {
            //
        }
        return ApproveResult.Ignore
    }

    override suspend fun approve(event: MemberJoinRequestEvent): ApproveResult {
        if (GitHubConfig.percentage <= 0) return ApproveResult.Ignore
        val login = event.message.substringAfterLast("答案：").trim()
        val comer = event.fromId
        return try {
            val group = requireNotNull(event.group) { "群获取失败" }
            if (group.members.any { login == it.nameCard }) {
                return ApproveResult.Reject(message = "<${login}> 已存在于 ${group.render()}")
            }

            val user = github.user(login = login).get()
            val stats = user.stats()
            if (stats.percentage >= GitHubConfig.percentage) {
                logger.info { "同意 $comer - $login - ${stats.rank}/${stats.percentage}" }
                logins[comer] = login
                cache.add { join: MemberJoinEvent -> join.member.id == comer && group.id == join.group.id }
                ApproveResult.Accept
            } else {
                logger.warning { "拒绝 $comer - $login - ${stats.rank}/${stats.percentage}"  }
                ApproveResult.Reject(message = "你的Github账户活跃等级不足: ${stats.rank}/${stats.percentage}")
            }
        } catch (exception: GitHubApiException) {
            logger.warning { "拒绝 $comer - $login - ${exception.json}"  }
            ApproveResult.Reject(message = "你的Github账户信息获取失败: <$login>")
        }
    }
}