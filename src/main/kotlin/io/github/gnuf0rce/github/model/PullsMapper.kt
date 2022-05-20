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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

/**
 * [Pulls](https://docs.github.com/en/rest/pulls/pulls)
 */
public open class PullsMapper(parent: Url, override val github: GitHubClient) :
    GitHubMapper(parent = parent, path = "pulls") {

    // region Pulls

    /**
     * [list-pull-requests](https://docs.github.com/en/rest/pulls/pulls#list-pull-requests)
     */
    public open suspend fun list(page: Int = 1, per: Int = 30, context: Temp? = null): List<Pull> =
        page(page = page, per = per, context = context)

    /**
     * [create-a-pull-request](https://docs.github.com/en/rest/pulls/pulls#create-a-pull-request)
     */
    public open suspend fun create(context: Temp): Pull = post(context = context)

    /**
     * [get-a-pull-request](https://docs.github.com/en/rest/pulls/pulls#get-a-pull-request)
     */
    public open suspend fun get(number: Int): Pull = get(path = "$number")

    // endregion

    // region Review comments

    /**
     * [get-a-pull-request](https://docs.github.com/en/rest/pulls/pulls#get-a-pull-request)
     */
    public open suspend fun comments(number: Int, page: Int = 1, per: Int = 30, block: CommentQuery.() -> Unit = {})
        : List<PullRequestReviewComment> =
        page(page = page, per = per, context = CommentQuery().apply(block).toJsonObject(), path = "$number/comments")

    /**
     * [list-review-comments-in-a-repository](https://docs.github.com/en/rest/pulls/comments#list-review-comments-in-a-repository)
     */
    public open val comments: Flow<List<PullRequestReviewComment>> by lazy {
        callbackFlow {
            while (github.isActive) {
                send(element = page(page = 1, per = 100, path = "comments"))
            }
        }
    }

    // endregion
}