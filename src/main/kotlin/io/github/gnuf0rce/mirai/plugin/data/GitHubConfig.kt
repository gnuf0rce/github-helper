package io.github.gnuf0rce.mirai.plugin.data

import io.github.gnuf0rce.mirai.plugin.*
import net.mamoe.mirai.console.data.*

object GitHubConfig : ReadOnlyPluginConfig("GithubConfig") {
    @ValueName("proxy")
    @ValueDescription("Proxy Format http://127.0.0.1:8080 or socks://127.0.0.1:1080")
    val proxy by value("")

    @ValueName("github_token")
    @ValueDescription("github token by ...") // XXX
    val token by value(System.getenv("GITHUB_TOKEN").orEmpty())

    @ValueName("reply_type")
    @ValueDescription("...") // XXX
    val reply by value(MessageType.TEXT)
}