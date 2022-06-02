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
import java.util.*

/**
 * 1. [https://api.github.com/repos/{owner}/{repo}/branches]
 */
public open class BranchesMapper(parent: Url) :
    GitHubMapper(parent = parent, path = "branches") {

    override val github: GitHubClient = GitHubClient()

    public open suspend fun list(protected: Boolean, page: Int, per: Int = 30): List<Temp> =
        page(page = page, per = per, context = mapOf("protected" to protected))

    public open suspend fun get(name: String): Temp = get<Temp>(name)

    protected open val protections: MutableMap<String, BranchProtectionMapper> = WeakHashMap()

    public open suspend fun protection(branch: String): BranchProtectionMapper = protections.getOrPut(branch) {
        object : BranchProtectionMapper(parent = base, branch = branch) {
            override val github: GitHubClient get() = this@BranchesMapper.github
        }
    }
}