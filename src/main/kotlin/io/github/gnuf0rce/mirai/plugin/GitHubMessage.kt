@file:OptIn(MiraiExperimentalApi::class, ExperimentalSerializationApi::class, ConsoleExperimentalApi::class)

package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.plugin.data.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.*
import java.io.File
import java.time.*

internal fun Contact(id: Long): Contact = Bot.instances.firstNotNullOf { it.getContactOrNull(id) }

@Serializable
enum class MessageType { TEXT, XML, JSON }

internal suspend fun UserInfo.avatar(flush: Boolean = false, client: GitHubClient = github): File {
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

private const val STATS_API = "https://github-readme-stats.vercel.app/api"

private val RANK_REGEX = """(?<=rank-text[^>]{0,1024}>[^>]{0,1024}>[^\w]{0,1024})[\w+-]+""".toRegex()

private val STARS_REGEX = """(?<=stars[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

private val COMMITS_REGEX = """(?<=commits[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

private val PRS_REGEX = """(?<=prs[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

private val ISSUES_REGEX = """(?<=issues[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

private val CONTRIBS_REGEX = """(?<=contribs[^>]{0,1024}>[^\w]{0,1024})[^<\s]+""".toRegex()

/**
 * XXX: svg to text
 */
@Suppress("BlockingMethodInNonBlockingContext")
internal suspend fun UserInfo.stats(flush: Boolean = false, client: GitHubClient = github): Message {
    val svg = ImageFolder.resolve("stats").resolve("${login}.svg").apply {
        if (exists().not() || flush) {
            parentFile.mkdirs()
            writeBytes(client.useHttpClient { client ->
                client.get(STATS_API) {
                    parameter("username", login)
                    for ((key, value) in GitHubConfig.stats) parameter(key, value)
                }
            })
        }
    }

    val xml = svg.readText()
    val year = Year.now()

    return buildMessageChain {
        appendLine("${login}'s GitHub Stats")
        appendLine("Rank: ${RANK_REGEX.find(xml)?.value}")
        appendLine("Total Stars Earned:   ${STARS_REGEX.find(xml)?.value}")
        appendLine("Total Commits (${year}): ${COMMITS_REGEX.find(xml)?.value}")
        appendLine("Total PRs:            ${PRS_REGEX.find(xml)?.value}")
        appendLine("Total Issues:         ${ISSUES_REGEX.find(xml)?.value}")
        appendLine("Contributed to:       ${CONTRIBS_REGEX.find(xml)?.value}")
    }
}

internal fun LightApp(block: JsonObjectBuilder.() -> Unit) = LightApp(GitHubJson.encodeToString(buildJsonObject(block)))

internal fun MessageChainBuilder.appendLine(image: Image) = append(image).appendLine()

suspend fun LifeCycle.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    return when (this) {
        is Issue -> toMessage(contact, type, notice)
        is Pull -> toMessage(contact, type, notice)
        is Release -> toMessage(contact, type, notice)
        is Release.Asset -> throw IllegalStateException("不该出现的执行")
        is Commit -> toMessage(contact, type, notice)
        is Repo -> toMessage(contact, type, notice)
    }
}

suspend fun Contact.sendMessage(entry: LifeCycle, notice: String) = sendMessage(entry.toMessage(this, reply, notice))

suspend fun Issue.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = user.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with issue by ${user.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
            appendLine("CREATED_AT: ${createdAt.withOffsetSameInstant(offset)} ")
            appendLine("UPDATED_AT: ${updatedAt.withOffsetSameInstant(offset)} ")
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
        MessageType.JSON -> LightApp {
            TODO()
        }
    }
}

suspend fun Pull.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = user.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with pull by ${user.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("TITLE: $title ")
            appendLine("STATE: $state ")
            if (labels.isNotEmpty()) appendLine("LABELS: ${labels.joinToString { it.name }} ")
            appendLine("CREATED_AT: ${createdAt.withOffsetSameInstant(offset)} ")
            appendLine("UPDATED_AT: ${updatedAt.withOffsetSameInstant(offset)} ")
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
        MessageType.JSON -> LightApp {
            TODO()
        }
    }
}

suspend fun Release.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = author.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with release by ${author.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("NAME: $name ")
            appendLine("CREATED_AT: $createdAt ")
            appendLine("PUBLISHED_AT: $publishedAt ")
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
                summary(text = tagName)
            }

            source(name = notice)
        }
        MessageType.JSON -> LightApp {
            TODO()
        }
    }
}

suspend fun Commit.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = author.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with commit by ${author.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("MESSAGE: ${detail.message} ")
            appendLine("CREATED_AT: $createdAt ")
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
                title(text = sha)
                summary(text = detail.message)
            }

            source(name = notice)
        }
        MessageType.JSON -> LightApp {
            TODO()
        }
    }
}

suspend fun Repo.toMessage(contact: Contact, type: MessageType, notice: String): Message {
    val image = owner.avatar().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            appendLine(image)
            appendLine("$notice with repo by ${owner.login} ")
            appendLine("URL: $htmlUrl ")
            appendLine("LANGUAGE: $language ")
            appendLine("CREATED_AT: $createdAt ")
        }
        MessageType.XML -> TODO()
        MessageType.JSON -> TODO()
    }
}