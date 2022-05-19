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

@Serializable
public data class Reactions(
    /**
     * 👍
     */
    @SerialName("+1")
    val plus: Int,
    /**
     * 👎
     */
    @SerialName("-1")
    val minus: Int,
    /**
     * 😄
     */
    @SerialName("laugh")
    val laugh: Int,
    /**
     * 😕
     */
    @SerialName("confused")
    val confused: Int,
    /**
     * ❤
     */
    @SerialName("heart")
    val heart: Int,
    /**
     * 🎉
     */
    @SerialName("hooray")
    val hooray: Int,
    /**
     * 🚀
     */
    @SerialName("rocket")
    val rocket: Int,
    /**
     * 👀
     */
    @SerialName("eyes")
    val eyes: Int,
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("url")
    val url: String
)