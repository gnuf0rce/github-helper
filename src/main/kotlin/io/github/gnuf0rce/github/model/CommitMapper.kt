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

public open class CommitMapper(parent: Url, sha: String, override val github: GitHubClient) :
    GitHubMapper(parent = parent, path = "commits/$sha") {

    public open suspend fun load(): Commit = get()

    public open suspend fun comments(page: Int, per: Int = 30): List<Temp> = page(page = page, per = per, path = "comments")

    public open suspend fun comment(context: Temp): Temp = post(context = context, path = "comments")

    public open suspend fun heads(): List<Temp> = get(path = "branches-where-head")

    public open suspend fun pulls(): List<Temp> = get(path = "pulls")

    public open suspend fun ref(): Temp = get(path = "ref")

    public open suspend fun compare(base: String, head: String): Temp = get(path = "compare/${base}...${head}")

    public open suspend fun community(): Temp = get(path = "community/profile")
}