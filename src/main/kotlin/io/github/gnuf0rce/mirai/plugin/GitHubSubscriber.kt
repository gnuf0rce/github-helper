package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.exception.*
import io.github.gnuf0rce.mirai.plugin.data.*
import kotlinx.coroutines.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*

abstract class GitHubSubscriber<T>(private val name: String, parent: CoroutineScope) :
    CoroutineScope by parent.childScope(name)
    where T : LifeCycle, T : HtmlPage {

    companion object : Sequence<GitHubSubscriber<*>> {
        val repos: MutableMap<String, GitHubRepo> = HashMap()
        const val PER_PAGE = 30

        val GitHubTask.repo get() = repos.getOrPut(id) { github.repo(id) }
        val current by lazy { github.current() }

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

    fun add(id: String, contact: Long) {
        check(regex?.matches(id) ?: true) { "$id not matches $regex" }
        compute(id) {
            contacts.add(contact)
        }
        jobs.compute(id) { _, old ->
            old?.takeIf { it.isActive } ?: launch(id)
        }
    }

    fun remove(id: String, contact: Long) {
        check(regex?.matches(id) ?: true) { "$id no matches $regex" }
        compute(id) {
            contacts.remove(contact)
        }
    }

    fun interval(id: String, millis: Long) {
        check(regex?.matches(id) ?: true) { "$id no matches $regex" }
        compute(id) {
            interval = millis
        }
    }

    fun list(contact: Long) = buildString {
        val records = synchronized(jobs) { tasks.filter { (_, task) -> contact in task.contacts } }
        appendLine("| name | last | interval |")
        appendLine("|:----:|:----:|:--------:|")
        for ((_, task) in records) {
            appendLine("| ${task.id} | ${task.last} | ${task.interval} |")
        }
    }

    suspend fun build(id: String, contact: Long): Message {
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
                Contact(cid).sendMessage(record, id)
            } catch (e: Throwable) {
                logger.warning("发送信息失败", e)
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

    fun start() {
        for (key in tasks.keys) launch(key)
    }

    fun stop() {
        coroutineContext.cancelChildren()
        jobs.clear()
    }
}