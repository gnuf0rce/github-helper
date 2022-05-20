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
 * [Commits](https://docs.github.com/en/rest/commits)
 */
public open class CommitMapper(parent: Url, sha: String, override val github: GitHubClient) :
    GitHubMapper(parent = parent, path = "commits/$sha") {

    // region Commits

    /**
     * [get-a-commit](https://docs.github.com/en/rest/commits/commits#get-a-commit)
     */
    public open suspend fun get(): Commit = get(path = "")

    /**
     * [list-branches-for-head-commit](https://docs.github.com/en/rest/commits/commits#list-branches-for-head-commit)
     */
    public open suspend fun branches(): List<Temp> = get(path = "branches-where-head")

    /**
     * [list-pull-requests-associated-with-a-commit](https://docs.github.com/en/rest/commits/commits#list-pull-requests-associated-with-a-commit)
     */
    public open suspend fun pulls(): List<Temp> = get(path = "pulls")

    // endregion

    // region Commit comments

    /**
     * [list-commit-comments](https://docs.github.com/en/rest/commits/comments#list-commit-comments)
     */
    public open suspend fun comments(page: Int, per: Int = 30): List<Temp> =
        page(page = page, per = per, path = "comments")

    // endregion
}