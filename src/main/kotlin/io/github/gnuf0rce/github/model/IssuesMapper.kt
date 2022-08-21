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
 * [Issues](https://docs.github.com/en/rest/issues)
 */
public open class IssuesMapper(parent: Url) :
    GitHubMapper(parent = parent, path = "issues") {

    override val github: GitHubClient = GitHubClient()

    // region Issues

    /**
     * [list-repository-issues](https://docs.github.com/en/rest/issues/issues#list-repository-issues)
     */
    public open suspend fun list(page: Int = 1, per: Int = 30, block: IssueQuery.() -> Unit = {}): List<Issue> =
        page(page = page, per = per, context = IssueQuery().apply(block).toJsonObject())

    /**
     * [create-an-issue](https://docs.github.com/en/rest/issues/issues#create-an-issue)
     */
    public open suspend fun create(title: String, block: IssueBody.() -> Unit = {}): Issue =
        post(context = IssueBody(title = title).apply(block))

    /**
     * [get-an-issue](https://docs.github.com/en/rest/issues/issues#get-an-issue)
     */
    public open suspend fun get(number: Int): Issue = get(path = "$number")

    /**
     * [update-an-issue](https://docs.github.com/en/rest/issues/issues#update-an-issue)
     */
    public open suspend fun update(number: Int, block: IssueBody.() -> Unit): Issue =
        patch(context = IssueBody().apply(block), path = "$number")

    /**
     * [lock-an-issue](https://docs.github.com/en/rest/issues/issues#lock-an-issue)
     */
    public open suspend fun lock(number: Int, reason: String? = null): Unit =
        put(context = mapOf("lock_reason" to reason), path = "$number/lock")

    /**
     * [unlock-an-issue](https://docs.github.com/en/rest/issues/issues#unlock-an-issue)
     */
    public open suspend fun unlock(number: Int): Unit = delete(path = "$number/lock")

    // endregion

    // region Comments

    /**
     * [list-issue-comments-for-a-repository](https://docs.github.com/en/rest/issues/comments#list-issue-comments-for-a-repository)
     */
    public open val comments: Flow<List<IssueComment>> by lazy {
        callbackFlow<List<IssueComment>> {
            while (isActive) {
                send(element = page(page = 1, per = 100, path = "comments"))
            }
        }.flowOn(github.coroutineContext)
    }

    /**
     * [list-issue-comments](https://docs.github.com/en/rest/issues/comments#list-issue-comments)
     */
    public open suspend fun comments(number: Int, page: Int = 1, per: Int = 30, block: CommentQuery.() -> Unit = {})
        : List<IssueComment> =
        page(page = page, per = per, context = CommentQuery().apply(block).toJsonObject(), path = "$number/comments")

    // endregion

    // region Events

    /**
     * [list-issue-events-for-a-repository](https://docs.github.com/en/rest/issues/events#list-issue-events-for-a-repository)
     */
    public open val events: Flow<List<IssueEvent>> by lazy {
        callbackFlow<List<IssueEvent>> {
            while (isActive) {
                send(element = page(page = 1, per = 100, path = "events"))
            }
        }.flowOn(github.coroutineContext)
    }

    /**
     * [list-issue-events](https://docs.github.com/en/rest/issues/events#list-issue-events)
     */
    public open suspend fun events(number: Int, page: Int = 1, per: Int = 30): List<IssueEvent> =
        page(page = page, per = per, path = "$number/events")

    // endregion
}