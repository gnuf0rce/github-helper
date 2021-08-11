package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
data class Error(
    @SerialName("documentation_url")
    val documentationUrl: String,
    @SerialName("message")
    val message: String
)