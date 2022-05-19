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
 * 1. [https://api.github.com/repos/{owner}/{repo}/issues]
 */
public open class IssuesMapper(parent: Url, override val github: GitHubClient) :
    GitHubMapper(parent = parent, path = "issues") {

    public open suspend fun list(page: Int, per: Int = 30, block: IssueQuery.() -> Unit = {}): List<Issue> =
        page(page = page, per = per, context = IssueQuery().apply(block).toJsonObject())

    public open suspend fun new(title: String, block: IssueBody.() -> Unit = {}): Issue =
        post(context = IssueBody(title = title).apply(block))

    public open suspend fun get(index: Int): Issue = get(path = "$index")

    public open suspend fun update(index: Int, block: IssueBody.() -> Unit): Issue =
        patch(context = IssueBody().apply(block), path = "$index")

    public open suspend fun lock(index: Int, reason: String? = null): Unit =
        put(context = mapOf("lock_reason" to reason), path = "$index/lock")

    public open suspend fun unlock(index: Int): Unit = delete(path = "$index/lock")

    public open suspend fun comments(index: Int, page: Int, per: Int = 30, block: CommentQuery.() -> Unit = {})
        : List<IssueComment> =
        page(page = page, per = per, context = CommentQuery().apply(block).toJsonObject(), path = "$index/comments")

    public open suspend fun comment(index: Int, block: CommentQuery.() -> Unit = {}): IssueComment =
        post(context = CommentQuery().apply(block).toJsonObject(), path = "$index/comments")
}