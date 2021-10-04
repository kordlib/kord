package dev.kord.rest.route

import dev.kord.rest.Image

class CdnUrl(private val rawAssetUri: String) {

    fun toUrl(): String {
        return toUrl(UrlFormatBuilder())
    }

    inline fun toUrl(format: UrlFormatBuilder.() -> Unit): String {
        val config = UrlFormatBuilder().apply(format)
        return toUrl(config)
    }

    fun toUrl(config: UrlFormatBuilder): String {
        val urlBuilder = StringBuilder(rawAssetUri).append(".").append(config.format.extension)
        config.size?.let { urlBuilder.append("?size=").append(it.maxRes) }
        return urlBuilder.toString()
    }

    override fun toString(): String {
        return "CdnUrl(rawAssetUri=$rawAssetUri)"
    }

    data class UrlFormatBuilder(var format: Image.Format = Image.Format.WEBP, var size: Image.Size? = null)
}