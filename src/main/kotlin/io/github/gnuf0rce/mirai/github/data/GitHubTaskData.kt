/*
 * Copyright 2021-2024 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github.data

import net.mamoe.mirai.console.data.*

public object GitHubTaskData : AutoSavePluginData(saveName = "TaskData") {
    @ValueName("issues")
    public val issues: MutableMap<String, GitHubTask> by value()
}