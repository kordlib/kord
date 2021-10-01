package dev.kord.rest.route

import dev.kord.rest.Image

class CdnUrlBuilder(private val rawAssetUri: String) {

    fun toUrl(): String {
        return toUrl(UrlFormat())
    }

    inline fun toUrl(format: UrlFormat.() -> Unit): String {
        val config = UrlFormat().apply(format)
        return toUrl(config)
    }

    fun toUrl(config: UrlFormat): String {
        val urlBuilder = StringBuilder(rawAssetUri).append(".").append(config.format.extension)
        config.size?.let { urlBuilder.append("?size=").append(it.maxRes) }
        return urlBuilder.toString()
    }

    override fun toString(): String {
        return "CdnUrl(rawAssetUri=$rawAssetUri)"
    }

    data class UrlFormat(var format: Image.Format = Image.Format.WEBP, var size: Image.Size? = null)
}