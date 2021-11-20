package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

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
    override val url: String,
    @SerialName("name")
    override val name: String = "",
    @SerialName("company")
    val company: String? = null,
    @SerialName("blog")
    val blog: String? = null,
    @SerialName("location")
    val location: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("hireable")
    val hireable: Boolean? = null,
    @SerialName("bio")
    val bio: String? = null,
    @SerialName("twitter_username")
    val twitterUsername: String? = null,
    @SerialName("public_repos")
    val publicRepos: Int = 0,
    @SerialName("public_gists")
    val publicGists: Int = 0,
    @SerialName("followers")
    val followers: Int = 0,
    @SerialName("following")
    val following: Int = 0,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime = OffsetDateTime.MIN,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime = OffsetDateTime.MIN,
) : Entry, UserInfo, LifeCycle, HtmlPage {

    @Deprecated("Owner No Merge", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null

    @Deprecated("Owner No Closed", ReplaceWith("null"))
    override val closedAt: OffsetDateTime?
        get() = null

    @Serializable
    enum class Type { User, Organization }

    sealed interface Product {
        val owner: Owner
    }
}