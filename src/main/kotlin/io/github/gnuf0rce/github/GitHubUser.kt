/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github

import io.github.gnuf0rce.github.model.*

/**
 * @see [UserMapper]
 */
public class GitHubUser(user: String, override val github: GitHubClient = GitHubClient()) :
    WithGithubClient, UserMapper(parent = api("users"), user = user)