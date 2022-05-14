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
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.*
import org.openqa.selenium.*
import xyz.cssxsh.selenium.*
import java.io.File
import java.time.*

internal fun Contact(id: Long): Contact = Bot.instances.firstNotNullOf { it.getContactOrNull(id) }

@Serializable
enum class MessageType { TEXT, XML, JSON }

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
                try {
                    driver.get(htmlUrl)
                } catch (exception: WebDriverException) {
                    driver.navigate().refresh()
                }
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

data class UserStats(
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
suspend fun Owner.toMessage(contact: Contact): Message {
    return when (this) {
        is User -> card(contact)
        is Organization -> avatar().uploadAsImage(contact)
    }
}

/**
 * TODO: License ...
 */
suspend fun HtmlPage.toMessage(contact: Contact, type: MessageType, notice: String): Message {
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
    }
}

suspend fun Contact.sendMessage(entry: HtmlPage, notice: String) = sendMessage(entry.toMessage(this, reply, notice))

suspend fun ControlRecord.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = user.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with issue by ${user.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("UPDATED_AT: $updatedAt ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
            if (reactions != null) appendLine(reactions!!.render())
            if (body != null && body!!.length < 50) appendLine(body)
        }
        MessageType.XML -> buildXmlMessage(1) {
            actionData = htmlUrl
            templateId = -1
            action = "web"
            brief = notice
            flag = 0

            item {
                layout = 2
                picture(coverUrl = image.queryUrl())
                title(text = title)
                for (label in labels) {
                    summary(text = label.name, color = "#${label.color.uppercase()}")
                }
            }

            source(name = notice)
        }
        MessageType.JSON -> buildStructMessage<StructNew> {
            config.ctime = createdAt.toEpochSecond()
            config.token = "239525e4c0fc9b6849624417086250df"
            desc = "issue"
            uin = contact.id
            detail.appType = 1
            detail.appid = 100951776
            detail.desc = labels.joinToString { it.name }
            detail.jumpUrl = htmlUrl
            detail.preview = image.queryUrl()
            detail.tag = "GitHub"
            detail.title = title
            prompt = "[分享]${title}"
        }
    }
}

suspend fun Release.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = author.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with release by ${author.login} ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("PUBLISHED_AT: $publishedAt ")
            appendLine("URL: $htmlUrl ")
            appendLine("NAME: $name ")
            if (reactions != null) appendLine(reactions.render())
            if (body != null && body.length < 50) appendLine(body)
        }
        MessageType.XML -> buildXmlMessage(1) {
            actionData = htmlUrl
            templateId = -1
            action = "web"
            brief = notice
            flag = 0

            item {
                layout = 2
                picture(coverUrl = image.queryUrl())
                title(text = name)
                for (asset in assets) {
                    summary(text = asset.name)
                }
                summary(text = tagName)
            }

            source(name = notice)
        }
        MessageType.JSON -> TODO()
    }
}

suspend fun Commit.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = author?.avatar()?.uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            if (image != null) appendLine(image)
            appendLine("$notice with commit by ${author?.login ?: detail.author.email} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine(detail.message)
        }
        MessageType.XML -> buildXmlMessage(1) {
            actionData = htmlUrl
            templateId = -1
            action = "web"
            brief = notice
            flag = 0

            item {
                layout = 2
                if (image != null) picture(coverUrl = image.queryUrl())
                title(text = sha)
                summary(text = detail.message)
            }

            source(name = notice)
        }
        MessageType.JSON -> TODO()
    }
}

suspend fun Repo.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = owner.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with repo by ${owner.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("STARGAZERS_COUNT: $stargazersCount")
            appendLine("LANGUAGE: $language ")
            appendLine("DESCRIPTION: $description ")
        }
        MessageType.XML -> buildXmlMessage(1) {
            actionData = htmlUrl
            templateId = -1
            action = "web"
            brief = notice
            flag = 0

            item {
                layout = 2
                picture(coverUrl = image.queryUrl())
                title(text = name)
                for (topic in topics) {
                    summary(text = topic)
                }
            }

            source(name = notice)
        }
        MessageType.JSON -> TODO()
    }
}

suspend fun Milestone.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = creator.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with milestone by ${creator.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("CREATED_AT: $updatedAt ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            appendLine("DESCRIPTION: $description ")
        }
        MessageType.XML -> buildXmlMessage(1) {
            actionData = htmlUrl
            templateId = -1
            action = "web"
            brief = notice
            flag = 0

            item {
                layout = 2
                picture(coverUrl = image.queryUrl())
                title(text = title)
                summary(text = description.orEmpty())
            }

            source(name = notice)
        }
        MessageType.JSON -> TODO()
    }
}

suspend fun Readme.toMessage(contact: Contact): Message {
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