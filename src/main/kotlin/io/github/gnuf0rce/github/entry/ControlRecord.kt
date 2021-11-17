package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

/**
 * @see [Pull]
 * @see [Issue]
 */
sealed interface ControlRecord : Entry, LifeCycle, HtmlPage, Content {
    override val htmlUrl: String
    val title: String
    val user: Coder
    val assignee: Coder?
    val assignees: List<Coder>
    val closedBy: Coder?
    val state: State
    val number: Int
    val labels: List<Label>
    val authorAssociation: Association
    override val body: String?
    val comments: Int
    val commentsUrl: String
    val milestone: Milestone?
    val draft: Boolean
    val locked: Boolean

    @Serializable
    data class Label(
        @SerialName("color")
        val color: String,
        @SerialName("default")
        val default: Boolean,
        @SerialName("description")
        val description: String,
        @SerialName("id")
        val id: Long,
        @SerialName("name")
        val name: String,
        @SerialName("node_id")
        override val nodeId: String,
        @SerialName("url")
        override val url: String
    ) : Entry
}