/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.exception.*
import io.github.gnuf0rce.mirai.plugin.data.*
import kotlinx.coroutines.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import java.util.*
import kotlin.collections.*

public abstract class GitHubSubscriber<T>(private val name: String, parent: CoroutineScope) :
    CoroutineScope by parent.childScope(name)
    where T : LifeCycle, T : WebPage {

    public companion object : Sequence<GitHubSubscriber<*>> {
        public val repos: MutableMap<String, GitHubRepo> = WeakHashMap()
        public const val PER_PAGE: Int = 30

        public val GitHubTask.repo: GitHubRepo get() = repos.getOrPut(id) { github.repo(id) }
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
        check(regex?.matches(id) ?: true) { "$id not matches $regex" }
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

    public fun list(contact: Long): String = buildString {
        val records = synchronized(jobs) { tasks.filter { (_, task) -> contact in task.contacts } }
        appendLine("| name | last | interval |")
        appendLine("|:----:|:----:|:--------:|")
        for ((_, task) in records) {
            appendLine("| ${task.id} | ${task.last} | ${task.interval} |")
        }
    }

    public suspend fun build(id: String, contact: Long): Message {
        val records = GitHubTask(id).load(PER_PAGE)
        if (records.isEmpty()) return "内容为空".toPlainText()
        return buildForwardMessage(Contact(contact)) {
            for (record in records) {
                add(
                    sender = context.bot,
                    time = record.updatedAt.toEpochSecond().toInt(),
                    message = record.toMessage(context, reply, id)
                )
            }
            displayStrategy = object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String = id
            }
        }
    }

    protected abstract suspend fun GitHubTask.load(per: Int): List<T>

    private suspend fun GitHubTask.sendMessage(record: T) {
        for (cid in contacts) {
            try {
                Contact(cid).sendEntry(record, id)
            } catch (e: Throwable) {
                logger.warning("发送信息失败(${name})", e)
            }
        }
    }

    private fun task(id: String): GitHubTask? = synchronized(jobs) {
        tasks.compute(id) { _, old ->
            old?.takeIf { it.contacts.isNotEmpty() }
        }
    }

    private fun launch(id: String) = launch(SupervisorJob()) {
        logger.info { "$name with $id run start" }
        while (isActive) {
            val current = task(id) ?: break
            try {
                val records = current.load(PER_PAGE).filter { it.updatedAt > current.last }
                for (record in records) {
                    current.sendMessage(record)
                }
                compute(current.id) { last = records.maxOfOrNull { it.updatedAt } ?: current.last }
            } catch (cause: GitHubApiException) {
                logger.warning { "$name with $id api fail, ${cause.json}" }
            } catch (cause: Throwable) {
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