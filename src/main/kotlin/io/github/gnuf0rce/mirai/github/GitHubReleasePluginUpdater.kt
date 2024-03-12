/*
 * Copyright 2021-2023 dsstudio Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/gnuf0rce/github-helper/blob/master/LICENSE
 */


package io.github.gnuf0rce.mirai.github

import io.github.gnuf0rce.github.*
import io.github.gnuf0rce.github.exception.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.*
import java.io.File

public object GitHubReleasePluginUpdater {

    @JvmStatic
    public val dict: MutableMap<String, String> = sortedMapOf(
        "cn.whitrayhb.grasspics" to "NLR-DevTeam/GrassPictures",

        "com.evolvedghost.mirai.steamhelper.steamhelper" to "EvolvedGhost/Steamhelper",
        "com.evolvedghost.mutegames" to "EvolvedGhost/MuteGames",

        "com.happysnaker.HRobot" to "happysnaker/mirai-plugin-HRobot",

        "com.hcyacg.bilibili" to "Nekoer/mirai-plugins-bilibili",
        "com.hcyacg.github-notice" to "Nekoer/mirai-github-notice",
        "com.hcyacg.novelai" to "Nekoer/mirai-plugins-novelai",
        "com.hcyacg.pixiv" to "Nekoer/mirai-plugins-pixiv",

        "com.hrs.kloping.AutoReply" to "Kloping/Mirai_Plugins_Auto_Reply",

        "com.kasukusakura.mlss" to "KasukuSakura/mirai-login-solver-sakura",

        "com.khjxiaogu.mirai.MiraiSongPlugin" to "khjxiaogu/MiraiSongPlugin",

        "com.xtex.repeater" to "xtexChooser/mirai-repeater",

        "indi.eiriksgata.rulateday-dice" to "Eiriksgata/mirai-rulateday-dice",

        "io.github.samarium150.mirai.plugin.mirai-console-drift-bottle" to "Samarium150/mirai-console-drift-bottle",
        "io.github.samarium150.mirai.plugin.mirai-console-loafers-calendar" to "Samarium150/mirai-console-loafers-calendar",
        "io.github.samarium150.mirai.plugin.mirai-console-lolicon" to "Samarium150/mirai-console-lolicon",

        "me.jie65535.mirai-console-jnr-plugin" to "jie65535/mirai-console-jnr-plugin",

        "me.sagiri.mirai.plugin.QShell" to "EroSagiri/QShell",

//        "me.stageguard.obms.OsuMapSuggester" to "StageGuard/OsuMapSuggester",
        "me.stageguard.sctimetable" to "StageGuard/SuperCourseTimetableBot",

        "org.laolittle.plugin.draw.DrawMeme" to "LaoLittle/DrawMeme",
        "org.laolittle.plugin.SkikoMirai" to "LaoLittle/SkikoMirai",

        "top.colter.bilibili-dynamic-mirai-plugin" to "Colter23/bilibili-dynamic-mirai-plugin",
        "top.colter.genshin-sign" to "Colter23/genshin-sign-mirai-plugin",

        "top.cutestar.antiRecall" to "Pmaru-top/AntiRecall",

        "top.jie65535.j24" to "jie65535/mirai-console-j24-plugin",
        "top.jie65535.jcf" to "jie65535/mirai-console-jcf-plugin",
        "top.jie65535.mail-notify" to "jie65535/JMailNotify",
        "top.jie65535.mirai-console-jcc-plugin" to "jie65535/mirai-console-jcc-plugin",
        "top.jie65535.mirai-console-jcr-plugin" to "jie65535/mirai-console-jcr-plugin",
        "top.jie65535.mirai-console-jms-plugin" to "jie65535/mirai-console-jms-plugin",
        "top.jie65535.mirai.grasscutter-command" to "jie65535/JGrasscutterCommand",

        "top.limbang.mcmod" to "limbang/mirai-console-mcmod-plugin",
        "top.limbang.mcsm" to "limbang/mirai-console-mcsm-plugin",
        "top.limbang.minecraft" to "limbang/mirai-console-minecraft-plugin",

        "top.mrxiaom.qrlogin" to "MrXiaoM/mirai-console-dev-qrlogin",

        "xmmt.dituon.petpet" to "Dituon/petpet",

        "xyz.cssxsh.mirai.fix-protocol-version" to "cssxsh/fix-protocol-version",
        "xyz.cssxsh.mirai.plugin.novelai-helper" to "cssxsh/novelai-helper",
        "xyz.cssxsh.mirai.plugin.pixiv-helper" to "cssxsh/pixiv-helper"
    )

    public fun reload(file: File) {
        if (file.exists()) {
            dict.clear()
            dict.putAll(GitHubJson.decodeFromString(file.readText()))
        } else {
            file.writeText(GitHubJson.encodeToString(dict))
        }
    }

    public fun update() {
        for (plugin in PluginManager.plugins) {
            if (plugin !is JvmPlugin) continue
            val id = dict[plugin.description.id]
                ?: dict[plugin.description.name]
                ?: continue
            val classLoader = plugin::class.java.classLoader as? java.net.URLClassLoader ?: continue
            val source = classLoader
                .urLs.singleOrNull()
                ?.let { File(it.path) }
                ?: continue
            var needUpdate = false
            val download = PluginManager.pluginsFolder.resolve("${plugin.description.id}.download")
            val backup = PluginManager.pluginsFolder.resolve("${source.name}.bak")

            plugin.launch(CoroutineName("update from github")) {
                val latest = try {
                    github.repo(id).releases.latest()
                } catch (exception: GitHubApiException) {
                    if (exception.cause.response.status == HttpStatusCode.NotFound) {
                        plugin.logger.warning("项目未设置 Release Latest!")
                    } else {
                        plugin.logger.warning("GitHub API 异常", exception)
                    }
                    return@launch
                }
                plugin.logger.info("github latest version ${latest.tagName}")

                val jar = latest.assets.find { it.name.endsWith(".mirai2.jar") }
                    ?: latest.assets.find { it.name.endsWith(".mirai.jar") }
                    ?: latest.assets.find { it.name.endsWith(".jar") }
                    ?: kotlin.run {
                        plugin.logger.warning("没有在 ${latest.htmlUrl} 中找到插件文件")
                        return@launch
                    }
                val updated = jar.updatedAt.toInstant().toEpochMilli()
                needUpdate = try {
                    SemVersion(latest.tagName.removePrefix("v")) > plugin.description.version
                } catch (_: IllegalArgumentException) {
                    updated > source.lastModified()
                }

                if (needUpdate.not()) return@launch

                plugin.logger.info(buildString {
                    append("github latest release note:").append('\n')
                    append(latest.htmlUrl).append('\n')
                    append(latest.body ?: "<empty>")
                })

                plugin.logger.info("尝试从 v${plugin.description.version} 升级到 ${latest.tagName}")
                runInterruptible(Dispatchers.IO) {
                    download.createNewFile()
                }
                github.useHttpClient { http ->
                    http.get(jar.browserDownloadUrl)
                        .bodyAsChannel()
                        .copyAndClose(download.writeChannel())
                    delay(1_000)
                }
                check(download.length() == jar.size) {
                    "从 ${jar.browserDownloadUrl} 下载失败(文件大小校验失败 ${download.length()}!=${jar.size})"
                }
                val target = PluginManager.pluginsFolder.resolve(jar.name)
                check(download.renameTo(target)) {
                    "重命名到 ${jar.name} 失败"
                }
                target.setLastModified(updated)
                plugin.logger.info("从 ${latest.htmlUrl} 升级成功")

            }.invokeOnCompletion { cause ->
                if (cause != null) {
                    plugin.logger.warning("从 $id 升级失败")
                    download.deleteOnExit()
                } else if (needUpdate) {
                    plugin.logger.info("旧版插件 ${source.name} 将尝试添加退出时重命名(备份)，请在下次启动时手动检查")
                    Runtime.getRuntime().addShutdownHook(Thread {
                        classLoader.close()
                        if (source.renameTo(backup).not()) {
                            System.err.println("重命名失败，${source.toPath().toUri()}")
                        }
                    })
                }
            }
        }
    }
}