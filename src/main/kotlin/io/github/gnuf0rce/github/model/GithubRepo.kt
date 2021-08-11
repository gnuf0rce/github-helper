package io.github.gnuf0rce.github.model

import io.github.gnuf0rce.github.*

class GithubRepo(val owner: String, val repo: String, val github: GithubClient = GithubClient()) {
    val issue by lazy { IssueMapper("https://api.github.com/repos/${owner}/${repo}/issues", github) }
    val pull by lazy { PullMapper("https://api.github.com/repos/${owner}/${repo}/pulls", github) }
    // val events by lazy {  }
    // val notifications
    // val stargazers
    // val subscribers
    // val subscription
}