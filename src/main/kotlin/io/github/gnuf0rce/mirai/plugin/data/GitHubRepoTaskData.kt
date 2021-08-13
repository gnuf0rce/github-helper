package io.github.gnuf0rce.mirai.plugin.data

import net.mamoe.mirai.console.data.*

object GitHubRepoTaskData : AutoSavePluginData("RepoTaskData") {
    @ValueName("issues")
    val issues by value(mutableMapOf<String, GitHubTask>())

    @ValueName("pulls")
    val pulls by value(mutableMapOf<String, GitHubTask>())

    @ValueName("releases")
    val releases by value(mutableMapOf<String, GitHubTask>())
}