package dev.kord.rest

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import java.util.*


class Image private constructor(val data: ByteArray, val format: Format) {

    val dataUri: String
        get() {
            val hash = Base64.getEncoder().encodeToString(data)
            return "data:image/${format.extensions.first()};base64,$hash"
        }

    companion object {
        fun raw(data: ByteArray, format: Format): Image {
            return Image(data, format)
        }

        suspend fun fromUrl(client: HttpClient, url: String): Image = with(Dispatchers.IO) {
            val call = client.request<HttpResponse>(url) { method = HttpMethod.Get }
            val contentType = call.headers["Content-Type"]
                ?: error("expected 'Content-Type' header in image request")

            val bytes = call.content.toByteArray()

            Image(bytes, Format.fromContentType(contentType))
        }
    }

    sealed class Format(val extensions: List<String>) {
        constructor(vararg extensions: String) : this(extensions.toList())

        val extension: String get() = extensions.first()

        object JPEG : Format("jpeg", "jpg")
        object PNG : Format("png")
        object WEBP : Format("webp")
        object GIF : Format("gif")

        companion object {
            val values: Set<Format>
                get() = setOf(
                    JPEG,
                    PNG,
                    WEBP,
                    GIF,
                )

            fun isSupported(fileName: String): Boolean {
                return values.any {
                    it.extensions.any { extension -> fileName.endsWith(extension, true) }
                }
            }

            fun fromContentType(type: String) = when (type) {
                "image/jpeg" -> JPEG
                "image/png" -> PNG
                "image/webp" -> WEBP
                "image/gif" -> GIF
                else -> error(type)
            }
        }
    }

    /**
     * Represents size of the [Image], for requesting different sizes of Image from the Discord.
     * Both height and width of the [Image] will always be smaller than or equal to [maxRes] of the [Size].
     */
    enum class Size(val maxRes: Int) {
        Size16(16),
        Size32(32),
        Size64(64),
        Size128(128),
        Size256(256),
        Size512(512),
        Size1024(1024),
        Size2048(2048),
        Size4096(4096),
    }
}
