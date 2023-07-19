package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.exception.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import org.junit.jupiter.api.*
import java.io.File
import java.util.TreeMap

internal class GitHubReleasePluginUpdaterTest : GitHubClientTest() {

    @Test
    fun dict(): Unit = runBlocking {
        for ((id, repo) in TreeMap(GitHubReleasePluginUpdater.dict)) {
            val folder = File("run/update/$id")
            folder.mkdirs()
            val latest = try {
                github.repo(repo).releases.latest()
            } catch (_: GitHubApiException) {
                continue
            }
            folder.resolve("latest.json").writeText(GitHubJson.encodeToString(latest))
            val jar = latest.assets.find { it.name.endsWith(".mirai2.jar") }
                ?: latest.assets.find { it.name.endsWith(".mirai.jar") }
                ?: latest.assets.find { it.name.endsWith(".jar") }
                ?: continue
            val target = folder.resolve(jar.name)

            if (target.exists()) continue

            github.useHttpClient { http ->
                http.get(jar.browserDownloadUrl)
                    .bodyAsChannel()
                    .copyAndClose(target.writeChannel())
            }
        }
    }
}