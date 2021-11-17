package io.github.gnuf0rce.mirai.plugin

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.message.data.*
import kotlin.reflect.full.*


@OptIn(ExperimentalSerializationApi::class)
internal inline fun LightApp(json: Json = Json, block: JsonObjectBuilder.() -> Unit): LightApp {
    return LightApp(json.encodeToString(buildJsonObject(block)))
}

sealed class StructMessageBuilder {
    protected open val block: JsonObjectBuilder.() -> Unit = {
        put("app", "com.tencent.structmsg")
    }

    fun build(json: Json = Json.Default) = LightApp(json, block)
}

data class StructNew(
    val config: Config = Config(),
    var uin: Long = 0,
    val detail: Detail = Detail(),
    var desc: String = "新闻",
    var prompt: String = "[分享]"
) : StructMessageBuilder() {

    data class Config(
        var autosize: Boolean = true,
        var ctime: Long = System.currentTimeMillis() / 1_000,
        var forward: Boolean = true,
        var token: String = "",
        var type: String = "normal"
    )

    data class Detail(
        var action: String = "",
        var androidPkgName: String = "",
        var appType: Int = 0,
        var appid: Int = 0,
        var desc: String = "",
        var jumpUrl: String = "",
        var preview: String = "",
        var sourceIcon: String = "",
        var sourceUrl: String = "",
        var tag: String = "",
        var title: String = "",
    )

    override val block: JsonObjectBuilder.() -> Unit
        get() = {
            apply(super.block)

            check(config.token.isEmpty()) { "Token Not Empty" }
            putJsonObject("config") {
                put("autosize", config.autosize)
                put("ctime", config.ctime)
                put("forward", config.forward)
                put("token", config.token)
                put("type", config.type)
            }
            put("desc", desc)

            check(detail.appType != 0) { "App Type Not Empty" }
            check(detail.appid != 0) { "App Id Not Zero" }
            check(uin != 0L) { "UIN Not Zero" }
            putJsonObject("extra") {
                put("app_type", detail.appType)
                put("appid", detail.appid)
                put("uin", uin)
            }

            putJsonObject("meta") {
                putJsonObject("news") {
                    put("action", detail.action)
                    put("android_pkg_name", detail.androidPkgName)
                    put("app_type", detail.appType)
                    put("appid", detail.appid)
                    put("desc", detail.desc)
                    put("jumpUrl", detail.jumpUrl)
                    put("preview", detail.preview)
                    put("source_icon", detail.sourceIcon)
                    put("source_url", detail.sourceUrl)
                    put("tag", detail.tag)//...
                    put("title", detail.title)
                }
            }

            put("prompt", prompt)
            put("ver", "0.0.0.1")
            put("view", "news")
        }
}

inline fun <reified T : StructMessageBuilder> buildStructMessage(json: Json = Json, block: T.() -> Unit): LightApp {
    return T::class.createInstance().apply(block).build(json)
}