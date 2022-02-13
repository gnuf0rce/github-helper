package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
sealed class Owner : Entry, LifeCycle, HtmlPage {
    abstract val avatarUrl: String
    abstract val eventsUrl: String
    abstract val reposUrl: String
    abstract val id: Long
    abstract val login: String
    abstract val name: String?
    abstract val type: String

    @Deprecated("Owner No Merge", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null

    @Deprecated("Owner No Closed", ReplaceWith("null"))
    override val closedAt: OffsetDateTime?
        get() = null

    sealed interface Product {
        val owner: Owner
    }
}