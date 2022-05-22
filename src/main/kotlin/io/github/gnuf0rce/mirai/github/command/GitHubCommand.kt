/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github.command

import net.mamoe.mirai.console.command.*

public sealed interface GitHubCommand : Command {

    public companion object : Collection<GitHubCommand> {
        private val commands by lazy {
            GitHubCommand::class.sealedSubclasses.mapNotNull { kClass -> kClass.objectInstance }
        }

        override val size: Int get() = commands.size

        override fun contains(element: GitHubCommand): Boolean = commands.contains(element)

        override fun containsAll(elements: Collection<GitHubCommand>): Boolean = commands.containsAll(elements)

        override fun isEmpty(): Boolean = commands.isEmpty()

        override fun iterator(): Iterator<GitHubCommand> = commands.iterator()

        public operator fun get(name: String): GitHubCommand = commands.first { it.primaryName.equals(name, true) }
    }
}