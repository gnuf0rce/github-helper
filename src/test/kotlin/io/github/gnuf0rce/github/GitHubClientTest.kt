package io.github.gnuf0rce.github

import io.ktor.utils.io.errors.*
import org.slf4j.*

internal abstract class GitHubClientTest {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    protected val github = object : GitHubClient(token = System.getenv("GITHUB_TOKEN")) {
        override val maxIgnoreCount: Int = 30
        override val ignore: (Throwable) -> Boolean = { cause ->
            when (cause) {
                is IOException -> {
                    logger.warn("GitHub Client IOException", cause)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}