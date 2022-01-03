package dev.kord.rest.route

import dev.kord.rest.Image

public class CdnUrl(private val rawAssetUri: String) {

    public fun toUrl(): String {
        return toUrl(UrlFormatBuilder())
    }

    public inline fun toUrl(format: UrlFormatBuilder.() -> Unit): String {
        val config = UrlFormatBuilder().apply(format)
        return toUrl(config)
    }

    public fun toUrl(config: UrlFormatBuilder): String {
        val urlBuilder = StringBuilder(rawAssetUri).append(".").append(config.format.extension)
        config.size?.let { urlBuilder.append("?size=").append(it.maxRes) }
        return urlBuilder.toString()
    }

    override fun toString(): String {
        return "CdnUrl(rawAssetUri=$rawAssetUri)"
    }

    public class UrlFormatBuilder {
        public var format: Image.Format = Image.Format.WEBP
        public var size: Image.Size? = null
    }
}
