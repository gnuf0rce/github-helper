/*
 * Copyright 2021-2022 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.github.entry

import io.ktor.util.*
import kotlinx.serialization.*

@Serializable
@SerialName("Readme")
public data class Readme(
    /**
     * encoded
     * @see encoding
     * @see decode
     */
    @SerialName("content")
    val content: String,
    @SerialName("download_url")
    val downloadUrl: String = "",
    @SerialName("encoding")
    val encoding: String,
    @SerialName("git_url")
    val gitUrl: String?,
    @SerialName("html_url")
    override val htmlUrl: String = "",
    @SerialName("_links")
    val links: Map<String, String?>,
    @SerialName("name")
    val name: String,
    @SerialName("path")
    val path: String,
    @SerialName("sha")
    override val sha: String,
    @SerialName("size")
    val size: Int,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    override val url: String,
    @SerialName("target")
    val target: String = "",
    @SerialName("submodule_git_url")
    val submoduleGitUrl: String = ""
) : WebPage, Record {

    internal fun decode(): String {
        return when (encoding) {
            "base64" -> content.lineSequence().joinToString("") { it.decodeBase64String() }
            else -> throw UnsupportedOperationException("decode $encoding")
        }
    }
}