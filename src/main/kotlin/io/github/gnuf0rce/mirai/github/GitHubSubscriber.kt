/*
 * Copyright 2021-2024 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.exception.*
import io.github.gnuf0rce.mirai.github.data.*
import kotlinx.coroutines.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import java.time.*
import kotlin.collections.*
import kotlin.coroutines.*

public abstract class GitHubSubscriber<T>(private val name: String) : CoroutineScope
    where T : LifeCycle, T : WebPage {

    override val coroutineContext: CoroutineContext =
        CoroutineName(name = name) + SupervisorJob() + CoroutineExceptionHandler { context, throwable ->
            logger.warning({ "$throwable in $context" }, throwable)
        }

    public companion object : Sequence<GitHubSubscriber<*>> {
        public const val PER_PAGE: Int = 30

        public val GitHubTask.repo: GitHubRepo get() = repo(id)
        public val current: GitHubCurrent by lazy { github.current() }

        private val instances: MutableList<GitHubSubscriber<*>> = ArrayList()

        override fun iterator(): Iterator<GitHubSubscriber<*>> = instances.iterator()
    }

    init {
        let(instances::add)
    }

    private val jobs: MutableMap<String, Job> = HashMap()

    protected abstract val tasks: MutableMap<String, GitHubTask>

    private fun compute(id: String, block: GitHubTask.() -> Unit): Unit = synchronized(jobs) {
        tasks.compute(id) { _, old ->
            (old ?: GitHubTask(id)).apply(block)
        }
    }

    protected open val regex: Regex? = FULL_REGEX

    public fun add(id: String, contact: Long) {
        check(regex?.matches(id) ?: true) { "$id no matches $regex" }
        compute(id) {
            contacts.add(contact)
        }
        jobs.compute(id) { _, old ->
            old?.takeIf { it.isActive } ?: launch(id)
        }
    }

    public fun remove(id: String, contact: Long) {
        check(regex?.matches(id) ?: true) { "$id no matches $regex" }
        compute(id) {
            contacts.remove(contact)
        }
    }

    public fun interval(id: String, millis: Long) {
        check(regex?.matches(id) ?: true) { "$id no matches $regex" }
        compute(id) {
            interval = millis
        }
    }

    public fun format(id: String, type: Format) {
        check(regex?.matches(id) ?: true) { "$id no matches $regex" }
        compute(id) {
            format = type
        }
    }

    public fun list(contact: Long): String = buildString {
        val records = synchronized(jobs) { tasks.filter { (_, task) -> contact in task.contacts } }
        appendLine("| name | last | interval | format |")
        appendLine("|:----:|:----:|:--------:|:------:|")
        for ((_, task) in records) {
            appendLine("| ${task.id} | ${task.last} | ${task.interval} | ${task.format} |")
        }
    }

    public suspend fun test(id: String, contact: Long, format: Format): Message {
        val since = OffsetDateTime.now().minusMinutes(10)
        val records = GitHubTask(id).load(per = PER_PAGE, since = since)
        if (records.isEmpty()) return "内容为空".toPlainText()
        return buildForwardMessage(Contact(contact)) {
            for (record in records) {
                add(
                    sender = context.bot,
                    time = record.updatedAt.toEpochSecond().toInt(),
                    message = record.toMessage(context, format, id, since)
                )
            }
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = id
            }
        }
    }

    protected abstract suspend fun GitHubTask.load(per: Int, since: OffsetDateTime): List<T>

    private suspend fun GitHubTask.sendMessage(record: T) {
        for (cid in contacts) {
            try {
                val contact = Contact(cid)
                val message = record.toMessage(contact = contact, notice = id, format = format, since = last)
                contact.sendMessage(message = message)
            } catch (cause: Exception) {
                logger.warning("发送信息失败(${name})", cause)
            }
        }
    }

    private suspend fun GitHubTask.sendMessage(records: List<T>) {
        for (cid in contacts) {
            try {
                val contact = Contact(cid)
                val message = buildForwardMessage(context = contact) {
                    for (record in records) {
                        val message = record.toMessage(contact = contact, notice = id, format = format, since = last)
                        contact.bot says message
                    }
                }
                contact.sendMessage(message = message)
            } catch (cause: Exception) {
                logger.warning("发送信息失败(${name})", cause)
            }
        }
    }

    private fun task(id: String): GitHubTask? = synchronized(jobs) {
        tasks.compute(id) { _, old ->
            old?.takeIf { it.contacts.isNotEmpty() }
        }
    }

    private fun launch(id: String) = launch {
        logger.info { "$name with $id run start" }
        while (isActive) {
            val current = task(id) ?: break
            try {
                val records = current.load(per = PER_PAGE, since = current.last)
                when (records.size) {
                    0 -> Unit
                    1 -> current.sendMessage(record = records.single())
                    else -> current.sendMessage(records = records)
                }
                compute(current.id) { last = records.maxOfOrNull { it.updatedAt } ?: current.last }
            } catch (_: CancellationException) {
                // ignored
            } catch (cause: GitHubApiException) {
                logger.warning { "$name with $id api fail, ${cause.json}" }
            } catch (cause: Exception) {
                logger.warning({ "$name with $id run fail" }, cause)
            }
            delay(current.interval)
        }
        logger.info { "$name with $id run stop" }
    }

    public fun start() {
        for (key in tasks.keys) launch(key)
    }

    public fun stop() {
        coroutineContext.cancelChildren()
        jobs.clear()
    }
}