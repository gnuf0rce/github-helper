package io.github.gnuf0rce.github.entry

import kotlinx.serialization.*

@Serializable
data class ApiError(
    @SerialName("documentation_url")
    val documentationUrl: String? = null,
    @SerialName("message")
    val message: String = "",
    @SerialName("errors")
    val errors: List<Detail> = emptyList()
) {
    @Serializable
    data class Detail(
        @SerialName("resource")
        val resource: String,
        @SerialName("field")
        val field: String,
        @SerialName("code")
        val code: String
    )
}