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

public sealed interface LifeCycle {
    public val createdAt: OffsetDateTime
    public val updatedAt: OffsetDateTime
    public val mergedAt: OffsetDateTime?
    public val closedAt: OffsetDateTime?
}

public sealed interface Record {
    public val sha: String
    public val url: String
}

public sealed interface Entry {
    public val url: String?
    public val nodeId: String
}

public sealed interface WebPage {
    public val htmlUrl: String?
}

public sealed interface Content {
    public val body: String?
    public val text: String
    public val html: String
    public val reactions: Reactions?
}