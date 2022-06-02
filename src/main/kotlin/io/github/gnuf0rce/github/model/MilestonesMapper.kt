/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.http.*

/**
 * [Milestones](https://docs.github.com/en/rest/issues/milestones)
 */
public open class MilestonesMapper(parent: Url) :
    GitHubMapper(parent = parent, path = "milestones") {

    override val github: GitHubClient = GitHubClient()

    public open suspend fun list(page: Int, per: Int = 30, context: Temp? = null): List<Milestone> =
        page(page = page, per = per, context = context)

    public open suspend fun new(context: Temp): Milestone = post(context = context)

    public open suspend fun get(index: Int): Milestone = get(path = "$index")
}