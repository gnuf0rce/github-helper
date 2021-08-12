package io.github.gnuf0rce.github

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
import java.time.*
import java.time.format.*

internal val GithubJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
    serializersModule = SerializersModule {
        include(serializersModule)
        contextual(OffsetDateTime::class, OffsetDateTimeSerializer)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(OffsetDateTime::class)
object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.STRING)

    private val formatter get() = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.parse(decoder.decodeString(), formatter)
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.format(formatter))
    }
}
