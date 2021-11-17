package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
data class Owner(
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
    val type: Type,
    @SerialName("url")
    override val url: String
) : Entry, UserInfo, HtmlPage {

    @Serializable
    enum class Type { User, Organization }

    sealed interface Product {
        val owner: Owner
    }
}