package io.github.gnuf0rce.mirai.plugin.command

import net.mamoe.mirai.console.command.*

sealed interface GitHubCommand : Command {

    companion object : Collection<GitHubCommand> {
        private val commands by lazy {
            GitHubCommand::class.sealedSubclasses.mapNotNull { kClass -> kClass.objectInstance }
        }

        override val size: Int get() = commands.size

        override fun contains(element: GitHubCommand): Boolean = commands.contains(element)

        override fun containsAll(elements: Collection<GitHubCommand>): Boolean = commands.containsAll(elements)

        override fun isEmpty(): Boolean = commands.isEmpty()

        override fun iterator(): Iterator<GitHubCommand> = commands.iterator()

        operator fun get(name: String): GitHubCommand = commands.first { it.primaryName.equals(name, true) }
    }
}