@file:OptIn(ConsoleExperimentalApi::class)

package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.*

typealias MessageReplier = suspend MessageEvent.(MatchResult) -> Any?

internal val ReplierPermission: Permission by lazy {
    PermissionService.INSTANCE.register(
        id = GitHubHelperPlugin.permissionId("url"),
        description = "",
        parent = GitHubHelperPlugin.parentPermission
    )
}

private fun MessageEvent.hasReplierPermission() = with(PermissionService) {
    toCommandSender().hasPermission(ReplierPermission)
}

private const val REPLIER_NOTICE = "replier"

/**
 * 1. [https://github.com/{owner}/{repo}/]
 */
internal val REPO_REGEX = """(?<=github\.com/)([\w-]+)/([\w-]+)(?![\w-]*/[\w-])""".toRegex()

internal val RepoReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Repo(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo) = result.destructured
        val entry = github.repo(owner, repo).get()
        entry.toMessage(subject, reply, REPLIER_NOTICE)
    } catch (cause: Throwable) {
        logger.warning({ "构建Repo(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/commit/{sha}]
 */
internal val COMMIT_REGEX = """(?<=github\.com/)([\w-]+)/([\w-]+)/commit/(\w+)""".toRegex()

internal val CommitReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Commit(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo, sha) = result.destructured
        val entry = github.repo(owner, repo).comments.get(sha)
        entry.toMessage(subject, reply, REPLIER_NOTICE)
    } catch (cause: Throwable) {
        logger.warning({ "构建Repo(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/issues/{number}]
 */
internal val ISSUE_REGEX = """(?<=github\.com/)([\w-]+)/([\w-]+)/issues/(\d+)""".toRegex()

internal val IssueReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Issue(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo, number) = result.destructured
        val entry = github.repo(owner, repo).issues.get(number.toInt())
        entry.toMessage(subject, reply, REPLIER_NOTICE)
    } catch (cause: Throwable) {
        logger.warning({ "构建Repo(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/pulls/{number}]
 */
internal val PULL_REGEX = """(?<=github\.com/)([\w-]+)/([\w-]+)/pulls/(\d+)""".toRegex()

internal val PullReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Pull(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo, number) = result.destructured
        val entry = github.repo(owner, repo).pulls.get(number.toInt())
        entry.toMessage(subject, reply, REPLIER_NOTICE)
    } catch (cause: Throwable) {
        logger.warning({ "构建Pull(${result.value})信息失败" }, cause)
        cause.message
    }
}

/**
 * 1. [https://github.com/{owner}/{repo}/releases/tag/{tag}]
 */
internal val RELEASE_REGEX = """(?<=github\.com/)([\w-]+)/([\w-]+)/releases/tag/([^/#]+)""".toRegex()

internal val ReleaseReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Release(${result.value})" }
    if (hasReplierPermission().not()) return@replier null
    try {
        val (owner, repo, tag) = result.destructured
        var page = 1
        lateinit var entry: Release
        while (true) {
            val list = github.repo(owner, repo).releases.list(page++)
            if (list.isEmpty()) throw NotImplementedError("$tag with ${owner}/${repo}")
            entry = list.find { it.tagName == tag } ?: continue
            break
        }
        entry.toMessage(subject, reply, REPLIER_NOTICE)
    } catch (cause: Throwable) {
        logger.warning({ "构建Release(${result.value})信息失败" }, cause)
        cause.message
    }
}

internal val UrlRepliers by lazy {
    mapOf(
        COMMIT_REGEX to CommitReplier,
        ISSUE_REGEX to IssueReplier,
        PULL_REGEX to PullReplier,
        RELEASE_REGEX to ReleaseReplier,
        REPO_REGEX to RepoReplier
    )
}