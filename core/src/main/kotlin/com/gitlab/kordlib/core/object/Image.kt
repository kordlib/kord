package com.gitlab.kordlib.core.`object`

import java.util.*


sealed class Image {
    abstract val dataUri: String

    class Raw(val data: ByteArray, val format: Format) : Image() {
        override val dataUri: String
            get() {
                val hash = Base64.getEncoder().encodeToString(data)
                return "data:image/${format.extension};base64,$hash"
            }
    }

    //TDOD expose HttpClient to get image from url


    sealed class Format(val extension: String) {
        object JPEG : Format("jpeg")
        object PNG : Format("png")
        object WEBP : Format("webp")
        object GIF : Format("gif")
    }
}