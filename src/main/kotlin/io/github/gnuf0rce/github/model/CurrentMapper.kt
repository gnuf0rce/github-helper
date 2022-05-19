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
 * 1. [https://api.github.com/issues]
 * 2. [https://api.github.com/user]
 * 3. [https://api.github.com/rate_limit]
 */
public open class CurrentMapper(parent: Url, override val github: GitHubClient) :
    GitHubMapper(parent = parent, path = "") {

    public suspend fun issues(page: Int, per: Int = 30, context: Temp? = null): List<Issue> =
        page(page = page, per = per, context = context, path = "issues")

    public suspend fun user(): User = get(path = "user")

    public suspend fun rate(): RateLimit = get(path = "rate_limit")
}