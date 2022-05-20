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
public data class GithubAppInfo(
    @SerialName("id")
    val id: Long,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("owner")
    override val owner: User?,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("external_url")
    val externalUrl: String,
    @SerialName("html_url")
    override val htmlUrl: String,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @Contextual
    @SerialName("permissions")
    val permissions: Map<String, String> = emptyMap(),
    @SerialName("events")
    val events: List<String>,
    @SerialName("installations_count")
    val installationsCount: Int = 0,
    @SerialName("client_id")
    val clientId: String? = null,
    @SerialName("client_secret")
    val clientSecret: String? = null,
    @SerialName("webhook_secret")
    val webhookSecret: String? = null,
    @SerialName("pem")
    val pem: String? = null
) : Entry, LifeCycle, WebPage, Owner.Product {

    override val url: String
        get() = externalUrl

    @Deprecated("GithubAppInfo No Close", ReplaceWith("null"))
    override val closedAt: OffsetDateTime?
        get() = null

    @Deprecated("GithubAppInfo No Merge", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null
}
