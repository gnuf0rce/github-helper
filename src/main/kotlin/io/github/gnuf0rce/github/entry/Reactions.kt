package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
data class Reactions(
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