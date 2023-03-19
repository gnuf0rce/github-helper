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

/**
 * @see Pull
 * @see Issue
 */
@Serializable
public sealed class ControlRecord : Entry, LifeCycle, WebPage, Content, Product {
    public abstract val title: String
    public abstract override val owner: Owner?
    public abstract val assignee: Owner?
    public abstract val assignees: List<Owner>
    public abstract val closedBy: Owner?
    public abstract val mergedBy: Owner?
    public abstract val state: State
    public abstract val number: Int
    public abstract val labels: List<Label>
    public abstract val association: Association
    public abstract val comments: Int
    public abstract val commentsUrl: String
    public abstract val milestone: Milestone?
    public abstract val draft: Boolean
    public abstract val locked: Boolean
    public abstract val repository: Repo?
}