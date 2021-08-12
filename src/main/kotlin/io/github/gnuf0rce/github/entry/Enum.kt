package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
enum class Visibility { public, private }

@Serializable
enum class STATE { open, closed, all }

@Serializable
enum class Direction { asc, desc }

@Serializable
enum class Affiliation { outside, direct, all }

@Serializable
enum class IssueFilter { assigned, created, mentioned, subscribed, all, repos }

@Serializable
enum class IssueSort { created, updated, comments }