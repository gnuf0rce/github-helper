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
import io.github.gnuf0rce.github.entry.User
import io.github.gnuf0rce.mirai.plugin.data.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.contact.*
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
public enum class MessageType { DEFAULT, SHORT, FORWARD }

internal suspend fun Owner.avatar(flush: Boolean = false, client: GitHubClient = github): File {
    val url = Url(avatarUrl)
    return ImageFolder.resolve("avatar").resolve(url.filename).apply {
        if (exists().not() || flush) {
            parentFile.mkdirs()
            writeBytes(client.useHttpClient { client ->
                client.get(url)
            })
        }
    }
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
    val image = avatar().uploadAsImage(contact)
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
        is Organization -> avatar().uploadAsImage(contact)
    }
}

public suspend fun WebPage.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    return when (this) {
        is Issue -> toMessage(contact, type, notice)
        is Pull -> toMessage(contact, type, notice)
        is Release -> toMessage(contact, type, notice)
        is Commit -> toMessage(contact, type, notice)
        is Repo -> toMessage(contact, type, notice)
        is Milestone -> toMessage(contact, type, notice)
        is Owner -> toMessage(contact)
        is License -> (htmlUrl ?: name).toPlainText()
        is Issue.PullRequest -> htmlUrl.toPlainText()
        is Readme -> toMessage(contact)
        is Team -> htmlUrl.toPlainText()
        is Commit.Tree -> (htmlUrl ?: sha).toPlainText()
        is Comment -> htmlUrl.toPlainText()
    }
}

public suspend fun Contact.sendEntry(entry: WebPage, notice: String): MessageReceipt<Contact> =
    sendMessage(entry.toMessage(this, reply, notice))

public suspend fun Issue.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    return when (type) {
        MessageType.DEFAULT -> buildMessageChain {
            appendLine("$notice with issue by ${user.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
            if (reactions != null) appendLine(reactions.render())
            if (text != null && text.length < 50) appendLine(text)
        }
        MessageType.SHORT -> TODO()
        MessageType.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = title
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOf(
                    "$notice with issue by ${user.login}",
                    "CREATED_AT: $createdAt ",
                    "UPDATED_AT: $updatedAt ",
                    "STATE: $state "
                )
            }
            contact.bot named (owner.name ?: owner.login) at createdAt.toEpochSecond().toInt() says buildMessageChain {
                appendLine("URL: $htmlUrl ")
                if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
                if (reactions != null) appendLine(reactions.render())
                if (body != null) appendLine(body)
            }
            if (comments > 0) {
                val (owner, repo) = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.destructured
                val per = 30
                val comments = github.repo(owner = owner, repo = repo).issues
                    .comments(index = number, page = ((comments - 1) / per) + 1, per = per)

                for (comment in comments) {
                    contact.bot named (comment.owner.name ?: comment.owner.login) at
                        comment.createdAt.toEpochSecond().toInt() says buildMessageChain {
                        if (comment.reactions != null) appendLine(comment.reactions.render())
                        if (body != null) appendLine(comment.body)
                    }
                }
            }
        }
    }
}

public suspend fun Pull.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    return when (type) {
        MessageType.DEFAULT -> buildMessageChain {
            appendLine("$notice with pull by ${user.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
            if (reactions != null) appendLine(reactions.render())
            if (text != null && text.length < 50) appendLine(text)
        }
        MessageType.SHORT -> TODO()
        MessageType.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = title
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOf(
                    "$notice with issue by ${user.login}",
                    "CREATED_AT: $createdAt ",
                    "UPDATED_AT: $updatedAt ",
                    "STATE: $state "
                )
            }
            contact.bot named (owner.name ?: owner.login) at createdAt.toEpochSecond().toInt() says buildMessageChain {
                appendLine("URL: $htmlUrl ")
                if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
                if (reactions != null) appendLine(reactions.render())
                if (body != null) appendLine(body)
            }
            if (comments > 0) {
                val (owner, repo) = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.destructured
                val per = 30
                val comments = github.repo(owner = owner, repo = repo).issues
                    .comments(index = number, page = ((comments - 1) / per) + 1, per = per)

                for (comment in comments) {
                    contact.bot named (comment.owner.name ?: comment.owner.login) at
                        comment.createdAt.toEpochSecond().toInt() says buildMessageChain {
                        if (comment.reactions != null) appendLine(comment.reactions.render())
                        if (body != null) appendLine(comment.body)
                    }
                }
            }
        }
    }
}

public suspend fun Release.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = author.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.DEFAULT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with release by ${author.login} ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("PUBLISHED_AT: $publishedAt ")
            appendLine("URL: $htmlUrl ")
            appendLine("NAME: $name ")
            if (reactions != null) appendLine(reactions.render())
            if (body != null && body.length < 50) appendLine(body)
        }
        MessageType.SHORT -> TODO()
        MessageType.FORWARD -> TODO()
    }
}

public suspend fun Commit.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = author?.avatar()?.uploadAsImage(contact)
    return when (type) {
        MessageType.DEFAULT -> buildMessageChain {
            if (image != null) appendLine(image)
            appendLine("$notice with commit by ${author?.login ?: detail.author.email} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine(detail.message)
        }
        MessageType.SHORT -> TODO()
        MessageType.FORWARD -> TODO()
    }
}

public suspend fun Repo.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = owner.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.DEFAULT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with repo by ${owner.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("STARGAZERS_COUNT: $stargazersCount")
            appendLine("LANGUAGE: $language ")
            appendLine("DESCRIPTION: $description ")
        }
        MessageType.SHORT -> TODO()
        MessageType.FORWARD -> TODO()
    }
}

public suspend fun Milestone.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = creator.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.DEFAULT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with milestone by ${creator.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("CREATED_AT: $updatedAt ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            appendLine("DESCRIPTION: $description ")
        }
        MessageType.SHORT -> TODO()
        MessageType.FORWARD -> TODO()
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