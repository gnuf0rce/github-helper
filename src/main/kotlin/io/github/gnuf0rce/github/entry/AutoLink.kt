package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
data class AutoLink(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("key_prefix")
    val keyPrefix: String,
    @SerialName("url_template")
    val urlTemplate: String
)