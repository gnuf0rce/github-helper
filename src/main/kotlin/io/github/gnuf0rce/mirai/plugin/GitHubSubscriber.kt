package io.github.gnuf0rce.mirai.plugin

import io.github.gnuf0rce.mirai.plugin.data.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope

object GitHubSubscriber: CoroutineScope by GitHubHelperPlugin.childScope("XXX") {

    private val reply get() = GitHubConfig.reply

    fun start() {

    }
    fun stop() {
        coroutineContext.cancelChildren()
    }
}