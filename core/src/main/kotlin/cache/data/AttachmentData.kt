package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
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
    val ephemeral: OptionalBoolean = OptionalBoolean.Missing
) {
    companion object {
        fun from(entity: DiscordAttachment) = with(entity) {
            AttachmentData(id, filename, size, url, proxyUrl, height, width, ephemeral)
        }
    }
}