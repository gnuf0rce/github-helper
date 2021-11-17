package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
data class Milestone(
    @Contextual
    @SerialName("closed_at")
    override val closedAt: OffsetDateTime?,
    @SerialName("closed_issues")
    val closedIssues: Int,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("creator")
    override val creator: Coder,
    @SerialName("description")
    val description: String,
    @Contextual
    @SerialName("due_on")
    val dueOn: OffsetDateTime?,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("labels_url")
    val labelsUrl: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("number")
    val number: Int,
    @SerialName("open_issues")
    val openIssues: Int,
    @SerialName("state")
    val state: State,
    @SerialName("title")
    val title: String,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String,
) : Entry, LifeCycle, HtmlPage, Coder.Product {

    @Deprecated("Milestone No Merge", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null
}