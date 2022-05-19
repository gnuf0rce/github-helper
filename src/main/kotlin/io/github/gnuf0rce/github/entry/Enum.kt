/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


@file:Suppress("EnumEntryName", "unused")

package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
public enum class Visibility { public, private }

@Serializable
public enum class State { open, closed, all }

@Serializable
public enum class Direction { asc, desc }

@Serializable
public enum class Affiliation { outside, direct, all }

@Serializable
public enum class Association { OWNER, MEMBER, CONTRIBUTOR, NONE }

@Serializable
public enum class IssueFilter { assigned, created, mentioned, subscribed, all, repos }

@Serializable
public enum class ElementSort { created, updated, comments }

@Serializable
public enum class MergeableState { unknown, dirty, blocked, clean }

@Serializable
public enum class VerificationReason { valid, unsigned }

@Serializable
public enum class Encoding { base64 }

@Serializable
public enum class Privacy { secret, closed }

@Serializable
public enum class RepoPermission { pull, push, admin }

@Serializable
public enum class ReadmeType { file }

@Serializable
public enum class ReleaseState { uploaded }