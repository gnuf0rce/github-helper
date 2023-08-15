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
@SerialName("Milestone")
public data class Milestone(
    @Contextual
    @SerialName("closed_at")
    override val closedAt: OffsetDateTime?,
    @SerialName("closed_issues")
    val closedIssues: Int,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("creator")
    override val creator: Owner?,
    @SerialName("description")
    val description: String?,
    @Contextual
    @SerialName("due_on")
    val dueOn: OffsetDateTime?,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("labels_url")
    val labelsUrl: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("number")
    val number: Int,
    @SerialName("open_issues")
    val openIssues: Int,
    @SerialName("state")
    val state: State,
    @SerialName("title")
    val title: String,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String
) : Entry, LifeCycle, WebPage, Product {

    override val owner: Owner?
        get() = creator

    /**
     * @see dueOn
     */
    override val mergedAt: OffsetDateTime?
        get() = dueOn
}