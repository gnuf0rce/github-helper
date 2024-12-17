package io.github.gnuf0rce.github

import io.ktor.http.*
import kotlin.reflect.jvm.*

internal fun Headers.fixContentType() {
    val type = when (get(HttpHeaders.ContentType)) {
        "zip" -> ContentType.Application.Zip
        else -> return
    }
    when (this::class.java.packageName) {
        """io.ktor.client.engine.okhttp""" -> {
            val impl = this::class.java.getDeclaredField("\$this_fromOkHttp")
            impl.isAccessible = true
            val value = (impl.get(this) as okhttp3.Headers)
                .newBuilder()
                .set(HttpHeaders.ContentType, type.toString())
                .build()
            impl.set(this, value)
        }
        else -> throw UnsupportedOperationException("Unsupported headers type ${this::class.jvmName}")
    }
}