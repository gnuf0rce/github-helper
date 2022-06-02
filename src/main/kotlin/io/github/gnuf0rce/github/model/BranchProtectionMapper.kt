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
import io.ktor.http.*

/**
 * 1. [https://api.github.com/repos/{owner}/{repo}/branches/{branch}/protection]
 */
public open class BranchProtectionMapper(parent: Url, branch: String) :
    GitHubMapper(parent = parent, path = "${branch}/protection") {

    override val github: GitHubClient = GitHubClient()

    public open suspend fun get(): Temp = get(path = "")

    public open suspend fun put(context: Temp): Temp = put(context = context, path = "")

    public open suspend fun delete(): Unit = delete(path = "")

    public open suspend fun admin(): Temp = get(path = "enforce_admins")

    public open suspend fun admin(open: Boolean): Unit = open(open, "enforce_admins")

    public open suspend fun reviews(): Temp = get(path = "required_pull_request_reviews")

    public open suspend fun reviews(context: Temp): Temp =
        patch(context = context, path = "required_pull_request_reviews")

    public open suspend fun reviewsDelete(): Unit = delete(path = "required_pull_request_reviews")
}