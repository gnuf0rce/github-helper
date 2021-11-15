package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

interface UserInfo {
    val avatarUrl: String
    val id: Long
    val login: String
}

@Serializable
data class Coder(
    @SerialName("avatar_url")
    override val avatarUrl: String,
    @SerialName("events_url")
    val eventsUrl: String,
    @SerialName("followers_url")
    val followersUrl: String,
    @SerialName("following_url")
    val followingUrl: String,
    @SerialName("gists_url")
    val gistsUrl: String,
    @SerialName("gravatar_id")
    val gravatarId: String,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    override val id: Long,
    @SerialName("login")
    override val login: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("organizations_url")
    val organizationsUrl: String,
    @SerialName("received_events_url")
    val receivedEventsUrl: String,
    @SerialName("repos_url")
    val reposUrl: String,
    @SerialName("site_admin")
    val siteAdmin: Boolean,
    @SerialName("starred_url")
    val starredUrl: String,
    @SerialName("subscriptions_url")
    val subscriptionsUrl: String,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    override val url: String
) : Entry, UserInfo, HtmlPage

@Serializable
data class Author(
    @Contextual
    @SerialName("date")
    val date: OffsetDateTime,
    @SerialName("email")
    val email: String,
    @SerialName("name")
    val name: String
)