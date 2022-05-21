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
import java.time.*

@Serializable
public sealed class Owner : Entry, LifeCycle, WebPage {
    public abstract val avatarUrl: String
    public abstract val eventsUrl: String
    public abstract val reposUrl: String
    public abstract val id: Long
    public abstract val login: String
    public abstract val name: String?
    public abstract val type: String
    public open val nameOrLogin: String get() = name ?: login

    @Deprecated("Owner No Merged", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null

    @Deprecated("Owner No Closed", ReplaceWith("null"))
    override val closedAt: OffsetDateTime?
        get() = null
}