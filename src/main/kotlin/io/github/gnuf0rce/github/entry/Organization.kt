/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
@SerialName("Organization")
public data class Organization(
    @SerialName("avatar_url")
    override val avatarUrl: String,
    @SerialName("events_url")
    override val eventsUrl: String,
    @SerialName("followers_url")
    val followersUrl: String = "",
    @SerialName("following_url")
    val followingUrl: String = "",
    @SerialName("gists_url")
    val gistsUrl: String = "",
    @SerialName("gravatar_id")
    val gravatarId: String = "",
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    override val id: Long,
    @SerialName("login")
    override val login: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("organizations_url")
    val organizationsUrl: String = "",
    @SerialName("received_events_url")
    val receivedEventsUrl: String = "",
    @SerialName("hooks_url")
    val hooksUrl: String = "",
    @SerialName("repos_url")
    override val reposUrl: String,
    @SerialName("site_admin")
    val siteAdmin: Boolean = true,
    @SerialName("starred_url")
    val starredUrl: String = "",
    @SerialName("subscriptions_url")
    val subscriptionsUrl: String = "",
    @SerialName("issues_url")
    val issuesUrl: String = "",
    @SerialName("members_url")
    val membersUrl: String = "",
    @SerialName("public_members_url")
    val publicMembersUrl: String = "",
    @SerialName("description")
    val description: String? = null,
    @SerialName("url")
    override val url: String,
    @SerialName("type")
    override val type: String,
    @SerialName("name")
    override val name: String? = null,
    @SerialName("company")
    val company: String? = null,
    @SerialName("blog")
    val blog: String? = null,
    @SerialName("location")
    val location: String? = null,
    @SerialName("email")
    val email: String? = null,
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
    @SerialName("is_verified")
    val isVerified: Boolean = false,
    @SerialName("has_organization_projects")
    val hasOrganizationProjects: Boolean = false,
    @SerialName("has_repository_projects")
    val hasRepositoryProjects: Boolean = false,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime = OffsetDateTime.MIN,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime = OffsetDateTime.MIN,
) : Owner()