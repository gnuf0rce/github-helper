package io.github.gnuf0rce.mirai.plugin.data

import net.mamoe.mirai.console.data.*

object GitHubTaskData : AutoSavePluginData("TaskData") {
    @ValueName("issues")
    val issues by value(mutableMapOf<String, GitHubTask>())
}