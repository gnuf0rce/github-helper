/*
 * Copyright 2021-2023 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*
import java.time.*

@Serializable
@SerialName("PullRequestReviewComment")
public data class PullRequestReviewComment(
    @SerialName("author_association")
    override val association: Association,
    @SerialName("body")
    override val body: String,
    @SerialName("body_text")
    override val text: String,
    @SerialName("body_html")
    override val html: String,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("html_url")
    override val htmlUrl: String,
    @SerialName("id")
    val id: Long,
    @SerialName("node_id")
    override val nodeId: String,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String,
    @SerialName("user")
    override val owner: Owner?,
    @SerialName("reactions")
    override val reactions: Reactions? = null,
    @SerialName("pull_request_review_id")
    val pullRequestReviewId: Int? = null,
    @SerialName("diff_hunk")
    val diffHunk: String,
    @SerialName("path")
    val path: String,
    @SerialName("position")
    val position: Int?,
    @SerialName("original_position")
    val originalPosition: Int?,
    @SerialName("commit_id")
    val commitId: String,
    @SerialName("original_commit_id")
    val originalCommitId: String,
    @SerialName("in_reply_to_id")
    val inReplyToId: Int? = null,
    @SerialName("pull_request_url")
    val pullRequestUrl: String,
    @SerialName("_links")
    val links: Map<String, Link>,
    @SerialName("start_line")
    val startLine: String?,
    @SerialName("original_start_line")
    val originalStartLine: String?,
    @SerialName("start_side")
    val startSide: Side?,
    @SerialName("line")
    val line: Int?,
    @SerialName("original_line")
    val originalLine: Int?,
    @SerialName("side")
    val side: Side
) : Entry, Comment()