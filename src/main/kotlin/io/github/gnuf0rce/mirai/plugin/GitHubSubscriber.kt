package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.github.exception.*
import io.github.gnuf0rce.mirai.plugin.data.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*

@OptIn(ConsoleExperimentalApi::class)
abstract class GitHubSubscriber<T : LifeCycle>(private val name: String, scope: CoroutineScope = MainScope()) :
    CoroutineScope by scope.childScope(name) {
    companion object {
        val reply by GitHubConfig::reply
        val repos = mutableMapOf<String, GitHubRepo>().withDefault { full -> github.repo(full) }
        const val PER_PAGE = 30

        val GitHubTask.repo get() = repos.getValue(id)
        val current by lazy { GitHubCurrent(github) }

        private val all = mutableListOf<GitHubSubscriber<*>>()

        fun start() {
            for (subscriber in all) {
                subscriber.start()
            }
        }

        fun stop() {
            for (subscriber in all) {
                subscriber.start()
            }
        }
    }

    init {
        let(all::add)
    }

    private val jobs = mutableMapOf<String, Job>()

    protected abstract val tasks: MutableMap<String, GitHubTask>

    private fun compute(id: String, block: GitHubTask.() -> Unit): Unit = synchronized(jobs) {
        tasks.compute(id) { _, old ->
            (old ?: GitHubTask(id)).apply(block)
        }
    }

    protected open val regex: Regex? = REPO_REGEX

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
                val contact = Contact(cid)
                contact.sendMessage(record.toMessage(contact, reply, id))
            } catch (e: Throwable) {
                logger.warning("发送信息失败", e)
            }
        }
    }

    private fun task(id: String) = synchronized(jobs) { tasks[id] }?.takeIf { it.contacts.isNotEmpty() }

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
    }

    fun start() {
        for (key in tasks.keys) launch(key)
    }

    fun stop() {
        coroutineContext.cancelChildren()
        jobs.clear()
    }
}