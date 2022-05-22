/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github.data

import io.github.gnuf0rce.mirai.github.*
import net.mamoe.mirai.console.data.*

public object GitHubConfig : ReadOnlyPluginConfig("GithubConfig") {
    @ValueName("proxy")
    @ValueDescription("Proxy Format http://127.0.0.1:8080 or socks://127.0.0.1:1080")
    public val proxy: String by value("")

    @ValueName("github_token")
    @ValueDescription("GitHub Token by https://github.com/settings/tokens")
    public val token: String by value(System.getenv("GITHUB_TOKEN").orEmpty())

    @ValueName("reply_type")
    @ValueDescription("Subscriber Reply Message Type")
    public val reply: Format by value(Format.TEXT)// TODO

    @ValueName("timeout")
    @ValueDescription("Http Timeout Second")
    public val timeout: Long by value(30L)

    @ValueName("percentage_member_join")
    @ValueDescription("放行活跃等级（百分制）")
    public val percentage: Int by value(0)

    @ValueName("percentages")
    @ValueDescription("放行活跃等级（百分制）")
    public val percentages: Map<Long, Int> by value(mapOf(12345L to 49))

    @ValueName("sign_member_join")
    @ValueDescription("加群提醒")
    public val sign: String by value("新人入群请看群公告")

    @ValueName("github_readme_stats")
    @ValueDescription("with https://github-readme-stats.vercel.app/")
    public val stats: Map<String, String> by value(
        mapOf(
            "show_icons" to "true",
            "theme" to "tokyonight",
            "count_private" to "true",
            "include_all_commits" to "true"
        )
    )
}