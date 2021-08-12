package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.github.GitHubRepo
import io.github.gnuf0rce.github.entry.*
import io.github.gnuf0rce.mirai.plugin.data.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope

@OptIn(ConsoleExperimentalApi::class)
abstract class GitHubSubscriber<T : LifeCycle> : CoroutineScope by GitHubHelperPlugin.childScope() {
    companion object {
        val reply by GitHubConfig::reply
        val repos = mutableMapOf<String, GitHubRepo>().withDefault {
            val (owner, repo) = it.split('/', '-')
            GitHubRepo(owner, repo, github)
        }
        const val PER_PAGE = 30

        val GitHubTask.repo get() = repos.getValue(id)
    }

    private val jobs = mutableMapOf<String, Job>()

    protected abstract val tasks: MutableMap<String, GitHubTask>

    private fun compute(id: String, block: GitHubTask.() -> Unit): Unit = synchronized(jobs) {
        tasks.compute(id) { _, old ->
            (old ?: GitHubTask(id)).apply(block)
        }
    }

    private val reply get() = GitHubConfig.reply

    fun start() {

    }
    fun stop() {
        coroutineContext.cancelChildren()
    }
}