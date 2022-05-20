/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import java.time.*

public sealed interface Comment : Entry, LifeCycle, WebPage, Content, Owner.Product {
    public val association: AuthorAssociation
    override val body: String
    override val text: String
    override val html: String
    override val createdAt: OffsetDateTime
    override val htmlUrl: String
    override val nodeId: String
    override val updatedAt: OffsetDateTime
    override val url: String
    public val user: User?
    override val reactions: Reactions?

    override val owner: User?
        get() = user
}