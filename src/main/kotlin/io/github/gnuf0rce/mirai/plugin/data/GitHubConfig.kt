package io.github.gnuf0rce.mirai.plugin.data

import io.github.gnuf0rce.mirai.plugin.*
import net.mamoe.mirai.console.data.*

object GitHubConfig : ReadOnlyPluginConfig("GithubConfig") {
    @ValueName("proxy")
    @ValueDescription("Proxy Format http://127.0.0.1:8080 or socks://127.0.0.1:1080")
    val proxy by value("")

    @ValueName("github_token")
    @ValueDescription("GitHub Token by https://github.com/settings/tokens")
    val token by value(System.getenv("GITHUB_TOKEN").orEmpty())

    @ValueName("reply_type")
    @ValueDescription("Subscriber Reply Message Type")
    val reply by value(MessageType.TEXT)

    @ValueName("timeout")
    @ValueDescription("Http Timeout Second")
    val timeout by value(30L)

    @ValueName("percentage_member_join")
    @ValueDescription("放行活跃等级（百分制）")
    val percentage by value(0)

    @ValueName("sign_member_join")
    @ValueDescription("加群提醒")
    val sign by value("新人入群请看群公告")

    @ValueName("github_readme_stats")
    @ValueDescription("with https://github-readme-stats.vercel.app/")
    val stats by value(
        mapOf(
            "show_icons" to "true",
            "theme" to "tokyonight",
            "count_private" to "true",
            "include_all_commits" to "true"
        )
    )
}