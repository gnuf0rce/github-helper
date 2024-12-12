/*
 * Copyright 2021-2024 dsstudio Technologies and contributors.
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
public data class ActionsArtifact(
    @SerialName("archive_download_url")
    val archiveDownloadUrl: String,
    @Contextual
    @SerialName("created_at")
    override val createdAt: OffsetDateTime,
    @SerialName("expired")
    val expired: Boolean,
    @Contextual
    @SerialName("expires_at")
    val expiresAt: OffsetDateTime,
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("node_id")
    override val nodeId: String,
    @SerialName("size_in_bytes")
    val size: Long,
    @Contextual
    @SerialName("updated_at")
    override val updatedAt: OffsetDateTime,
    @SerialName("url")
    override val url: String,
    @SerialName("workflow_run")
    val run: WorkflowRun
) : Entry, LifeCycle, WebPage {

    /**
     * @see [expiresAt]
     */
    override val closedAt: OffsetDateTime
        get() = expiresAt

    @Deprecated("Artifact No Merged", ReplaceWith("null"))
    override val mergedAt: OffsetDateTime?
        get() = null

    override val htmlUrl: String
        get() {
            val full = url.substringAfterLast("repos/").substringBefore("/actions")
            return "https://github.com/${full}/actions/runs/${run.id}"
        }

    @Serializable
    public data class WorkflowRun(
        @SerialName("head_branch")
        val headBranch: String,
        @SerialName("head_repository_id")
        val headRepositoryId: Long,
        @SerialName("head_sha")
        val headSha: String,
        @SerialName("id")
        val id: Long,
        @SerialName("repository_id")
        val repositoryId: Long
    )
}