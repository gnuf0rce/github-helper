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
 * [Repository autolinks](https://docs.github.com/en/rest/repos/autolinks)
 */
public open class AutoLinksMapper(parent: Url) :
    GitHubMapper(parent = parent, path = "autolinks") {

    override val github: GitHubClient = GitHubClient()

    public open suspend fun list(page: Int, per: Int = 30): List<AutoLink> = page(page = page, per = per)

    public open suspend fun new(link: AutoLink): AutoLink = post(context = link)

    public open suspend fun get(id: Int): AutoLink = get(path = "$id")

    public open suspend fun delete(id: Int): Unit = delete(path = "$id")
}