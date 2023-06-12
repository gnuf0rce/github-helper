/*
 * Copyright 2021-2023 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.exception.*
import io.github.gnuf0rce.mirai.github.data.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.*
import net.mamoe.mirai.Bot as MiraiBot
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.FileSupported
import net.mamoe.mirai.contact.PermissionDeniedException
import net.mamoe.mirai.contact.file.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.*
import org.openqa.selenium.*
import xyz.cssxsh.selenium.*
import java.io.File
import java.time.*
import java.util.*

private val contacts: MutableMap<Long, Contact> = WeakHashMap()

internal fun Contact(id: Long): Contact {
    val contact = contacts[id]
    if (contact != null) return contact
    for (bot in MiraiBot.instances.shuffled()) {
        for (friend in bot.friends) {
            if (friend.id == id) {
                contacts[id] = friend
                return friend
            }
        }
        for (group in bot.groups) {
            if (group.id == id) return group
            for (member in group.members) {
                if (member.id == id) {
                    contacts[id] = member
                    return member
                }
            }
        }
    }
    throw kotlin.NoSuchElementException("Contact($id)")
}

// region UserStats

private const val STATS_API = "https://github-readme-stats.vercel.app/api"

private const val RANK_REGEX = """(?<=rank-text[^>]{0,1024}>[^>]{0,1024}>\W{0,1024})[\w+-]+"""

private const val STARS_REGEX = """(?<=stars[^>]{0,1024}>\W{0,1024})[^<\s]+"""

private const val COMMITS_REGEX = """(?<=commits[^>]{0,1024}>\W{0,1024})[^<\s]+"""

private const val PRS_REGEX = """(?<=prs[^>]{0,1024}>\W{0,1024})[^<\s]+"""

private const val ISSUES_REGEX = """(?<=issues[^>]{0,1024}>\W{0,1024})[^<\s]+"""

private const val CONTRIB_REGEX = """(?<=contribs[^>]{0,1024}>\W{0,1024})[^<\s]+"""

private const val OFFSET_REGEX = """(?<=dashoffset: )\d+\.?\d+"""

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
            }.body()
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
            rank = requireNotNull(RANK_REGEX.toRegex().find(xml)) { "UserStats 解析 rank 失败" }.value,
            stars = requireNotNull(STARS_REGEX.toRegex().find(xml)) { "UserStats 解析 stars 失败" }.value,
            commits = requireNotNull(COMMITS_REGEX.toRegex().find(xml)) { "UserStats 解析 commits 失败" }.value,
            prs = requireNotNull(PRS_REGEX.toRegex().find(xml)) { "UserStats 解析 prs 失败" }.value,
            issues = requireNotNull(ISSUES_REGEX.toRegex().find(xml)) { "UserStats 解析 issues 失败" }.value,
            contrib = requireNotNull(CONTRIB_REGEX.toRegex().find(xml)) { "UserStats 解析 contrib 失败" }.value,
            percentage = kotlin.run {
                val pre = requireNotNull(OFFSET_REGEX.toRegex().find(xml)) { "UserStats 解析 percentage 失败" }
                val next = requireNotNull(OFFSET_REGEX.toRegex().find(xml, pre.range.last)) { "UserStats 解析 percentage 失败" }
                ((1 - next.value.toDouble() / pre.value.toDouble()) * 100).toInt()
            }
        )
    } catch (cause: Exception) {
        svg.renameTo(stats.resolve("${login}.error.svg"))
        throw cause
    }
}

internal suspend fun User.card(contact: Contact): Message {
    val image = avatar(contact)
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

// endregion

// region WebPage

@Serializable
public enum class Format { OLD, TEXT, FORWARD, GRAPH }

internal suspend fun Owner?.avatar(contact: Contact, size: Int = UserAvatarSize, client: GitHubClient = github): Image {
    val avatarUrl = this?.avatarUrl ?: "https://avatars.githubusercontent.com/u/10137"
    val login = this?.login ?: "ghost"

    val folder = ImageFolder.resolve("avatar")
    folder.mkdirs()
    val cache = folder.listFiles()?.find { it.name.startsWith("${login}.${size}") }
    if (cache != null && System.currentTimeMillis() - cache.lastModified() < 7 * 24 * 3600_000) {
        return cache.uploadAsImage(contact)
    }

    val file = client.useHttpClient { http ->
        val response = http.get(avatarUrl) {
            parameter("s", size)
        }
        val format = response.contentType()?.contentSubtype ?: "jpg"
        val file = folder.resolve("${login}.${size}.${format}")

        file.writeBytes(response.body())

        // XXX: handle default avatar
        runInterruptible(Dispatchers.IO) {
            val input = javax.imageio.ImageIO.read(file)
            if (input.width > size || input.height > size) {
                logger.info { "avatar miss: ${response.request.url}" }
                val output = java.awt.image.BufferedImage(size, size, input.type.coerceAtLeast(2))
                output.graphics.drawImage(input, 0, 0, size, size, null)
                javax.imageio.ImageIO.write(output, format, file)
            }
        }

        file
    }

    return file.uploadAsImage(contact)
}

internal suspend fun <T> T.graph(contact: Contact, client: GitHubClient = github): Image
    where T : WebPage, T : Entry {
    return when (this) {
        is Repo, is Issue, is Pull, is Commit -> {
            val graph = ImageFolder.resolve("graph").resolve("${nodeId}.png")
            val imageUrl = graphUrl as String
            if (graph.exists().not()) {
                graph.parentFile.mkdirs()
                client.useHttpClient { http ->
                    val response = http.get(imageUrl)
                    graph.writeBytes(response.body())
                }
            }
            graph.uploadAsImage(contact)
        }
        else -> {
            val screenshot = ImageFolder.resolve("screenshot").resolve("${nodeId}.png")
            if (screenshot.exists().not() || (System.currentTimeMillis() - screenshot.lastModified()) >= 86400_000) {
                screenshot.parentFile.mkdirs()
                if (selenium.not()) throw IllegalArgumentException("Selenium 未安装，无法截图")
                val bytes = useRemoteWebDriver { driver ->
                    driver.manage().window().size = Dimension(1920, 1080)
                    driver.get(htmlUrl)
                    delay(10_000)
                    driver.getScreenshotAs(OutputType.BYTES)
                }
                screenshot.writeBytes(bytes)
            }
            screenshot.uploadAsImage(contact)
        }
    }
}

internal fun MessageChainBuilder.appendLine(image: Image) = append(image).appendLine()

internal fun Appendable.appendLine(reactions: Reactions?): Appendable {
    reactions?.run {
        if (plus > 0) append("👍:$plus")
        if (minus > 0) append("👎:$minus")
        if (laugh > 0) append("😄:$laugh")
        if (confused > 0) append("😕:$confused")
        if (heart > 0) append("❤:$heart")
        if (hooray > 0) append("🎉:$hooray")
        if (rocket > 0) append("🚀:$rocket")
        if (eyes > 0) append("👀:$eyes")
    }
    return appendLine()
}

internal fun Appendable.appendParagraph(content: String?, maxLength: Int = TextMaxLength) = apply {
    when {
        content == null -> Unit
        content.length <= maxLength -> append(content)
        else -> appendLine(content.subSequence(0, maxLength)).append("......")
    }
}

@ForwardMessageDsl
private infix fun ForwardMessageBuilder.BuilderNode.at(offsetDateTime: OffsetDateTime) = apply {
    time = offsetDateTime.toEpochSecond().toInt()
}

public suspend fun WebPage.toMessage(contact: Contact, format: Format, notice: String, since: OffsetDateTime): Message {
    return when (this) {
        is Issue -> toMessage(contact, format, notice, since)
        is Issue.PullRequest -> htmlUrl.toPlainText()
        is IssueComment -> toMessage(contact)
        is Pull -> toMessage(contact, format, notice, since)
        is PullRequestReviewComment -> toMessage(contact)
        is Release -> toMessage(contact, format, notice)
        is Commit -> toMessage(contact, format, notice)
        is Commit.Tree -> htmlUrl.toPlainText()
        is Commit.File -> toMessage()
        is CommitComment -> toMessage(contact)
        is Repo -> toMessage(contact, format, notice)
        is Repo.CodeOfConduct -> body.toPlainText()
        is Milestone -> toMessage(contact, format, notice)
        is User -> toMessage(contact, format)
        is Organization -> toMessage(contact, format)
        is Bot -> htmlUrl.toPlainText()
        is License -> htmlUrl.toPlainText()
        is Readme -> toMessage(contact)
        is Team -> htmlUrl.toPlainText()
        is GithubAppInfo -> htmlUrl.toPlainText()
    }
}

public suspend fun Contact.sendEntry(entry: WebPage, notice: String, format: Format, since: OffsetDateTime)
    : MessageReceipt<Contact> = sendMessage(entry.toMessage(this, format, notice, since))


// region User

public suspend fun User.toMessage(contact: Contact, format: Format): Message {
    return when (format) {
        Format.OLD -> buildMessageChain {
            appendLine(avatar(contact))
            appendLine("NAME: $name ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("BLOG: $blog")
            appendLine("EMAIL: $email")
        }
        Format.TEXT -> buildMessageChain {
            appendLine("<$nameOrLogin> created at $createdAt")
            appendLine(blog ?: htmlUrl)
            appendLine("email: $email, twitter: $twitterUsername")
            appendLine("$publicRepos repos, $followers followers")
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = nameOrLogin
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOfNotNull(
                    "<$nameOrLogin> created at $createdAt",
                    "email: $email, twitter: $twitterUsername",
                    "$publicRepos repos, $followers followers"
                )
            }
            contact.bot named nameOrLogin at createdAt says {
                append(avatar(contact)).appendLine(nameOrLogin)
                appendLine(blog ?: htmlUrl)
                appendLine("email: $email, twitter: $twitterUsername")
                appendLine("$publicRepos repos, $followers followers")
            }

            // TODO: more info for user
        }
        Format.GRAPH -> graph(contact)
    }
}

// endregion

// region Organization

public suspend fun Organization.toMessage(contact: Contact, format: Format): Message {
    return when (format) {
        Format.OLD -> buildMessageChain {
            appendLine(avatar(contact))
            appendLine("NAME: $name ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("COMPANY: $company")
            appendLine("EMAIL: $email")
            appendLine("VERIFIED: $isVerified")
            appendParagraph(description)
        }
        Format.TEXT -> buildMessageChain {
            appendLine("<$nameOrLogin> created at $createdAt")
            appendLine("$email ${if (isVerified) "verified" else ""}")
            appendLine("$publicRepos repos, $followers followers")
            appendParagraph(description)
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = nameOrLogin
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOfNotNull(
                    "<$nameOrLogin> created at $createdAt",
                    "$email ${if (isVerified) "verified" else ""}",
                    "$publicRepos repos, $followers followers",
                    description
                )
            }
            contact.bot named nameOrLogin at createdAt says {
                append(avatar(contact)).appendLine(nameOrLogin)
                appendLine(htmlUrl)
                appendLine("$email ${if (isVerified) "verified" else ""}")
                appendLine("$publicRepos repos, $followers followers")
                append(description)
            }

            // TODO: more info for org
        }
        Format.GRAPH -> graph(contact)
    }
}

// endregion

// region Issue

public suspend fun Issue.toMessage(contact: Contact, format: Format, notice: String, since: OffsetDateTime): Message {
    return when (format) {
        Format.OLD -> buildMessageChain {
            appendLine(user.avatar(contact, 88))
            appendLine("$notice with issue by ${owner?.nameOrLogin} ")
            appendLine("URL: $htmlUrl ")
            appendLine("TITLE: $title ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("STATE: $state ")
            if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
            if (reactions != null) appendLine(reactions)
            if (text != null && text.length < TextMaxLength) appendLine(text)
        }
        Format.TEXT -> when {
            // case 1 new open issue
            createdAt == updatedAt -> buildMessageChain {
                appendLine(user.avatar(contact))
                appendLine("[$notice] New issue <$title> opened by ${user?.nameOrLogin}")
                appendLine(htmlUrl)
                appendParagraph(text)
            }
            // case 2 merged issue
            mergedAt == updatedAt -> buildMessageChain {
                appendLine("[$notice] Issue <$title> merged by ${pullRequest?.htmlUrl ?: mergedBy?.nameOrLogin}")
                appendLine(htmlUrl)
                appendLine(stateReason)
            }
            // case 3 closed issue
            closedAt == updatedAt -> buildMessageChain {
                appendLine(closedBy.avatar(contact))
                appendLine("[$notice] Issue <$title> closed by ${closedBy?.nameOrLogin}")
                appendLine(htmlUrl)
                appendLine(stateReason)
            }
            // case 4 new comment or event for issue
            else -> buildMessageChain {
                val repo = repo(full = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.value)

                val comments = repo.issues.comments(number = number) {
                    this.sort = ElementSort.updated
                    this.direction = Direction.asc
                    this.since = since
                }.filter { it.updatedAt > since }

                if (comments.isNotEmpty()) {
                    appendLine("[$notice] Issue <$title> new ${comments.size} comments")
                    appendLine(htmlUrl)
                    val comment = comments.first()
                    appendLine("last comment at ${comment.updatedAt}")
                    append(comment.user.avatar(contact)).appendLine(comment.user?.nameOrLogin)
                    appendLine(comment.reactions)
                    appendParagraph(comment.text)

                    return@buildMessageChain
                }

                val events = repo.issues.events(number = number).filter { it.createdAt >= since }
                if (events.isNotEmpty()) {
                    appendLine("[$notice] Issue <$title> new ${events.size} events")
                    appendLine(htmlUrl)
                    for (event in events) {
                        append(event.toMessage(contact)).appendLine()
                    }

                    return@buildMessageChain
                }

                appendLine("[$notice] Issue <$title> has change")
                appendLine(htmlUrl)
                appendLine(reactions)
                if (assignees.isNotEmpty()) {
                    appendLine("assignees: ")
                    assignees.forEach { assignee ->
                        append(assignee.avatar(contact)).append(assignee.nameOrLogin).append(" ")
                    }
                    appendLine()
                }
                if (labels.isNotEmpty()) {
                    appendLine("labels: ${labels.joinToString { it.name }}")
                }
                if (milestone != null) {
                    appendLine("milestone with ${milestone.title}")
                }
            }
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = title
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOf(
                    "$notice issue #${number} by $ownerNameOrLogin",
                    "$state, $comments comments",
                    "update at $updatedAt",
                    stateReason ?: activeLockReason ?: labels.joinToString { it.name }
                )

                override fun generateSummary(forward: RawForwardMessage): String {
                    return stateReason ?: activeLockReason ?: labels.joinToString { it.name }
                }
            }
            contact.bot named association.name at createdAt says {
                append(user.avatar(contact)).appendLine(user?.nameOrLogin)
                appendLine(htmlUrl)
                labels.joinTo(buffer = this, prefix = "label: ", postfix = "\n") { it.name }
                appendLine(reactions)
                append(body)
            }
            contact.bot named "status" at createdAt says {
                if (assignees.isNotEmpty()) {
                    appendLine("assigned to ")
                    assignees.forEach { assignee ->
                        append(assignee.avatar(contact)).append(assignee.nameOrLogin).append(" ")
                    }
                    appendLine()
                }
                if (milestone != null) {
                    appendLine("milestone with ${milestone.title}")
                }
                if (closedBy != null) {
                    append(closedBy.avatar(contact)).appendLine("closed at $closedAt by ${closedBy.nameOrLogin}")
                }
                build()
                ifEmpty {
                    appendLine("created")
                }
            }
            val repo = repo(full = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.value)
            if (comments > 0) {
                val comments = repo.issues.comments(number = number) {
                    sort = ElementSort.created
                    direction = Direction.asc
                }

                for (comment in comments) {
                    contact.bot named "comment" at comment.createdAt says comment.toMessage(contact)
                }
            }
            val events = repo.issues.events(number = number)
            for (event in events) {
                contact.bot named "event" at event.createdAt says event.toMessage(contact)
            }
        }
        Format.GRAPH -> graph(contact)
    }
}

public suspend fun IssueComment.toMessage(contact: Contact): Message = buildMessageChain {
    append(user.avatar(contact)).appendLine(user?.nameOrLogin)
    appendLine(reactions)
    append(body)
}

public suspend fun IssueEvent.toMessage(contact: Contact): Message = buildMessageChain {
    append(actor.avatar(contact)).appendLine(actor?.nameOrLogin)
    append(event)
    when (event) {
        "renamed" -> {
            append(" <").append(rename?.from).append("> to <").append(rename?.to).append(">")
        }
        "labeled",
        "unlabeled" -> {
            append(" ").append(label?.name)
        }
        "assigned" -> {
            append(" ").append(assignee.avatar(contact)).append(assignee?.nameOrLogin)
        }
        "milestoned" -> {
            append(" to ").append(milestone?.title)
        }
        "demilestoned" -> {
            append(" from ").append(milestone?.title)
        }
        "merged",
        "referenced" -> {
            append(" on ").append(commitId)
        }
        "locked" -> {
            append(" as ").append(lockReason)
        }
        "closed" -> {
            append(" as ").append(stateReason)
        }
        "head_ref_force_pushed",
        "ready_for_review",
        "connected",
        "mentioned",
        "subscribed",
        "unsubscribed",
        "pinned",
        "unpinned",
        "transferred",
        "reopened",
        "unlocked" -> {
            //
        }
        "review_requested",
        "review_request_removed" -> {
            append(" from ").append(requestedReviewer.avatar(contact)).append(requestedReviewer?.nameOrLogin)
        }
        "added_to_project" -> {
            append(" ").append(projectCard?.projectId).append(" column ").append(projectCard?.columnName)
            if (projectCard?.previousColumnName != null) {
                append(" from ").append(projectCard.previousColumnName)
            }
        }
        "moved_columns_in_project" -> {
            append(" ").append(projectCard?.projectId).append(" column ").append(projectCard?.columnName)
            if (projectCard?.previousColumnName != null) {
                append(" from ").append(projectCard.previousColumnName)
            }
        }
        else -> {
            logger.warning { "未知事件类型 $event with $url" }
        }
    }
}

// endregion

// region Pull

public suspend fun Pull.toMessage(contact: Contact, format: Format, notice: String, since: OffsetDateTime): Message {
    return when (format) {
        Format.OLD -> buildMessageChain {
            appendLine(user.avatar(contact, 88))
            appendLine("$notice with pull by $ownerNameOrLogin ")
            appendLine("URL: $htmlUrl ")
            appendLine("TITLE: $title ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("STATE: $state ")
            if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
            if (reactions != null) appendLine(reactions)
            if (text != null && text.length < TextMaxLength) appendLine(text)
        }
        Format.TEXT -> when {
            // case 1 new open pull
            createdAt == updatedAt -> buildMessageChain {
                appendLine(user.avatar(contact))
                appendLine("[$notice] New pull request <$title> opened by ${user?.nameOrLogin}")
                appendLine(htmlUrl)
                appendParagraph(text)
            }
            // case 2 merged pull
            mergedAt == updatedAt -> buildMessageChain {
                appendLine(mergedBy.avatar(contact))
                appendLine("[$notice] Pull Request <$title> merged by ${mergedBy?.nameOrLogin}")
                appendLine(htmlUrl)
            }
            // case 3 closed pull
            closedAt == updatedAt -> buildMessageChain {
                appendLine(closedBy.avatar(contact))
                appendLine("[$notice] Pull Request <$title> closed by ${closedBy?.nameOrLogin}")
                appendLine(htmlUrl)
                appendParagraph(autoMerge?.commitTitle)
            }
            // case 4 new comment or event for issue
            else -> buildMessageChain {
                val repo = repo(full = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.value)

                val reviews = repo.pulls.comments(number = number) {
                    this.sort = ElementSort.updated
                    this.direction = Direction.asc
                    this.since = since
                }.filter { it.updatedAt > since }

                if (reviews.isNotEmpty()) {
                    appendLine("[$notice] Pull Request <$title> new ${reviews.size} reviews")
                    appendLine(htmlUrl)
                    val review = reviews.maxByOrNull { it.updatedAt }!!
                    appendLine("last review at ${review.updatedAt}")
                    append(review.user.avatar(contact)).appendLine(review.user?.nameOrLogin)
                    appendLine(review.reactions)
                    appendParagraph(review.text)

                    return@buildMessageChain
                }

                val comments = repo.issues.comments(number = number) {
                    this.sort = ElementSort.updated
                    this.direction = Direction.asc
                    this.since = since
                }.filter { it.updatedAt > since }

                if (comments.isNotEmpty()) {
                    appendLine("[$notice] Pull Request <$title> new ${comments.size} comments")
                    appendLine(htmlUrl)
                    val comment = comments.maxByOrNull { it.updatedAt }!!
                    appendLine("last comment at ${comment.updatedAt}")
                    append(comment.user.avatar(contact)).appendLine(comment.user?.nameOrLogin)
                    appendLine(comment.reactions)
                    appendParagraph(comment.text)

                    return@buildMessageChain
                }

                val events = repo.issues.events(number = number).filter { it.createdAt >= since }
                if (events.isNotEmpty()) {
                    appendLine("[$notice] Issue <$title> new ${events.size} events")
                    appendLine(htmlUrl)
                    for (event in events) {
                        append(event.toMessage(contact)).appendLine()
                    }

                    return@buildMessageChain
                }

                appendLine("[$notice] Pull Request <$title> has change")
                appendLine(htmlUrl)
                appendLine(reactions)
                if (requestedReviewers.isNotEmpty()) {
                    appendLine("reviewers:")
                    requestedReviewers.forEach { reviewer ->
                        append(" ").append(reviewer.avatar(contact)).append(reviewer.nameOrLogin)
                    }
                    appendLine()
                }
                if (assignees.isNotEmpty()) {
                    appendLine("assignees:")
                    assignees.forEach { assignee ->
                        append(" ").append(assignee.avatar(contact)).append(assignee.nameOrLogin)
                    }
                    appendLine()
                }
                if (labels.isNotEmpty()) {
                    appendLine("label: ${labels.joinToString { it.name }}")
                }
                if (milestone != null) {
                    appendLine("milestone with ${milestone.title}")
                }
            }
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = title
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOf(
                    "$notice pull #${number} by $ownerNameOrLogin",
                    "$state, ${comments + reviewComments} comments, $commits commits, $changedFiles files",
                    "update at $updatedAt, $mergeableState",
                    autoMerge?.commitTitle ?: activeLockReason ?: labels.joinToString { it.name }
                )

                override fun generateSummary(forward: RawForwardMessage): String {
                    return activeLockReason ?: labels.joinToString { it.name }
                }
            }
            contact.bot named association.name at createdAt says {
                append(user.avatar(contact)).appendLine(user?.nameOrLogin)
                appendLine(htmlUrl)
                labels.joinTo(buffer = this, prefix = "label: ", postfix = "\n") { it.name }
                appendLine(reactions)
                append(body)
            }
            contact.bot named "status" at createdAt says {
                if (mergedBy != null) {
                    append(mergedBy.avatar(contact)).appendLine("merged at $closedAt by ${mergedBy.nameOrLogin}")
                } else if (closedBy != null) {
                    append(closedBy.avatar(contact)).appendLine("closed at $closedAt by ${closedBy.nameOrLogin}")
                }
                if (assignees.isNotEmpty()) {
                    appendLine("assigned to ")
                    assignees.forEach { assignee ->
                        append(assignee.avatar(contact)).append(assignee.nameOrLogin).append(" ")
                    }
                    appendLine()
                }
                if (requestedReviewers.isNotEmpty()) {
                    appendLine("review by ")
                    requestedReviewers.forEach { reviewer ->
                        append(reviewer.avatar(contact)).append(reviewer.nameOrLogin).append(" ")
                    }
                    appendLine()
                }
                if (milestone != null) {
                    appendLine("milestone with ${milestone.title}")
                }
                build()
                ifEmpty {
                    appendLine("created")
                }
            }
            val repo = repo(full = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.value)
            if (reviewComments > 0) {
                val comments = repo.pulls.comments(number = number) {
                    sort = ElementSort.created
                    direction = Direction.asc
                }

                for (comment in comments) {
                    contact.bot named "review_comment" at comment.createdAt says comment.toMessage(contact)
                }
            }
            if (comments > 0) {
                val comments = repo.issues.comments(number = number) {
                    sort = ElementSort.created
                    direction = Direction.asc
                }

                for (comment in comments) {
                    contact.bot named "comment" at comment.createdAt says comment.toMessage(contact)
                }
            }
            val events = repo.issues.events(number = number)
            for (event in events) {
                contact.bot named "event" at event.createdAt says event.toMessage(contact)
            }
        }
        Format.GRAPH -> graph(contact)
    }
}

public suspend fun PullRequestReviewComment.toMessage(contact: Contact): Message = buildMessageChain {
    append(user.avatar(contact)).appendLine(user?.nameOrLogin)
    appendLine(reactions)
    append(body)
}

// endregion

// region Release

public suspend fun Release.toMessage(contact: Contact, format: Format, notice: String): Message {
    if (contact is FileSupported) {
        supervisorScope {
            launch {
                uploadTo(contact)
            }
        }
    }

    return when (format) {
        Format.OLD -> buildMessageChain {
            appendLine(author.avatar(contact))
            appendLine("$notice with release by $ownerNameOrLogin ")
            appendLine("NAME: ${name ?: tagName} ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("PUBLISHED_AT: $publishedAt ")
            appendLine("URL: $htmlUrl ")
            if (reactions != null) appendLine(reactions)
            if (text != null && text.length < TextMaxLength) appendLine(text)
        }
        Format.TEXT -> buildMessageChain {
            appendLine(author.avatar(contact))
            appendLine("[$notice] New release <${name ?: tagName}> opened by ${author?.nameOrLogin}")
            appendLine(htmlUrl)
            appendLine(reactions)
            assets.joinTo(buffer = this, prefix = "assets: ") { it.name }.appendLine()
            appendParagraph(text)
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = name ?: tagName
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOf(
                    "$notice release $tagName by $ownerNameOrLogin",
                    "$status, ${assets.size} assets, $mentionsCount mentions",
                    "update at $updatedAt",
                    targetCommitish
                )
            }

            contact.bot named targetCommitish at createdAt says {
                append(author.avatar(contact)).appendLine(author?.nameOrLogin)
                appendLine(htmlUrl)
                appendLine(reactions)
                append(body)
            }
            for (asset in assets) {
                contact.bot named "asset" at asset.createdAt says asset.toMessage(contact)
            }
        }
        Format.GRAPH -> graph(contact)
    }
}

public suspend fun Release.uploadTo(contact: FileSupported) {
    if (assets.isEmpty()) return
    val (owner, repo) = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.destructured
    val folder = with(contact.files.root) {
        resolveFolder("${repo}@${owner}")
            ?: resolveFolder(repo)
            ?: resolveFolder(owner)
            ?: try {
                createFolder("${repo}@${owner}")
            } catch (_: PermissionDeniedException) {
                this
            }
    }
    for (asset in assets) {
        // TODO: 上传大小上限
        asset.uploadTo(folder)
    }
}

public suspend fun Release.Asset.toMessage(contact: Contact): Message = buildMessageChain {
    append(uploader.avatar(contact)).appendLine(uploader?.nameOrLogin)
    appendLine("update at $updatedAt")
    appendLine("type: ${contentType.contentType}, size: $size, downloaded: $downloadCount")
    append(browserDownloadUrl)
}

public suspend fun Release.Asset.uploadTo(folder: AbsoluteFolder) {
    val asset = CacheFolder.resolve("release").resolve(nodeId).resolve(name)
    if (asset.exists().not()) {
        asset.parentFile.mkdirs()
        github.useHttpClient { http ->
            val response = http.get(browserDownloadUrl)
            try {
                response.bodyAsChannel().copyAndClose(asset.writeChannel())
            } finally {
                if (asset.length() != size) asset.delete()
            }
            asset.setLastModified(updatedAt.toInstant().toEpochMilli())
        }
    }
    asset.toExternalResource().use { resource ->
        folder.uploadNewFile(name, resource)
    }
}

// endregion

// region Commit

public suspend fun Commit.toMessage(contact: Contact, type: Format, notice: String): Message {
    return when (type) {
        Format.OLD -> buildMessageChain {
            appendLine(author.avatar(contact))
            appendLine("$notice with commit by $ownerNameOrLogin ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine(detail.message)
        }
        Format.TEXT -> when {
            // case 1 new open commit
            detail.commentCount == 0 -> buildMessageChain {
                appendLine(author.avatar(contact))
                appendLine("[$notice] New commit $key by $ownerNameOrLogin")
                appendLine(htmlUrl)
                appendLine("${stats.additions} additions, ${stats.deletions} deletions, ${files.size} files")
                appendLine(detail.message)
            }
            // case 2 new comment for commit
            else -> buildMessageChain {
                val repo = repo(full = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.value)
                val comment = repo.commit(sha = sha).comments().maxByOrNull { it.updatedAt }!!

                appendLine("[$notice] commit $key new comment by ${comment.user?.nameOrLogin}")
                appendLine(htmlUrl)
                appendLine("last comment at ${comment.updatedAt}")
                append(comment.user.avatar(contact)).appendLine(comment.user?.nameOrLogin)
                appendLine(comment.reactions)
                appendParagraph(comment.text)
            }
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = "[$notice] commit $key"
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOf(
                    "$notice commit $key by $ownerNameOrLogin",
                    "${stats.additions} additions, ${stats.deletions} deletions, ${files.size} files, ${detail.commentCount} comment",
                    detail.verification.reason
                )
            }

            contact.bot named ownerNameOrLogin at createdAt says {
                appendLine(author.avatar(contact))
                appendLine("[$notice] commit $key by $ownerNameOrLogin")
                appendLine(htmlUrl)
                appendLine("${stats.additions} additions, ${stats.deletions} deletions, ${files.size} files")
                appendLine(detail.message)
            }

            for (file in files) {
                contact.bot named "file" at createdAt says file.toMessage()
            }

            val repo = repo(full = FULL_REGEX.find(Url(htmlUrl).encodedPath)!!.value)
            if (detail.commentCount > 0) {
                val comments = repo.commit(sha = sha).comments()

                for (comment in comments) {
                    contact.bot named "comment" at comment.createdAt says comment.toMessage(contact)
                }
            }
        }
        Format.GRAPH -> graph(contact)
    }
}

public fun Commit.File.toMessage(): Message = buildMessageChain {
    if (previousFilename.isEmpty()) {
        append("$status ").appendLine(filename)
    } else {
        append(previousFilename).append(" $status to ").appendLine(filename)
    }
}

public suspend fun CommitComment.toMessage(contact: Contact): Message = buildMessageChain {
    append(user.avatar(contact)).appendLine(user?.nameOrLogin)
    appendLine(reactions)
    append(body)
}

// endregion

// region Repo

public suspend fun Repo.toMessage(contact: Contact, type: Format, notice: String): Message {
    return when (type) {
        Format.OLD -> buildMessageChain {
            appendLine(owner.avatar(contact))
            appendLine("$notice with repo by $ownerNameOrLogin ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("STARGAZERS_COUNT: $stargazersCount ")
            appendLine("LANGUAGE: $language ")
            appendParagraph(description)
        }
        Format.TEXT -> when {
            // case 1 new open repo
            createdAt == updatedAt -> buildMessageChain {
                appendLine("[$fullName] created at $createdAt")
                appendLine(htmlUrl)
                when {
                    templateRepository != null -> appendLine("from template [${templateRepository.fullName}]")
                    fork -> appendLine("from fork [...]")
                }
                appendParagraph(description)
            }
            // case 2 has update repo
            else -> buildMessageChain {
                appendLine("[$fullName] updated at $updatedAt")
                appendLine(htmlUrl)
                when {
                    templateRepository != null -> appendLine("from template [${templateRepository.fullName}]")
                    fork -> appendLine("from fork [...]")
                }
                appendLine("$stargazersCount stars, $subscribersCount subscribers, $forksCount forks, $openIssuesCount issues")
                appendLine(topics.joinToString())
                appendParagraph(description)
            }
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = "[$fullName] updated at $updatedAt"
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOfNotNull(
                    "$stargazersCount stars, $subscribersCount subscribers, $forksCount forks, $openIssuesCount issues",
                    topics.joinToString(),
                    language,
                    license?.name,
                    description
                )
            }

            val repo = repo(full = fullName)

            contact.bot named "Info" at createdAt says {
                append(owner.avatar(contact)).appendLine(owner?.nameOrLogin)
                appendLine(htmlUrl)
                appendLine("created at $createdAt")
                appendLine("updated at $updatedAt")
                appendLine("pushed at $pushedAt")
                appendLine("$openIssuesCount issues")
            }

            contact.bot named "About" at updatedAt says {
                appendLine(description)
                appendLine(homepage)
                appendLine(license?.name)
                appendLine("$stargazersCount stars")
                appendLine("$watchersCount watchers")
                appendLine("$forksCount forks")
            }

            contact.bot named "Release" at updatedAt says release@{
                val latest = try {
                    repo.releases.latest()
                } catch (cause: GitHubApiException) {
                    append(cause.message)
                    return@release
                }
                appendLine(latest.author.avatar(contact))
                appendLine("latest release ${latest.name ?: latest.tagName} by ${latest.author?.nameOrLogin}")
                appendLine(latest.htmlUrl)
                appendLine(latest.reactions)
                appendParagraph(latest.body)
            }

            contact.bot named "Contributors" at updatedAt says {
                appendLine("some contributors")
                for (collaborator in repo.collaborators(anonymous = false)) {
                    append(collaborator.avatar(contact)).appendLine(collaborator.nameOrLogin)
                }
            }

            contact.bot named "Languages" at updatedAt says {
                appendLine("language and additional")
                for ((language, additional) in repo.languages()) {
                    val size = when (additional) {
                        0L -> "0"
                        in 1L until (1 shl 10) -> "${additional}B"
                        in (1 shl 10) until (1 shl 20) -> "${additional.div(1 shl 10)}KB"
                        else -> "${additional.div(1 shl 20)}MB"
                    }
                    appendLine("$language: $size")
                }
            }
        }
        Format.GRAPH -> graph(contact)
    }
}

// endregion

// region Milestone

public suspend fun Milestone.toMessage(contact: Contact, type: Format, notice: String): Message {
    return when (type) {
        Format.OLD -> buildMessageChain {
            appendLine(creator.avatar(contact))
            appendLine("$notice with milestone by $ownerNameOrLogin ")
            appendLine("URL: $htmlUrl ")
            appendLine("TITLE: $title ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("DUE_ON: $dueOn ")
            appendLine("STATE: $state ")
            appendParagraph(description)
        }
        Format.TEXT -> when {
            // case 1 new open milestone
            createdAt == updatedAt -> buildMessageChain {
                appendLine(creator.avatar(contact))
                appendLine("[$notice] New milestone $title opened by ${creator?.nameOrLogin}")
                appendLine(htmlUrl)
                appendLine(dueOn?.let { "due on $it" } ?: "no due on")
                appendParagraph(description)
            }
            // case 2 milestone closed
            state == State.closed -> buildMessageChain {
                appendLine("[$notice] Milestone $title closed at $closedAt")
                appendLine(htmlUrl)
                appendLine("${openIssues + closedIssues} issues, $openIssues open, $closedIssues closed")
            }
            // case 3 milestone updated
            else -> buildMessageChain {
                appendLine("[$notice] Milestone $title update at $updatedAt")
                appendLine(htmlUrl)
                appendLine("${openIssues + closedIssues} issues, $openIssues open, $closedIssues closed")
            }
        }
        Format.FORWARD -> buildForwardMessage(contact) {
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = title
                override fun generatePreview(forward: RawForwardMessage): List<String> = listOfNotNull(
                    "$notice milestone $title by $ownerNameOrLogin",
                    "${openIssues + closedIssues} issues, $openIssues open, $closedIssues closed",
                    description
                )
            }

            contact.bot named ownerNameOrLogin says {
                appendLine(creator.avatar(contact))
                appendLine("[$notice] Milestone $title by $ownerNameOrLogin")
                appendLine(htmlUrl)
                when (state) {
                    State.open -> appendLine("opened at $createdAt")
                    State.closed -> appendLine("closed at $createdAt")
                }
                appendLine(dueOn?.let { "due on $it" } ?: "no due on")
                appendParagraph(description)
            }

            // TODO: show open issue
        }
        Format.GRAPH -> graph(contact)
    }
}

// endregion

// region Readme

public suspend fun Readme.toMessage(contact: Contact): Message {
    if (contact is FileSupported) {
        contact.launch {
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

public fun Readme.pdf(flush: Boolean = false): File {
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

public fun Readme.markdown(flush: Boolean = false): File {
    return ImageFolder.resolve("readme").resolve("${sha}.md").apply {
        if (exists().not() || flush) {
            parentFile.mkdirs()
            writeText(decode())
        }
    }
}

// endregion

// endregion