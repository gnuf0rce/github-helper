package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.entry.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*

internal class GitHubRepoTest : GitHubClientTest() {
    private val repo = github.repo(owner = "mamoe", repo = "mirai")

    @Test
    fun get(): Unit = runBlocking {
        val info = repo.load()
        Assertions.assertEquals("mamoe", info.ownerNameOrLogin)
        Assertions.assertEquals("mirai", info.name)
    }

    @Test
    fun issues(): Unit = runBlocking {
        for (issue in repo.issues.list(page = 1) { state = StateFilter.all }) {
            Assertions.assertNotEquals(0, issue.number)
            for (comment in repo.issues.comments(number = issue.number, page = 1)) {
                Assertions.assertFalse(comment.htmlUrl.isEmpty())
                Assertions.assertFalse(comment.body.isEmpty())
            }
            for (event in repo.issues.events(number = issue.number, page = 1)) {
                Assertions.assertFalse(event.url.isEmpty())
            }
        }
    }

    @Test
    fun pulls(): Unit = runBlocking {
        for (pull in repo.pulls.list(page = 1)) {
            Assertions.assertNotEquals(0, pull.number)
            for (comment in repo.issues.comments(number = pull.number, page = 1)) {
                Assertions.assertFalse(comment.htmlUrl.isEmpty())
                Assertions.assertFalse(comment.body.isEmpty())
            }
            for (event in repo.issues.events(number = pull.number, page = 1)) {
                Assertions.assertFalse(event.url.isEmpty())
            }
        }
    }

    @Test
    fun commits(): Unit = runBlocking {
        for (page in 1..10) {
            repo.commits(page = page).forEach { commit ->
                Assertions.assertFalse(commit.sha.isEmpty())
                Assertions.assertFalse(commit.htmlUrl.isEmpty())
                Assertions.assertFalse(commit.parents.isEmpty())
            }
        }
        val commit = repo.commit(sha = "c468570ee157c12120af087995e48074835edce6").get()
        Assertions.assertEquals("c468570ee157c12120af087995e48074835edce6", commit.sha)
        Assertions.assertFalse(commit.htmlUrl.isEmpty())
        Assertions.assertFalse(commit.parents.isEmpty())
    }

    @Test
    fun releases(): Unit = runBlocking {
        val repo = github.repo("cssxsh/pixiv-helper")
        for (page in 1..10) {
            val releases = repo.releases.list(page = page)
            if (releases.isEmpty()) break
            releases.forEach { release ->
                Assertions.assertEquals("cssxsh", release.author?.nameOrLogin)
                release.assets.forEach { asset ->
                    Assertions.assertEquals("cssxsh", asset.uploader?.nameOrLogin)
                }
            }
        }
    }

    @Test
    fun readme(): Unit = runBlocking {
        val readme = repo.readme()
        val markdown = readme.decode()
        Assertions.assertFalse(markdown.isEmpty())
    }
}