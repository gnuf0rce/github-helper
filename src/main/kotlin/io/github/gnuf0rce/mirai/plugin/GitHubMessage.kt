package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.File

@Serializable
enum class MessageType { TEXT, XML, JSON }

private suspend fun UserInfo.image(): File = Url(avatarUrl).let { url ->
    ImageFolder.resolve("avatar").resolve(url.filename).apply {
        if (exists().not()) {
            parentFile.mkdirs()
            writeBytes(github.useHttpClient { it.get(url) })
        }
    }
}

suspend fun Issue.toMessage(contact: Contact, type: MessageType): Message {
    val image = user.image().uploadAsImage(contact)
    return when (type) {
        MessageType.TEXT -> buildMessageChain {
            add(image)
            // TODO
        }
        MessageType.XML -> buildXmlMessage(1) {
            // TODO
        }
        MessageType.JSON -> LightApp(GithubJson.encodeToString(buildJsonObject {
            // TODO
        }))
    }
}