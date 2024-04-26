package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import kotlinx.coroutines.*
import net.mamoe.mirai.mock.*
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal abstract class GitHubSubscriberTest<T> : GitHubClientTest()
    where T : LifeCycle, T : WebPage {

    init {
        System.setProperty(IMAGE_FOLDER_PROPERTY, "./run/image")
        System.setProperty(CACHE_FOLDER_PROPERTY, "./run/cache")
    }

    protected val bot = MockBotFactory.newMockBotBuilder().create()

    protected val group = bot.addGroup(114514, "mock")

    protected val sender = group.addMember(1919810, "...")

    protected abstract val subscriber: GitHubSubscriber<T>

    @Test
    open fun subscribe(): Unit = runBlocking {
        subscriber.start()
        delay(15 * 1000L)
    }

    @AfterAll
    fun stop() {
        subscriber.stop()
    }
}