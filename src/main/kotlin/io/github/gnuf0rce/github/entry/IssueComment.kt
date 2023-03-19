/*
 * Copyright 2021-2023 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
@SerialName("IssueComment")
public data class IssueComment(
    @SerialName("author_association")
    override val association: Association,
    @SerialName("body")
    override val body: String,
    @SerialName("body_text")
    override val text: String,
    @SerialName("body_html")
    override val html: String,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("issue_url")
    val issueUrl: String,
    @SerialName("node_id")
    override val nodeId: String,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String,
    @SerialName("user")
    override val user: Owner?,
    @SerialName("reactions")
    override val reactions: Reactions?,
    @SerialName("performed_via_github_app")
    val performedViaGithubApp: GithubAppInfo? = null
) : Entry, Comment()