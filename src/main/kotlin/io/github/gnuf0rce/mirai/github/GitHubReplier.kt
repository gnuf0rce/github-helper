/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


@file:OptIn(ConsoleExperimentalApi::class)

package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.mirai.github.data.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.*
import java.time.*

internal typealias MessageReplier = suspend MessageEvent.(MatchResult) -> Any?

internal val ReplierPermission: Permission by lazy {
    PermissionService.INSTANCE.register(
        id = GitHubHelperPlugin.permissionId("url"),
        description = "自动匹配GitHub相关的Url, 并返回结果",
        parent = GitHubHelperPlugin.parentPermission
    )
}

private fun MessageEvent.hasReplierPermission() = toCommandSender().hasPermission(ReplierPermission)

private val REPLIER_FORMAT get() = GitHubConfig.replier

/**
 * 1. [https://github.com/{owner}/]
 */
internal val OWNER_REGEX = """(?<=github\.com/)([\w-.]+)(?!\S*/\S+)""".toRegex()

internal val OwnerReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Owner(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner) = result.destructured
        val entry = github.user(owner).load().takeIf { it.type == "User" }
            ?: github.organization(owner).load()
        entry.toMessage(subject, REPLIER_FORMAT, "replier", OffsetDateTime.MIN)
    } catch (cause: Exception) {
        logger.warning({ "构建Repo(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/]
 */
internal val REPO_REGEX = """(?<=github\.com/)([\w-.]+)/([\w-.]+)(?!\S*/\S+)""".toRegex()

internal val RepoReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Repo(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo) = result.destructured
        val entry = repo(owner, repo).load()
        entry.toMessage(subject, REPLIER_FORMAT, "replier")
    } catch (cause: Exception) {
        logger.warning({ "构建Repo(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/commit/{sha}]
 */
internal val COMMIT_REGEX = """(?<=github\.com/)([\w-.]+)/([\w-]+)/commit/([0-9a-f]{40})""".toRegex()

internal val CommitReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Commit(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo, sha) = result.destructured
        val entry = repo(owner, repo).commit(sha).get()
        entry.toMessage(subject, REPLIER_FORMAT, "$owner/$repo")
    } catch (cause: Exception) {
        logger.warning({ "构建Commit(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/issues/{number}]
 */
internal val ISSUE_REGEX = """(?<=github\.com/)([\w-.]+)/([\w-.]+)/issues/(\d+)""".toRegex()

internal val IssueReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Issue(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo, number) = result.destructured
        val entry = repo(owner, repo).issues.get(number.toInt())
        entry.toMessage(subject, REPLIER_FORMAT, "$owner/$repo", OffsetDateTime.now())
    } catch (cause: Exception) {
        logger.warning({ "构建Issue(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/pull/{number}]
 */
internal val PULL_REGEX = """(?<=github\.com/)([\w-.]+)/([\w-.]+)/pull/(\d+)""".toRegex()

internal val PullReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Pull(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo, number) = result.destructured
        val entry = repo(owner, repo).pulls.get(number.toInt())
        entry.toMessage(subject, REPLIER_FORMAT, "$owner/$repo", OffsetDateTime.now())
    } catch (cause: Exception) {
        logger.warning({ "构建Pull(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/releases/tag/{name}]
 */
internal val RELEASE_REGEX = """(?<=github\.com/)([\w-.]+)/([\w-.]+)/releases/(tag|latest)/?(\S*)""".toRegex()

internal val ReleaseReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Release(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo, type, name) = result.destructured
        val entry = if (type == "latest") {
            repo(owner, repo).releases.latest()
        } else {
            repo(owner, repo).releases.get(tag = name)
        }
        entry.toMessage(subject, REPLIER_FORMAT, "$owner/$repo")
    } catch (cause: Exception) {
        logger.warning({ "构建Release(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/milestone/{number}]
 */
internal val MILESTONE_REGEX = """(?<=github\.com)([\w-.]+)/([\w-.]+)/milestone/(\d+)""".toRegex()

internal val MilestoneReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Milestone(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo, number) = result.destructured
        val entry = repo(owner, repo).milestones.get(number.toInt())
        entry.toMessage(subject, REPLIER_FORMAT, "$owner/$repo")
    } catch (cause: Exception) {
        logger.warning({ "构建Milestone(${result.value})信息失败" }, cause)
        cause.message
    }
}

internal val UrlRepliers by lazy {
    mapOf(
        COMMIT_REGEX to CommitReplier,
        ISSUE_REGEX to IssueReplier,
        PULL_REGEX to PullReplier,
        RELEASE_REGEX to ReleaseReplier,
        REPO_REGEX to RepoReplier,
        OWNER_REGEX to OwnerReplier,
        MILESTONE_REGEX to MilestoneReplier,
        SHORT_LINK_REGEX to ShortLinkReplier
    )
}

private suspend fun Url.location(): String? {
    return github.useHttpClient { client ->
        client.config {
            followRedirects = false
            expectSuccess = false
        }.head(this@location)
    }.headers[HttpHeaders.Location]
}

internal val SHORT_LINK_REGEX = """git\.io/\w+""".toRegex()

internal val ShortLinkReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配ShortLink(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val location = requireNotNull(Url(result.value).location()) { "跳转失败" }
        for ((regex, replier) in UrlRepliers) {
            val new = regex.find(location) ?: continue
            return@replier replier(new)
        }
    } catch (cause: Exception) {
        logger.warning({ "构建ShortLink(${result.value})信息失败" }, cause)
        cause.message
    }
}