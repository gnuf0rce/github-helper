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
 * [Repositories](https://docs.github.com/en/rest/repos)
 */
public open class ReposMapper(parent: Url, public val owner: String, public val repo: String) :
    GitHubMapper(parent = parent, path = "$owner/$repo") {

    override val github: GitHubClient = GitHubClient()

    // region Repositories

    public open suspend fun load(): Repo = get()

    public open suspend fun update(context: Temp): Temp = patch(context = context)

    public open suspend fun delete(): Unit = delete<Unit>()

    public open suspend fun fixes(open: Boolean): Temp = open(open = open, path = "automated-security-fixes")

    public open suspend fun contributors(page: Int, per: Int = 30, anon: Boolean = false): List<Temp> =
        page(page = page, per = per, context = mapOf("anon" to anon), path = "contributors")

    public open suspend fun dispatches(context: Temp): Unit = post(context = context, path = "dispatches")

    /**
     * [list-repository-languages](https://docs.github.com/en/rest/repos/repos#list-repository-languages)
     */
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

    /**
     * [list-commits](https://docs.github.com/en/rest/commits/commits#list-commits)
     */
    public open suspend fun commits(page: Int = 1, per: Int = 30): List<Commit> =
        page(page = page, per = per, path = "commits")

    /**
     * [list-repository-contributors](https://docs.github.com/en/rest/repos/repos#list-repository-contributors)
     */
    public open suspend fun collaborators(page: Int = 1, per: Int = 30, anonymous: Boolean = false): List<User> =
        page(page = page, per = per, context = mapOf("anon" to anonymous), path = "contributors")

    public open val issues: IssuesMapper = object : IssuesMapper(parent = base) {
        override val github: GitHubClient get() = this@ReposMapper.github
    }

    public open val pulls: PullsMapper = object : PullsMapper(parent = base) {
        override val github: GitHubClient get() = this@ReposMapper.github
    }

    public open val branches: BranchesMapper = object : BranchesMapper(parent = base) {
        override val github: GitHubClient get() = this@ReposMapper.github
    }

    public open val releases: ReleasesMapper = object : ReleasesMapper(parent = base) {
        override val github: GitHubClient get() = this@ReposMapper.github
    }

    public open val milestones: MilestonesMapper = object : MilestonesMapper(parent = base) {
        override val github: GitHubClient get() = this@ReposMapper.github
    }

    protected open val commits: MutableMap<String, CommitMapper> = WeakHashMap()

    public open fun commit(sha: String): CommitMapper = commits.getOrPut(sha) {
        object : CommitMapper(parent = base, sha = sha) {
            override val github: GitHubClient get() = this@ReposMapper.github
        }
    }

    // endregion

    // region Autolinks

    public open val autolinks: AutoLinksMapper = object : AutoLinksMapper(parent = base) {
        override val github: GitHubClient get() = this@ReposMapper.github
    }

    // endregion

    // region Contents

    protected open val contents: MutableMap<String, ContentsMapper> = WeakHashMap()

    public open fun content(path: String): ContentsMapper = contents.getOrPut(path) {
        object : ContentsMapper(parent = base, path = path) {
            override val github: GitHubClient get() = this@ReposMapper.github
        }
    }

    /**
     * [get-a-repository-readme-for-a-directory](https://docs.github.com/en/rest/repos/contents#get-a-repository-readme-for-a-directory)
     */
    public open suspend fun readme(dir: String = ""): Readme = get(path = "readme/$dir")

    /**
     * [download-a-repository-archive-tar](https://docs.github.com/en/rest/repos/contents#download-a-repository-archive-tar)
     */
    public open suspend fun tar(ref: String): Readme = get(path = "tarball/$ref")

    /**
     * [download-a-repository-archive-zip](https://docs.github.com/en/rest/repos/contents#download-a-repository-archive-zip)
     */
    public open suspend fun zip(ref: String): Readme = get(path = "zipball/$ref")

    // endregion
}
