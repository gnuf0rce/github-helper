package io.github.gnuf0rce.mirai.plugin.data

import net.mamoe.mirai.console.data.*

object GitHubRepoTaskData : AutoSavePluginData("RepoTaskData") {
    @ValueName("commits")
    val commits by value<MutableMap<String, GitHubTask>>()

    @ValueName("issues")
    val issues by value<MutableMap<String, GitHubTask>>()

    @ValueName("pulls")
    val pulls by value<MutableMap<String, GitHubTask>>()

    @ValueName("releases")
    val releases by value<MutableMap<String, GitHubTask>>()
}