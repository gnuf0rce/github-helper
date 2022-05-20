/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import io.github.gnuf0rce.github.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class PullQuery(
    @SerialName("state")
    var state: StateFilter = StateFilter.open,
    @SerialName("head")
    var head: String = "",
    @SerialName("base")
    var base: String = "",
    @SerialName("sort")
    var sort: ElementSort = ElementSort.created,
    @SerialName("direction")
    var direction: Direction = Direction.desc
) : Query {
    override fun toJsonObject(): JsonObject {
        return GitHubJson.encodeToJsonElement(serializer(), this) as JsonObject
    }
}