/*
 * Copyright 2021-2023 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import java.time.*

public sealed interface LifeCycle {
    public val createdAt: OffsetDateTime
    public val updatedAt: OffsetDateTime
    public val mergedAt: OffsetDateTime?
    public val closedAt: OffsetDateTime?
}

public sealed interface Record {
    public val sha: String
    public val url: String

    /**
     * @see sha
     */
    public val key: String get() = sha.substring(0, 7)
}

public sealed interface Entry {
    public val url: String?
    public val nodeId: String
}

public sealed interface WebPage {
    public val htmlUrl: String
    public val graphUrl: String? get() = null
}

public sealed interface Content {
    public val body: String?
    public val text: String?
    public val html: String?
    public val reactions: Reactions?
}

public sealed interface Product {
    public val owner: Owner?
    public val ownerNameOrLogin: String get() = owner?.name ?: owner?.login ?: "ghost"
    public val user: Owner? get() = owner
    public val author: Owner? get() = owner
    public val uploader: Owner? get() = owner
    public val creator: Owner? get() = owner
}