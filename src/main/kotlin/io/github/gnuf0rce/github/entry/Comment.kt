package io.github.gnuf0rce.github.entry

import java.time.*

public sealed interface Comment : Entry, LifeCycle, WebPage, Content, Owner.Product {
    public val authorAssociation: AuthorAssociation
    override val body: String
    override val text: String
    override val html: String
    override val createdAt: OffsetDateTime
    override val htmlUrl: String
    override val nodeId: String
    override val updatedAt: OffsetDateTime
    override val url: String
    public val user: User
    override val reactions: Reactions?

    override val owner: User
        get() = user
}