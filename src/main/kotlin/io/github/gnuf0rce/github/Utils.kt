package io.github.gnuf0rce.github

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
import java.time.*
import java.time.format.*

const val IGNORE_UNKNOWN_KEYS = "io.github.gnuf0rce.github.ignore"

internal fun GitHubClient() = GitHubClient(token = System.getenv("GITHUB_TOKEN"))

internal val GitHubJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = System.getProperty(IGNORE_UNKNOWN_KEYS, "false").toBoolean()
    isLenient = true
    allowStructuredMapKeys = true
    serializersModule = SerializersModule {
        include(serializersModule)
        contextual(OffsetDateTimeSerializer)
        contextual(ContentTypeSerializer)
    }
}

@Suppress("unused")
internal val ContentType.Application.GitHubJson
    get() = ContentType.parse("application/vnd.github.v3+json")

@OptIn(ExperimentalSerializationApi::class)
@Serializer(OffsetDateTime::class)
object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
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

@OptIn(ExperimentalSerializationApi::class)
@Serializer(ContentType::class)
object ContentTypeSerializer : KSerializer<ContentType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(ContentType::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ContentType {
        return ContentType.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ContentType) {
        encoder.encodeString(value.toString())
    }
}

@Suppress("FunctionName")
fun HttpClientConfig<*>.RateLimit(block: RateLimitFeature.Config.() -> Unit) {
    install(RateLimitFeature, block)
}

internal val REPO_REGEX = """([\w-]+)/([\w-]+)""".toRegex()

fun GitHubClient.repo(owner: String, repo: String) = GitHubRepo(owner = owner, repo = repo, github = this)

fun GitHubClient.repo(full: String): GitHubRepo {
    val (owner, repo) = requireNotNull(REPO_REGEX.find(full)) { "Not Found FullName." }.destructured
    return GitHubRepo(owner = owner, repo = repo, github = this)
}

fun GitHubClient.user(name: String) = GitHubUser(user = name, github = this)

fun GitHubClient.current() = GitHubCurrent(github = this)
