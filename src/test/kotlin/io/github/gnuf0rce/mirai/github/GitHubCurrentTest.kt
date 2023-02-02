package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.github.data.*
import kotlinx.coroutines.*
import net.mamoe.mirai.message.data.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.*
import java.time.*

internal class GitHubCurrentTest : GitHubSubscriberTest<Issue>() {
    private val current = github.current()

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun issues(): Unit = runBlocking {
        val now = OffsetDateTime.now()
        for (issue in current.issues(per = 100)) {
            val message = issue.toMessage(group, Format.OLD, "有新 Issue", now)
            Assertions.assertTrue(message is MessageChain)
            val text = issue.toMessage(group, Format.TEXT, "有新 Issue", now)
            Assertions.assertTrue(text is MessageChain)
            val forward = issue.toMessage(group, Format.FORWARD, "有新 Issue", now)
            Assertions.assertTrue(forward is ForwardMessage)
            val graph = issue.toMessage(group, Format.GRAPH, "有新 Issue", now)
            Assertions.assertTrue(graph is Image)
        }
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun user(): Unit = runBlocking {
        val user = current.user()
        val message = user.toMessage(group, Format.OLD)
        Assertions.assertTrue(message is MessageChain)
        val text = user.toMessage(group, Format.TEXT)
        Assertions.assertTrue(text is MessageChain)
        val forward = user.toMessage(group, Format.FORWARD)
        Assertions.assertTrue(forward is ForwardMessage)
        val miss = github.user("hundun000").load()
        miss.avatar(group)
        val ghost = github.user("ghost").load()
        ghost.avatar(group)
    }

    @Test
    fun rate(): Unit = runBlocking {
        val limit = current.rate()
        Assertions.assertFalse(limit.resources.isEmpty())
    }

    override val subscriber = object : GitHubSubscriber<Issue>(name = "current-test") {
        override val tasks: MutableMap<String, GitHubTask> = HashMap()

        override suspend fun GitHubTask.load(per: Int, since: OffsetDateTime) = current.issues(page = 0, per = per)

        override val regex: Regex? = null

        init {
            tasks["current"] = GitHubTask(
                id = "current",
                contacts = hashSetOf(group.id),
                last = OffsetDateTime.now().minusYears(1)
            )
        }
    }
}