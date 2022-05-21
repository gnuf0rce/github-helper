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