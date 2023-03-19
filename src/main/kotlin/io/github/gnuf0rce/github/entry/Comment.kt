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
public sealed class Comment : Entry, LifeCycle, WebPage, Content, Product {
    public abstract val association: Association
    abstract override val body: String
    abstract override val text: String
    abstract override val html: String
    abstract override val url: String

    @Deprecated("Comment No Closed", ReplaceWith("null"))
    override val closedAt: OffsetDateTime?
        get() = null

    @Deprecated("Comment No Merged", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null
}