package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.exception.*
import io.github.gnuf0rce.mirai.plugin.command.*
import io.github.gnuf0rce.mirai.plugin.data.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*

object GitHubHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.gnuf0rce.github-helper",
        name = "github-helper",
        version = "1.1.5",
    ) {
        author("cssxsh")
    }
) {

    override fun PluginComponentStorage.onLoad() {
        System.setProperty(IGNORE_UNKNOWN_KEYS, "true")
    }

    override fun onEnable() {
        GitHubConfig.reload()
        GitHubRepoTaskData.reload()
        GitHubTaskData.reload()

        for (command in GitHubCommand) {
            command.register()
        }

        globalEventChannel().subscribeOnce<BotOnlineEvent> {
            GitHubSubscriber.start()
        }

        logger.info { "url auto reply: /perm add u* ${ReplierPermission.id}" }

        globalEventChannel().subscribeMessages {
            for ((regex, replier) in UrlRepliers) {
                regex findingReply replier
            }
        }

        if (GitHubConfig.rank > "") globalEventChannel().subscribeAlways<MemberJoinRequestEvent> {
            val login = message.substringAfterLast("答案：").trim()
            try {
                val user = github.user(login = login).get()
                val stats = user.stats(flush = true)
                if (stats.rank.orEmpty() >= "A+") {
                    logger.info { "同意 $fromId - $login - ${stats.rank}" }
                    accept()
                    delay(10_000)
                    val member = requireNotNull(group?.get(fromId)) { "获取${fromId}信息失败" }
                    member.nameCard = login
                    group!!.sendMessage(At(member) + "如果是机器人请看群公告修改id")
                } else {
                    logger.info { "拒绝 $fromId - $login -${stats.rank}"  }
                    reject(message = "你的Github账户活跃等级不足: ${stats.rank}")
                }
            } catch (exception: GitHubApiException) {
                reject(message = "你的Github账户信息获取失败: $login")
            } catch (cause: Throwable) {
                logger.warning({ "未知错误, 无法处理" }, cause)
            }
        }
    }

    override fun onDisable() {
        for (command in GitHubCommand) {
            command.unregister()
        }
        GitHubSubscriber.stop()
    }
}