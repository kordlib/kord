package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordAttachment
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.OptionalInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttachmentData(
        val id: Snowflake,
        val filename: String,
        val size: Int,
        val url: String,
        val proxyUrl: String,
        val height: OptionalInt? = OptionalInt.Missing,
        val width: OptionalInt? = OptionalInt.Missing,
) {
    companion object {
        fun from(entity: DiscordAttachment) = with(entity) {
            AttachmentData(id, filename, size, url, proxyUrl, height, width)
        }
    }
}