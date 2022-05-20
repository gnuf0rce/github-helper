/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


@file:OptIn(MiraiExperimentalApi::class, ConsoleExperimentalApi::class)

package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.plugin.data.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.FileSupported
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.*
import org.openqa.selenium.*
import xyz.cssxsh.selenium.*
import java.io.File
import java.time.*

internal fun Contact(id: Long): Contact = Bot.instances.firstNotNullOf { it.getContactOrNull(id) }

@Serializable
public enum class Format { OLD, TEXT, FORWARD }

internal suspend fun Owner?.avatar(contact: Contact, size: Int, client: GitHubClient = github): Image {
    val avatarUrl = this?.avatarUrl ?: "https://avatars.githubusercontent.com/u/0"
    val login = this?.login.orEmpty()

    val folder = ImageFolder.resolve("avatar")
    val cache = folder.listFiles()?.find { it.name.startsWith("${login}.${size}") }
    if (cache != null && System.currentTimeMillis() - cache.lastModified() < 7 * 24 * 3600_000) {
        return cache.uploadAsImage(contact)
    }

    val file = client.useHttpClient { http ->
        http.get<HttpStatement>(avatarUrl) { url.parameters["s"] = size.toString() }.execute { response ->
            val format = response.contentType()?.contentSubtype ?: "jpg"
            val file = folder.resolve("${login}.${size}.${format}")

            file.writeBytes(response.receive())

            file
        }
    }

    return file.uploadAsImage(contact)
}

internal fun Readme.pdf(flush: Boolean = false): File {
    return ImageFolder.resolve("readme").resolve("${sha}.pdf").apply {
        if (exists().not() || flush) {
            parentFile.mkdirs()
            val bytes = useRemoteWebDriver { driver ->
                driver.get(htmlUrl)
                val start = System.currentTimeMillis()
                do {
                    if (System.currentTimeMillis() - start > 180_000) {
                        break
                    }
                } while (!driver.isReady())
                driver.printToPDF()
            }
            writeBytes(bytes)
        }
    }
}

internal fun Readme.markdown(flush: Boolean = false): File {
    return ImageFolder.resolve("readme").resolve("${sha}.md").apply {
        if (exists().not() || flush) {
            parentFile.mkdirs()
            writeText(decode())
        }
    }
}

private const val STATS_API = "https://github-readme-stats.vercel.app/api"

private val RANK_REGEX = """(?<=rank-text[^>]{0,1024}>[^>]{0,1024}>[^\w]{0,1024})[\w+-]+""".toRegex()

private val STARS_REGEX = """(?<=stars[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

private val COMMITS_REGEX = """(?<=commits[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

private val PRS_REGEX = """(?<=prs[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

private val ISSUES_REGEX = """(?<=issues[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

private val CONTRIB_REGEX = """(?<=contribs[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

private val OFFSET_REGEX = """(?<=dashoffset: )\d+\.?\d+""".toRegex()

private fun Reactions.render(): String = buildString {
    if (plus > 0) append("👍:$plus")
    if (minus > 0) append("👎:$minus")
    if (laugh > 0) append("😄:$laugh")
    if (confused > 0) append("😕:$confused")
    if (heart > 0) append("❤:$heart")
    if (hooray > 0) append("🎉:$hooray")
    if (rocket > 0) append("🚀:$rocket")
    if (eyes > 0) append("👀:$eyes")
}

public data class UserStats(
    /**
     * B+, A+, A++, S, S+
     */
    val rank: String,
    val stars: String,
    val commits: String,
    val prs: String,
    val issues: String,
    val contrib: String,
    val percentage: Int
)

@Suppress("BlockingMethodInNonBlockingContext")
internal suspend fun User.stats(flush: Long = 86400_000, client: GitHubClient = github): UserStats {
    val stats = ImageFolder.resolve("stats")
    val svg = stats.resolve("${login}.svg")
    val png = stats.resolve("${login}.png")

    if (svg.exists().not() || (System.currentTimeMillis() - svg.lastModified()) >= flush) {
        stats.mkdirs()
        svg.writeBytes(client.useHttpClient { http ->
            http.get(STATS_API) {
                parameter("username", login)
                for ((key, value) in GitHubConfig.stats) parameter(key, value)
            }
        })
    }

    if (selenium && (png.exists().not() || (System.currentTimeMillis() - png.lastModified()) >= flush)) {
        val screenshot = useRemoteWebDriver { driver ->
            driver.get("file:///${svg.absolutePath}")
            delay(10_000)
            driver.findElement(By.tagName("rect")).getScreenshotAs(OutputType.BYTES)
        }
        png.writeBytes(screenshot)
    }

    val xml = svg.readText()

    return try {
        UserStats(
            rank = requireNotNull(RANK_REGEX.find(xml)) { "UserStats 解析 rank 失败" }.value,
            stars = requireNotNull(STARS_REGEX.find(xml)) { "UserStats 解析 stars 失败" }.value,
            commits = requireNotNull(COMMITS_REGEX.find(xml)) { "UserStats 解析 commits 失败" }.value,
            prs = requireNotNull(PRS_REGEX.find(xml)) { "UserStats 解析 prs 失败" }.value,
            issues = requireNotNull(ISSUES_REGEX.find(xml)) { "UserStats 解析 issues 失败" }.value,
            contrib = requireNotNull(CONTRIB_REGEX.find(xml)) { "UserStats 解析 contrib 失败" }.value,
            percentage = kotlin.run {
                val pre = requireNotNull(OFFSET_REGEX.find(xml)) { "UserStats 解析 percentage 失败" }
                val next = requireNotNull(OFFSET_REGEX.find(xml, pre.range.last)) { "UserStats 解析 percentage 失败" }
                ((1 - next.value.toDouble() / pre.value.toDouble()) * 100).toInt()
            }
        )
    } catch (cause: Throwable) {
        svg.renameTo(stats.resolve("${login}.error.svg"))
        throw cause
    }
}

internal fun MessageChainBuilder.appendLine(image: Image) = append(image).appendLine()

internal suspend fun User.card(contact: Contact): Message {
    val image = avatar(contact, 50)
    val stats = stats()
    return if (selenium) {
        buildMessageChain {
            appendLine(image)
            appendLine("Percentage: ${stats.percentage}")
            appendLine(ImageFolder.resolve("stats").resolve("${login}.png").uploadAsImage(contact))
        }
    } else {
        buildMessageChain {
            appendLine(image)
            appendLine("${name ?: login}'s GitHub Stats")
            appendLine("Rank:                 ${stats.rank}/${stats.percentage}")
            appendLine("Total Stars Earned:   ${stats.stars}")
            appendLine("Total Commits (${Year.now()}): ${stats.commits}")
            appendLine("Total PRs:            ${stats.prs}")
            appendLine("Total Issues:         ${stats.issues}")
            appendLine("Contributed to:       ${stats.contrib}")
        }
    }
}

internal suspend fun User.contribution(contact: Contact): Message {
    return if (selenium) {
        val png = ImageFolder.resolve("contribution").resolve("${login}.png")
        if (png.exists().not() || (System.currentTimeMillis() - png.lastModified()) >= 86400_000) {
            png.parentFile.mkdirs()
            val screenshot = useRemoteWebDriver { driver ->
                driver.manage().window().size = Dimension(1920, 1080)
                driver.get(htmlUrl)
                delay(10_000)
                driver.findElement(By.cssSelector(".ContributionCalendar")).getScreenshotAs(OutputType.BYTES)
            }
            png.writeBytes(screenshot)
        }
        png.uploadAsImage(contact)
    } else {
        "目前只有安装了 selenium 才能工作".toPlainText()
    }
}

internal suspend fun User.trophy(contact: Contact): Message {
    return if (selenium) {
        val png = ImageFolder.resolve("trophy").resolve("${login}.png")
        if (png.exists().not() || (System.currentTimeMillis() - png.lastModified()) >= 86400_000) {
            png.parentFile.mkdirs()
            val screenshot = useRemoteWebDriver { driver ->
                driver.get("https://github-profile-trophy.vercel.app/?username=${login}&column=4")
                delay(10_000)
                driver.findElement(By.cssSelector("svg")).getScreenshotAs(OutputType.BYTES)
            }
            png.writeBytes(screenshot)
        }
        png.uploadAsImage(contact)
    } else {
        "目前只有安装了 selenium 才能工作".toPlainText()
    }
}

/**
 * TODO: more info ...
 */
public suspend fun Owner.toMessage(contact: Contact): Message {
    return when (this) {
        is User -> card(contact)
        is Organization -> avatar(contact, 50)
    }
}

public suspend fun WebPage.toMessage(contact: Contact, format: Format, notice: String, since: OffsetDateTime): Message {
    return when (this) {
        is Issue -> toMessage(contact, format, notice, since)
        is Pull -> toMessage(contact, format, notice, since)
        is Release -> toMessage(contact, format, notice)
        is Commit -> toMessage(contact, format, notice)
        is Repo -> toMessage(contact, format, notice)
        is Milestone -> toMessage(contact, format, notice)
        is Owner -> toMessage(contact)
        is License -> (htmlUrl ?: name).toPlainText()
        is Issue.PullRequest -> htmlUrl.toPlainText()
        is Readme -> toMessage(contact)
        is Team -> htmlUrl.toPlainText()
        is Commit.Tree -> (htmlUrl ?: sha).toPlainText()
        is IssueComment -> htmlUrl.toPlainText()
        is PullRequestReviewComment -> htmlUrl.toPlainText()
        is GithubAppInfo -> htmlUrl.toPlainText()
    }
}

public suspend fun Contact.sendEntry(entry: WebPage, notice: String, format: Format, since: OffsetDateTime)
    : MessageReceipt<Contact> = sendMessage(entry.toMessage(this, format, notice, since))

@ForwardMessageDsl
private infix fun ForwardMessageBuilder.BuilderNode.at(offsetDateTime: OffsetDateTime) = apply {
    time = offsetDateTime.toEpochSecond().toInt()
}

public suspend fun Issue.toMessage(contact: Contact, format: Format, notice: String, since: OffsetDateTime): Message {
    return when (format) {
        Format.OLD -> buildMessageChain {
            appendLine(user.avatar(contact, 30))
            appendLine("$notice with issue by ${owner?.nameOrLogin} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
            if (reactions != null) appendLine(reactions.render())
            if (text != null && text.length < 50) appendLine(text)
        }
        Format.TEXT -> when {
            // case 1 new open issue
            createdAt == updatedAt -> buildMessageChain {
                appendLine(htmlUrl)
                appendLine(user.avatar(contact, 30))
                appendLine("[$notice] New issue <$title> open by ${user?.nameOrLogin}")
                if (text != null && text.length < 100) {
                    appendLine(text)
                }
            }
            // case 2 merged issue
            mergedAt == updatedAt -> buildMessageChain {
                appendLine(htmlUrl)
                appendLine(mergedBy.avatar(contact, 30))
                appendLine("[$notice] Issue <$title> merged by ${pullRequest?.htmlUrl ?: mergedBy?.nameOrLogin}")
                appendLine(stateReason)
            }
            // case 3 closed issue
            closedAt == updatedAt -> buildMessageChain {
                appendLine(htmlUrl)
                appendLine(closedBy.avatar(contact, 30))
                appendLine("[$notice] Issue <$title> closed by ${closedBy?.nameOrLogin}")
                appendLine(stateReason)
            }
            // case 4 locked issue
            locked -> buildMessageChain {
                appendLine(htmlUrl)
                appendLine("[$notice] Issue <$title> locked by ${activeLockReason ?: stateReason}")
            }
            // case 6 new comment or other update for issue
            else -> buildMessageChain {
                appendLine(htmlUrl)
                val (owner, repo) = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.destructured
                val comments = github.repo(owner = owner, repo = repo).issues
                    .comments(number = number) {
                        this.sort = ElementSort.updated
                        this.since = since
                    }

                if (comments.isEmpty()) {
                    appendLine("[$notice] Issue <$title> has change")
                    appendLine(reactions?.render())
                    if (assignees.isNotEmpty()) {
                        appendLine("assignees: ")
                        assignees.forEach { assignee ->
                            append(assignee.avatar(contact, 30)).append(assignee.nameOrLogin).append(" ")
                        }
                        appendLine()
                    }
                    if (milestone != null) {
                        appendLine("milestone with ${milestone.title}")
                    }
                } else {
                    appendLine("[$notice] Issue <$title> new ${comments.size} comments")
                    val comment = comments.first()
                    appendLine("last at ${comment.updatedAt}")
                    append(comment.user.avatar(contact, 30)).append(association.name)
                    appendLine(comment.reactions?.render())
                    append(comment.text)
                }
            }
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = title
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOf(
                    "$notice with #${number} issue by $ownerNameOrLogin",
                    "$state, $comments comments",
                    "update at $updatedAt",
                    stateReason ?: activeLockReason ?: labels.joinToString { it.name }
                )

                override fun generateSummary(forward: RawForwardMessage): String {
                    return stateReason ?: activeLockReason ?: labels.joinToString { it.name }
                }
            }
            contact.bot named association.name at createdAt says buildMessageChain {
                append(user.avatar(contact, 30)).appendLine(ownerNameOrLogin)
                appendLine(htmlUrl)
                appendLine(labels.joinToString { it.description ?: it.name })
                appendLine(reactions?.render())
                append(body)
            }
            contact.bot named "status" at createdAt says buildMessageChain {
                if (closedBy != null) {
                    append(closedBy.avatar(contact, 30)).appendLine("closed at $closedAt by ${closedBy.nameOrLogin}")
                }
                if (assignees.isNotEmpty()) {
                    appendLine("assigned to ")
                    assignees.forEach { assignee ->
                        append(assignee.avatar(contact, 30)).append(assignee.nameOrLogin).append(" ")
                    }
                    appendLine()
                }
                if (milestone != null) {
                    appendLine("milestone with ${milestone.title}")
                }
            }
            if (comments > 0) {
                val (owner, repo) = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.destructured
                val comments = github.repo(owner = owner, repo = repo).issues
                    .comments(number = number) {
                        sort = ElementSort.created
                        direction = Direction.asc
                    }

                for (comment in comments) {
                    contact.bot named comment.association.name at comment.createdAt says buildMessageChain {
                        append(comment.user.avatar(contact, 30)).appendLine(comment.user?.nameOrLogin)
                        appendLine(comment.reactions?.render())
                        append(comment.body)
                    }
                }
            }
        }
    }
}

public suspend fun Pull.toMessage(contact: Contact, format: Format, notice: String, since: OffsetDateTime): Message {
    return when (format) {
        Format.OLD -> buildMessageChain {
            appendLine(user.avatar(contact, 30))
            appendLine("$notice with pull by $ownerNameOrLogin ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
            if (reactions != null) appendLine(reactions.render())
            if (text != null && text.length < 50) appendLine(text)
        }
        Format.TEXT -> when {
            // case 1 new open pull
            createdAt == updatedAt -> buildMessageChain {
                appendLine(htmlUrl)
                appendLine(user.avatar(contact, 30))
                appendLine("[$notice] New pull request <$title> open by ${user?.nameOrLogin}")
                if (body != null && body.length < 100) {
                    appendLine(body)
                }
            }
            // case 2 merged pull
            mergedAt == updatedAt -> buildMessageChain {
                appendLine(htmlUrl)
                appendLine(mergedBy.avatar(contact, 30))
                appendLine("[$notice] Pull Request <$title> merged by ${mergedBy?.nameOrLogin}")
                appendLine(autoMerge)
            }
            // case 3 closed pull
            closedAt == updatedAt -> buildMessageChain {
                appendLine(htmlUrl)
                appendLine(closedBy.avatar(contact, 30))
                appendLine("[$notice] Pull Request <$title> closed by ${closedBy?.nameOrLogin}")
            }
            // case 4 locked pull
            locked -> buildMessageChain {
                appendLine(htmlUrl)
                appendLine("[$notice] Pull Request <$title> locked by $activeLockReason")
            }
            // case 6 new comment or other update for issue
            else -> buildMessageChain {
                appendLine(htmlUrl)
                val (owner, repo) = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.destructured
                val comments = github.repo(owner = owner, repo = repo).issues
                    .comments(number = number) {
                        this.sort = ElementSort.updated
                        this.since = since
                    }

                if (comments.isEmpty()) {
                    appendLine("[$notice] Pull Request <$title> has change")
                    appendLine(reactions?.render())
                    if (requestedReviewers.isNotEmpty()) {
                        appendLine("assignees: ")
                        assignees.forEach { assignee ->
                            append(assignee.avatar(contact, 30)).append(assignee.nameOrLogin).append(" ")
                        }
                        appendLine()
                    }
                    if (assignees.isNotEmpty()) {
                        appendLine("assignees: ")
                        assignees.forEach { assignee ->
                            append(assignee.avatar(contact, 30)).append(assignee.nameOrLogin).append(" ")
                        }
                        appendLine()
                    }
                    if (milestone != null) {
                        appendLine("milestone with ${milestone.title}")
                    }
                } else {
                    appendLine("[$notice] Pull Request <$title> new ${comments.size} comments")
                    val comment = comments.first()
                    appendLine("last at ${comment.updatedAt}")
                    append(comment.user.avatar(contact, 30)).appendLine(comment.user?.nameOrLogin)
                    appendLine(comment.reactions?.render())
                    append(comment.text)
                }
            }
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = title
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOf(
                    "$notice with #${number} pull by $ownerNameOrLogin",
                    "$state, $comments comments, $commits commits, $changedFiles files",
                    "update at $updatedAt, $mergeableState",
                    autoMerge ?: activeLockReason ?: labels.joinToString { it.name }
                )

                override fun generateSummary(forward: RawForwardMessage): String {
                    return autoMerge ?: activeLockReason ?: labels.joinToString { it.name }
                }
            }
            contact.bot named association.name at createdAt says buildMessageChain {
                append(user.avatar(contact, 30)).appendLine(user?.nameOrLogin)
                appendLine(htmlUrl)
                appendLine(labels.joinToString { it.description ?: it.name })
                appendLine(reactions?.render())
                append(body)
            }
            contact.bot named "status" at createdAt says buildMessageChain {
                if (mergedBy != null) {
                    append(mergedBy.avatar(contact, 30)).appendLine("merged at $closedAt by ${mergedBy.nameOrLogin}")
                } else if (closedBy != null) {
                    append(closedBy.avatar(contact, 30)).appendLine("closed at $closedAt by ${closedBy.nameOrLogin}")
                }
                if (assignees.isNotEmpty()) {
                    appendLine("assigned to ")
                    assignees.forEach { assignee ->
                        append(assignee.avatar(contact, 30)).append(assignee.nameOrLogin).append(" ")
                    }
                    appendLine()
                }
                if (requestedReviewers.isNotEmpty()) {
                    appendLine("review by ")
                    requestedReviewers.forEach { reviewer ->
                        append(reviewer.avatar(contact, 30)).append(reviewer.nameOrLogin).append(" ")
                    }
                    appendLine()
                }
                if (milestone != null) {
                    appendLine("milestone with ${milestone.title}")
                }
            }
            if (comments > 0) {
                val (owner, repo) = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.destructured
                val comments = github.repo(owner = owner, repo = repo).pulls
                    .comments(number = number) {
                        sort = ElementSort.created
                        direction = Direction.asc
                    }

                for (comment in comments) {
                    contact.bot named comment.association.name at comment.createdAt says buildMessageChain {
                        append(comment.user.avatar(contact, 30)).appendLine(comment.user?.nameOrLogin)
                        appendLine(comment.reactions?.render())
                        append(comment.body)
                    }
                }
            }
        }
    }
}

public suspend fun Release.toMessage(contact: Contact, format: Format, notice: String): Message {
    return when (format) {
        Format.OLD -> buildMessageChain {
            appendLine(author.avatar(contact, 50))
            appendLine("$notice with release by $ownerNameOrLogin ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("PUBLISHED_AT: $publishedAt ")
            appendLine("URL: $htmlUrl ")
            appendLine("NAME: $name ")
            if (reactions != null) appendLine(reactions.render())
            if (body != null && body.length < 50) appendLine(body)
        }
        Format.TEXT -> TODO("Release TEXT")
        Format.FORWARD -> TODO("Release FORWARD")
    }
}

public suspend fun Commit.toMessage(contact: Contact, type: Format, notice: String): Message {
    return when (type) {
        Format.OLD -> buildMessageChain {
            appendLine(author.avatar(contact, 50))
            appendLine("$notice with commit by $ownerNameOrLogin ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine(detail.message)
        }
        Format.TEXT -> TODO("Commit TEXT")
        Format.FORWARD -> TODO("Commit FORWARD")
    }
}

public suspend fun Repo.toMessage(contact: Contact, type: Format, notice: String): Message {
    return when (type) {
        Format.OLD -> buildMessageChain {
            appendLine(owner.avatar(contact, 50))
            appendLine("$notice with repo by $ownerNameOrLogin ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("STARGAZERS_COUNT: $stargazersCount")
            appendLine("LANGUAGE: $language ")
            appendLine("DESCRIPTION: $description ")
        }
        Format.TEXT -> TODO("Repo TEXT")
        Format.FORWARD -> TODO("Repo FORWARD")
    }
}

public suspend fun Milestone.toMessage(contact: Contact, type: Format, notice: String): Message {
    return when (type) {
        Format.OLD -> buildMessageChain {
            appendLine(creator.avatar(contact, 50))
            appendLine("$notice with milestone by $ownerNameOrLogin ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("CREATED_AT: $updatedAt ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            appendLine("DESCRIPTION: $description ")
        }
        Format.TEXT -> TODO("Milestone TEXT")
        Format.FORWARD -> TODO("Milestone FORWARD")
    }
}

public suspend fun Readme.toMessage(contact: Contact): Message {
    if (contact is FileSupported) {
        contact.launch(SupervisorJob()) {
            val file = if (selenium) {
                logger.info { "将尝试发送 ${sha}.pdf to ${contact.render()}" }
                pdf()
            } else {
                markdown()
            }

            file.toExternalResource().use {
                contact.files.uploadNewFile(filepath = file.name, it)
            }
        }
    }

    return htmlUrl.toPlainText()
}