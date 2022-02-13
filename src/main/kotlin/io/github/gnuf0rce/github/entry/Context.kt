package io.github.gnuf0rce.github.entry

import java.time.*

sealed interface LifeCycle {
    val createdAt: OffsetDateTime
    val updatedAt: OffsetDateTime
    val mergedAt: OffsetDateTime?
    val closedAt: OffsetDateTime?
}

sealed interface Record {
    val sha: String
    val url: String
}

sealed interface Entry {
    val url: String?
    val nodeId: String
}

sealed interface HtmlPage {
    val htmlUrl: String?
}

sealed interface Content {
    val body: String?
    val reactions: Reactions?
}