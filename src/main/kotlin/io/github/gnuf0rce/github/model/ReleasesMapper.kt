/*
 * Copyright 2021-2024 dsstudio Technologies and contributors.
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
 * [Releases](https://docs.github.com/en/rest/releases)
 */
public open class ReleasesMapper(parent: Url) :
    GitHubMapper(parent = parent, path = "releases") {

    override val github: GitHubClient = GitHubClient()

    // region Releases

    /**
     * [list-releases](https://docs.github.com/en/rest/releases/releases#list-releases)
     */
    public open suspend fun list(page: Int = 1, per: Int = 30): List<Release> = page(page = page, per = per)

    /**
     * [get-the-latest-release](https://docs.github.com/en/rest/releases/releases#get-the-latest-release)
     */
    public open suspend fun latest(): Release = get(path = "latest")

    /**
     * [get-a-release-by-tag-name](https://docs.github.com/en/rest/releases/releases#get-a-release-by-tag-name)
     */
    public open suspend fun get(tag: String): Release = get(path = "tags/$tag")

    /**
     * [get-a-release](https://docs.github.com/en/rest/releases/releases#get-a-release)
     */
    public open suspend fun get(id: Long): Release = get(path = "$id")

    // endregion
}