package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
public data class GithubAppInfo(
    @SerialName("id")
    val id: Int,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("owner")
    override val owner: User,
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
