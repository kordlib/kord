package com.gitlab.kordlib.rest

import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.*
import io.ktor.http.HttpMethod
import io.ktor.util.toByteArray
import kotlinx.coroutines.Dispatchers
import java.util.*


class Image private constructor(val data: ByteArray, val format: Format, val size: Size) {

    val dataUri: String
        get() {
            val hash = Base64.getEncoder().encodeToString(data)
            return "data:image/${format.extension};base64,$hash"
        }

    companion object {
        fun raw(data: ByteArray, format: Format, size: Size): Image {
            return Image(data, format, size)
        }

        suspend fun fromUrl(client: HttpClient, url: String): Image = with(Dispatchers.IO) {
            val call = client.request<HttpResponse>(url) { method = HttpMethod.Get }
            val size = Size.fromResolution(call.request.url.parameters["size"])
            val contentType = call.headers["Content-Type"]
                ?: error("expected 'Content-Type' header in image request")

            @Suppress("EXPERIMENTAL_API_USAGE")
            val bytes = call.content.toByteArray()

            Image(bytes, Format.fromContentType(contentType), size)
        }
    }

    /**
     * Represents size of the Image as specified by
     */
    enum class Size(val resolution: String) {
        SIZE_16("16"),
        SIZE_32("32"),
        SIZE_64("64"),
        SIZE_128("128"),
        SIZE_256("256"),
        SIZE_512("512"),
        SIZE_1024("1024"),
        SIZE_2048("2048"),
        SIZE_4096("4096");

        companion object {
            fun fromResolution(resolution: String?): Size {
                return if (resolution == null) SIZE_128
                else values().first { it.resolution == resolution }
            }
        }
    }

    sealed class Format(val extension: String) {
        object JPEG : Format("jpeg")
        object PNG : Format("png")
        object WEBP : Format("webp")
        object GIF : Format("gif")

        companion object {
            fun fromContentType(type: String) = when (type) {
                "image/jpeg" -> JPEG
                "image/png" -> PNG
                "image/webp" -> WEBP
                "image/gif" -> GIF
                else -> error(type)
            }
        }
    }
}
