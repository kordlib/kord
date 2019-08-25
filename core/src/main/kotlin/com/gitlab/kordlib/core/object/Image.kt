package com.gitlab.kordlib.core.`object`

import java.util.*


class Image private constructor(val data: ByteArray, val format: Format) {

    val dataUri: String get() {
        val hash = Base64.getEncoder().encodeToString(data)
        return "data:image/${format.extension};base64,$hash"
    }

    companion object {
        fun raw(data: ByteArray, format: Format) : Image {
            return Image(data, format)
        }

        suspend fun fromUrl(url: String): Image {
            TODO()
        }
    }

    sealed class Format(val extension: String) {
        object JPEG : Format("jpeg")
        object PNG : Format("png")
        object WEBP : Format("webp")
        object GIF : Format("gif")
    }
}