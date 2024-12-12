/*
 * Copyright 2021-2024 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
public data class ApiError(
    @SerialName("documentation_url")
    val documentationUrl: String?,
    @SerialName("message")
    val message: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("errors")
    val errors: List<Detail> = emptyList()
) {
    @Serializable
    public data class Detail(
        @SerialName("resource")
        val resource: String,
        @SerialName("field")
        val field: String,
        @SerialName("code")
        val code: String
    )
}