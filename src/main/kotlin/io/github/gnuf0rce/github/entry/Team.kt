package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
data class Team(
    @SerialName("description")
    val description: String?,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("members_url")
    val membersUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("parent")
    val parent: Team?,
    @SerialName("permission")
    val permission: String,
    @SerialName("privacy")
    val privacy: String,
    @SerialName("repositories_url")
    val repositoriesUrl: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("url")
    override val url: String
) : Entry, HtmlPage