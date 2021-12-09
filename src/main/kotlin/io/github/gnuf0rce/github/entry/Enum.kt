@file:Suppress("ClassName", "unused")

package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
enum class Visibility { public, private }

@Serializable
enum class State { open, closed, all }

@Serializable
enum class Direction { asc, desc }

@Serializable
enum class Affiliation { outside, direct, all }

@Serializable
enum class Association { OWNER, MEMBER, CONTRIBUTOR, NONE }

@Serializable
enum class IssueFilter { assigned, created, mentioned, subscribed, all, repos }

@Serializable
enum class IssueSort { created, updated, comments }

@Serializable
enum class MergeableState { unknown, dirty, blocked }

@Serializable
enum class VerificationReason { valid, unsigned }

@Serializable
enum class Encoding { base64 }

@Serializable
enum class Privacy { secret, closed }

@Serializable
enum class RepoPermission { pull, push, admin }