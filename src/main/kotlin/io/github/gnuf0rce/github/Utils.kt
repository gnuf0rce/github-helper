/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github

import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
import java.time.*
import java.time.format.*

internal const val IGNORE_UNKNOWN_KEYS = "io.github.gnuf0rce.github.ignore"

internal fun GitHubClient() = GitHubClient(token = System.getenv("GITHUB_TOKEN"))

internal val GitHubJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = System.getProperty(IGNORE_UNKNOWN_KEYS, "false").toBoolean()
    isLenient = true
    allowStructuredMapKeys = true
    serializersModule = SerializersModule {
        contextual(OffsetDateTimeSerializer)
        contextual(ContentTypeSerializer)
        contextual(UrlSerializer)
    }
}

internal val GitHubJsonContentType by lazy {
    ContentType.parse("application/vnd.github.v3.full+json")
}

internal object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.STRING)

    private val formatter get() = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    private val offset get() = OffsetDateTime.now().offset

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.parse(decoder.decodeString(), formatter).withOffsetSameInstant(offset)
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.withOffsetSameInstant(ZoneOffset.UTC).format(formatter))
    }
}

internal object ContentTypeSerializer : KSerializer<ContentType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(ContentType::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ContentType {
        return try {
            ContentType.parse(decoder.decodeString())
        } catch (_: BadContentTypeFormatException) {
            ContentType.Application.OctetStream
        }
    }

    override fun serialize(encoder: Encoder, value: ContentType) {
        encoder.encodeString(value.toString())
    }
}

internal object UrlSerializer : KSerializer<Url> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(Url::class.qualifiedName!!, PrimitiveKind.STRING)

    const val API_HOST = "api.github.com"

    const val DOCS_HOST = "docs.github.com"

    override fun deserialize(decoder: Decoder): Url {
        return Url(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Url) {
        encoder.encodeString(value.toString())
    }
}

internal fun api(vararg components: String): Url {
    return URLBuilder(protocol = URLProtocol.HTTPS, host = UrlSerializer.API_HOST).apply {
        path(*components)
    }.build()
}