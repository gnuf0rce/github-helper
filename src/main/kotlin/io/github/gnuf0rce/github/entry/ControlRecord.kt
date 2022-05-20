/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

/**
 * @see Pull
 * @see Issue
 */
public sealed interface ControlRecord : Entry, LifeCycle, WebPage, Content, Owner.Product {
    override val htmlUrl: String
    public val title: String
    public val user: User?
    public val assignee: User?
    public val assignees: List<User>
    public val closedBy: User?
    public val mergedBy: User?
    public val state: State
    public val number: Int
    public val labels: List<Label>
    public val association: Association
    public val comments: Int
    public val commentsUrl: String
    public val milestone: Milestone?
    public val draft: Boolean
    public val locked: Boolean
    public val repository: Repo?

    override val owner: User?
        get() = user

    // TODO: 可能是字符串
    @Serializable
    public data class Label(
        @SerialName("color")
        val color: String?,
        @SerialName("default")
        val default: Boolean,
        @SerialName("description")
        val description: String?,
        @SerialName("id")
        val id: Long,
        @SerialName("name")
        val name: String,
        @SerialName("node_id")
        override val nodeId: String,
        @SerialName("url")
        override val url: String
    ) : Entry
}