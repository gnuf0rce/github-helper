package io.github.gnuf0rce.github.entry

import io.ktor.util.*
import kotlinx.serialization.*

@Serializable
data class Readme(
    /**
     * encoded
     * @see [encoding]
     * @see [decode]
     */
    @SerialName("content")
    val content: String,
    @SerialName("download_url")
    val downloadUrl: String,
    @SerialName("encoding")
    val encoding: String,
    @SerialName("git_url")
    val gitUrl: String,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("_links")
    val links: Links,
    @SerialName("name")
    val name: String,
    @SerialName("path")
    val path: String,
    @SerialName("sha")
    override val sha: String,
    @SerialName("size")
    val size: Int,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    override val url: String
) : HtmlPage, Record {

    @Serializable
    data class Links(
        @SerialName("git")
        val git: String,
        @SerialName("html")
        val html: String,
        @SerialName("self")
        val self: String
    )

    @OptIn(InternalAPI::class)
    internal fun decode(): String {
        return when (encoding) {
            "base64" -> {
                content.lineSequence().joinToString("") { it.decodeBase64String() }
            }
            else -> throw IllegalArgumentException(encoding)
        }
    }
}