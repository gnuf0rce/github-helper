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

public open class CollaboratorsMapper(parent: Url, override val github: GitHubClient) :
    GitHubMapper(parent = parent, path = "collaborators") {

    public open suspend fun list(page: Int, per: Int = 30, affiliation: Affiliation = Affiliation.all): List<Temp> =
        page(page = page, per = per, context = mapOf("affiliation" to affiliation), path = "contributors")

    public open suspend fun new(context: Temp): Temp = post(context = context)
}