package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*

internal class GitHubOwnerTest : GitHubClientTest() {

    init {
        System.setProperty(IMAGE_FOLDER_PROPERTY, "./run/image")
        System.setProperty(CACHE_FOLDER_PROPERTY, "./run/cache")
    }

    @Test
    fun user(): Unit = runBlocking {
        val user = github.user(login = "cssxsh").load()
        Assertions.assertEquals("cssxsh", user.login)
        val page = github.user(login = "cssxsh").repos(page = 1)
        Assertions.assertEquals(user.login, page.firstOrNull()?.owner?.login)
        Assertions.assertEquals(30, page.size)
    }


    @Test
    fun organization(): Unit = runBlocking {
        val organization = github.organization(login = "iTXTech").load()
        Assertions.assertEquals("iTXTech", organization.login)
        val page = github.user(login = "iTXTech").repos(page = 1)
        Assertions.assertEquals(organization.login, page.firstOrNull()?.owner?.login)
        Assertions.assertEquals(30, page.size)
    }
}