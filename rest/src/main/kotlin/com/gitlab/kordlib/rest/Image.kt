package com.gitlab.kordlib.rest

import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.*
import io.ktor.http.HttpMethod
import io.ktor.util.toByteArray
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging
import java.awt.Dimension
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO
import javax.imageio.stream.ImageInputStream

private val logger = KotlinLogging.logger { }

class Image private constructor(val data: ByteArray, val format: Format, val resolution: Resolution) {

    val dataUri: String
        get() {
            val hash = Base64.getEncoder().encodeToString(data)
            return "data:image/${format.extension};base64,$hash"
        }

    companion object {
        fun raw(data: ByteArray, format: Format): Image {
            return Image(data, format, Resolution.fromImageData(data, format))
        }

        suspend fun fromUrl(client: HttpClient, url: String): Image = with(Dispatchers.IO) {
            val call = client.request<HttpResponse>(url) { method = HttpMethod.Get }
            val contentType = call.headers["Content-Type"]
                ?: error("expected 'Content-Type' header in image request")

            @Suppress("EXPERIMENTAL_API_USAGE")
            val bytes = call.content.toByteArray()
            val format = Format.fromContentType(contentType)
            val resolution = Resolution.fromImageData(bytes, format)

            Image(bytes, format, resolution)
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

    /**
     * Exact resolution of the Image.
     */
    data class Resolution(val width: Int, val height: Int) {

        companion object {
            val UnknownResolution = Resolution(0, 0)

            /**
             * Reads the resolution of the image from its header.
             */
            fun fromImageData(data: ByteArray, format: Format): Resolution {
                val iter = ImageIO.getImageReadersBySuffix(format.extension)
                for (reader in iter) {
                    try {
                        reader.input = ImageIO.createImageInputStream(data.inputStream())
                        return Resolution(reader.getWidth(reader.minIndex), reader.getHeight(reader.minIndex))
                    } catch (e: IOException) {
                        logger.error(e) { e.message }
                    } finally {
                        reader.dispose()
                    }
                }

                // Manual header parsing of WebP images, as not supported by ImageIO
                if (String(ByteArray(4, data::get)) == "RIFF" && data[15].toChar() == 'X') {
                    val width = 1 + get24bit(data, 24)
                    val height = 1 + get24bit(data, 27)

                    if (width.toLong() * height <= 4294967296L) return Resolution(width, height)
                }

                return UnknownResolution
            }

            private fun get24bit(data: ByteArray, index: Int): Int {
                return ((data[index].toInt() and 0xFF) or (data[index + 1].toInt() and 0xFF shl 8) or (data[index + 2].toInt() and 0xFF shl 16))
            }
        }

    }

    /**
     * Represents size of the [Image], for requesting different sizes from the discord.
     * [Image.Resolution] (both height and width) will always be smaller than or equal to [maxRes] of the [Size].
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
