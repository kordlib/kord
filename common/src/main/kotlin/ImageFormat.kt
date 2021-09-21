package dev.kord.common

enum class ImageFormat(val extension: String) {
    JPEG("jepg"),
    JPG("jpg"),
    GIF("gif"),
    WebP("webp"),
    PNG("png"),
    Lottie("json");

    companion object {
        fun isSupported(fileName: String): Boolean {
            return ImageFormat.values().any { fileName.endsWith(it.extension, true) }
        }
    }
}