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
 * [Users](https://docs.github.com/en/rest/users)
 */
public open class UserMapper(parent: Url, user: String, override val github: GitHubClient) :
    GitHubMapper(parent = parent, path = user) {

    // region Users

    /**
     * [about-the-users-api](https://docs.github.com/en/rest/users/users#about-the-users-api)
     */
    public open suspend fun load(): User = get()

    public open suspend fun repos(page: Int, per: Int = 30): List<Repo> = page(page = page, per = per, path = "repos")

    // endregion
}
