package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
public data class RepoSecurityAndAnalysis(
    @Serializable
    public val status: String
)