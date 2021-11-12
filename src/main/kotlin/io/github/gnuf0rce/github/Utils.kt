package io.github.gnuf0rce.github

import io.ktor.client.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
import java.time.*
import java.time.format.*

internal fun GitHubClient() = GitHubClient(token = System.getenv("GITHUB_TOKEN"))

internal val GitHubJson = Json {
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

@Suppress("FunctionName")
fun HttpClientConfig<*>.RateLimit(block: RateLimitFeature.Config.() -> Unit) {
    install(RateLimitFeature, block)
}

internal val REPO_REGEX = """([0-9A-z_-]+)/([0-9A-z_-]+)""".toRegex()

fun GitHubClient.repo(owner: String, repo: String) = GitHubRepo(owner = owner, repo = repo, github = this)

fun GitHubClient.repo(full: String): GitHubRepo {
    val (owner, repo) = REPO_REGEX.find(full)!!.destructured
    return GitHubRepo(owner = owner, repo = repo, github = this)
}

fun GitHubClient.current() = GitHubCurrent(github = this)
