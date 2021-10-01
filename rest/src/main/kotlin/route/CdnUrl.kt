package dev.kord.rest.route

import dev.kord.rest.Image

class CdnUrl(private val rawAssetUri: String) {

    inline fun toUrl(builder: UrlBuilder.() -> Unit = {}): String {
        val config = UrlBuilder().apply(builder)
        return toUrl(config)
    }

    fun toUrl(config: UrlBuilder): String {
        val urlBuilder = StringBuilder(rawAssetUri).append(".").append(config.format.extension)
        config.size?.let { urlBuilder.append("?size=").append(it.maxRes) }
        return urlBuilder.toString()
    }

    override fun toString(): String {
        return "CdnUrl(rawAssetUri=$rawAssetUri)"
    }

    data class UrlBuilder(var format: Image.Format = Image.Format.WEBP, var size: Image.Size? = null)
}