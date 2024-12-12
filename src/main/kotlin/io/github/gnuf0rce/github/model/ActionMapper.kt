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
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * [Actions](https://docs.github.com/en/rest/actions)
 */
public open class ActionMapper(parent: Url) :
    GitHubMapper(parent = parent, path = "actions") {

    override val github: GitHubClient = GitHubClient()

    // region Artifacts

    /**
     * [list-artifacts-for-a-repository](https://docs.github.com/en/rest/actions/artifacts#list-artifacts-for-a-repository)
     */
    public open suspend fun artifacts(page: Int = 1, per: Int = 30): List<ActionsArtifact> {
        return rest<ActionsArtifacts>(path = "artifacts") {
            method = HttpMethod.Get
            parameter("per_page", per)
            parameter("page", page)
        }.artifacts
    }

    /**
     * [get-an-artifact](https://docs.github.com/en/rest/actions/artifacts#get-an-artifact)
     */
    public open suspend fun artifact(id: Long): ActionsArtifact = get(path = "artifacts/$id")

    // endregion
}