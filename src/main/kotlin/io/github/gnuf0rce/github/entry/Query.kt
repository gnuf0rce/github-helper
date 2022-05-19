package io.github.gnuf0rce.github.entry

import kotlinx.serialization.json.*

public interface Query {

    public fun toJsonObject(): JsonObject
}