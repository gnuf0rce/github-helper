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
import java.util.*

/**
 * 1. [https://api.github.com/repos/{owner}/{repo}]
 */
public open class RepoMapper(parent: Url, override val github: GitHubClient) :
    GitHubMapper(parent = parent, path = "") {

    public open suspend fun load(): Repo = get()

    public open suspend fun update(context: Temp): Temp = patch(context = context)

    public open suspend fun delete(): Unit = delete<Unit>()

    public open suspend fun fixes(open: Boolean): Temp = open(open = open, path = "automated-security-fixes")

    public open suspend fun contributors(page: Int, per: Int = 30, anon: Boolean = false): List<Temp> =
        page(page = page, per = per, context = mapOf("anon" to anon), path = "contributors")

    public open suspend fun dispatches(context: Temp): Unit = post(context = context, path = "dispatches")

    public open suspend fun languages(): Map<String, Long> = get(path = "languages")

    public open suspend fun tags(page: Int, per: Int = 30): List<Temp> = page(page = page, per = per, path = "tags")

    public open suspend fun teams(page: Int, per: Int = 30): List<Temp> = page(page = page, per = per, path = "teams")

    public open suspend fun topics(page: Int, per: Int = 30): List<String>? =
        get<Map<String, List<String>>>("topics")["names"]

    public open suspend fun topics(names: List<String>): List<String>? =
        put<Map<String, List<String>>, Map<String, List<String>>>(
            context = mapOf("names" to names),
            path = "topics"
        )["names"]

    public open suspend fun transfer(context: Temp): Temp = post(context = context, path = "transfer")

    public open suspend fun alerts(): Unit = get(path = "vulnerability-alerts")

    public open suspend fun alerts(open: Boolean): Unit = open(open = open, path = "vulnerability-alerts")

    public open suspend fun generate(context: Temp): Temp = post(context = context, path = "generate")

    public open val issues: IssuesMapper by lazy { IssuesMapper(parent = base, github = github) }

    public open val pulls: PullsMapper by lazy { PullsMapper(parent = base, github = github) }

    public open val autolinks: AutoLinksMapper by lazy { AutoLinksMapper(parent = base, github = github) }

    public open val branches: BranchesMapper by lazy { BranchesMapper(parent = base, github = github) }

    public open val collaborators: CollaboratorsMapper by lazy { CollaboratorsMapper(parent = base, github = github) }

    public open val releases: ReleasesMapper by lazy { ReleasesMapper(parent = base, github = github) }

    public open val milestones: MilestonesMapper by lazy { MilestonesMapper(parent = base, github = github) }

    /**
     * [list-commits](https://docs.github.com/en/rest/commits/commits#list-commits)
     */
    public open suspend fun commits(page: Int = 1, per: Int = 30): List<Commit> =
        page(page = page, per = per, path = "commits")

    protected open val commits: MutableMap<String, CommitMapper> = WeakHashMap()

    public open fun commit(sha: String): CommitMapper =
        commits.getOrPut(sha) { CommitMapper(parent = base, sha = sha, github = github) }

    protected open val contents: MutableMap<String, ContentMapper> = WeakHashMap()

    public open fun content(path: String): ContentMapper =
        contents.getOrPut(path) { ContentMapper(parent = base, path = path, github = github) }

    public open suspend fun readme(dir: String = ""): Readme = get(path = "readme/$dir")
}
