package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
public data class Link(
    @SerialName("href")
    val href: String
)