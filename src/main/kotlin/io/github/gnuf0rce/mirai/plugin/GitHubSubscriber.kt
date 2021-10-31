package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.plugin.data.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.utils.*

@OptIn(ConsoleExperimentalApi::class)
abstract class GitHubSubscriber<T : LifeCycle>(private val name: String, scope: CoroutineScope = MainScope()) :
    CoroutineScope by scope.childScope(name) {
    companion object {
        val reply by GitHubConfig::reply
        val repos = mutableMapOf<String, GitHubRepo>().withDefault { id ->
            val (owner, repo) = id.split('/', '-')
            GitHubRepo(owner, repo, github)
        }
        const val PER_PAGE = 30
        val REPO_REGEX = """[0-9A-z_-]+/[0-9A-z_-]+""".toRegex()

        val GitHubTask.repo get() = repos.getValue(id)
        val current by lazy { GitHubCurrent(github) }
    }

    private val jobs = mutableMapOf<String, Job>()

    protected abstract val tasks: MutableMap<String, GitHubTask>

    private fun compute(id: String, block: GitHubTask.() -> Unit): Unit = synchronized(jobs) {
        tasks.compute(id) { _, old ->
            (old ?: GitHubTask(id)).apply(block)
        }
    }

    protected abstract val regex: Regex?

    fun add(id: String, contact: Long) {
        check(regex?.matches(id) ?: true) { "$id not matches $regex" }
        compute(id) {
            contacts.add(contact)
        }
        jobs.compute(id) { _, old ->
            old?.takeIf { it.isActive } ?: run(id)
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

    private fun run(id: String) = launch(SupervisorJob()) {
        logger.info { "$name with $id run start" }
        while (isActive) {
            val current = task(id) ?: break
            try {
                val records = current.load(PER_PAGE).filter { it.updatedAt > current.last }
                for (record in records) {
                    current.sendMessage(record)
                }
                compute(current.id) { last = records.maxOfOrNull { it.updatedAt } ?: current.last }
            } catch (e: Throwable) {
                logger.warning { "$name with $id run fail $e" }
            }
            delay(current.interval)
        }
    }

    fun start() {
        for (key in tasks.keys) run(key)
    }

    fun stop() {
        coroutineContext.cancelChildren()
        jobs.clear()
    }
}