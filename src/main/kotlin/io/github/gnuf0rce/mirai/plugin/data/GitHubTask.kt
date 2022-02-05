package io.github.gnuf0rce.mirai.plugin.data

import io.github.gnuf0rce.github.*
import kotlinx.serialization.*
import java.time.*

@Serializable
data class GitHubTask(
    @SerialName("id")
    val id: String,
    @SerialName("contacts")
    val contacts: MutableSet<Long> = HashSet(),
    @SerialName("last")
    @Serializable(OffsetDateTimeSerializer::class)
    var last: OffsetDateTime = OffsetDateTime.now(),
    @SerialName("interval")
    var interval: Long = 600_000L
)
