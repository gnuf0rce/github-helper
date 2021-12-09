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
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.ContactUtils.render
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

        dependsOn("xyz.cssxsh.mirai.plugin.mirai-selenium-plugin", true)
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

        if (GitHubConfig.percentage > 0) {
            logger.info { "自动审核加群将开始" }
            globalEventChannel().subscribeAlways<MemberJoinRequestEvent> {
                val login = message.substringAfterLast("答案：").trim()
                try {
                    val group = requireNotNull(group) { "群获取失败" }
                    if (group.members.any { login == it.nameCard }) {
                        @OptIn(ConsoleExperimentalApi::class)
                        reject(message = "<${login}>已存在 于 ${group.render()}")

                        return@subscribeAlways
                    }

                    val user = github.user(login = login).get()
                    val stats = user.stats()
                    if (stats.percentage >= GitHubConfig.percentage) {
                        logger.info { "同意 $fromId - $login - ${stats.rank}" }
                        accept()
                        delay(10_000)
                        val member = requireNotNull(group[fromId]) { "获取${fromId}信息失败" }
                        member.nameCard = login
                        group.sendMessage(At(member) + GitHubConfig.sign)
                    } else {
                        logger.warning { "拒绝 $fromId - $login - ${stats.rank}/${stats.percentage}"  }
                        reject(message = "你的Github账户活跃等级不足: ${stats.rank}/${stats.percentage}")
                    }
                } catch (exception: GitHubApiException) {
                    logger.warning { "拒绝 $fromId - $login - ${exception.json}"  }
                    reject(message = "你的Github账户信息获取失败: <$login>")
                } catch (cause: Throwable) {
                    logger.warning({ "未知错误, 无法处理" }, cause)
                }
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