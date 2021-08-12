package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
data class RateLimit(
    @SerialName("rate")
    val rate: Status,
    @SerialName("resources")
    val resources: Map<String, Status>
) {
    @Serializable
    data class Status(
        @SerialName("limit")
        val limit: Int,
        @SerialName("remaining")
        val remaining: Int,
        @SerialName("reset")
        val reset: Long,
        @SerialName("resource")
        val resource: String,
        @SerialName("used")
        val used: Int
    )
}