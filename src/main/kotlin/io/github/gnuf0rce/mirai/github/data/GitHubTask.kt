/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github.data

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.github.*
import kotlinx.serialization.*
import java.time.*

@Serializable
public data class GitHubTask(
    @SerialName("id")
    val id: String,
    @SerialName("contacts")
    val contacts: MutableSet<Long> = HashSet(),
    @SerialName("last")
    @Serializable(OffsetDateTimeSerializer::class)
    var last: OffsetDateTime = OffsetDateTime.now(),
    @SerialName("interval")
    var interval: Long = 600_000L,
    @SerialName("format")
    var format: Format = Format.TEXT
)
