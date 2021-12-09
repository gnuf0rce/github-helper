package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
data class License(
    @SerialName("html_url")
    override val htmlUrl: String? = null,
    @SerialName("key")
    val key: String,
    @SerialName("name")
    val name: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("spdx_id")
    val spdxId: String,
    @SerialName("url")
    override val url: String?
) : Entry, HtmlPage