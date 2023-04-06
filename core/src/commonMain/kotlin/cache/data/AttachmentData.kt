package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import kotlinx.serialization.Serializable

@Serializable
public data class AttachmentData(
    val id: Snowflake,
    val filename: String,
    val description: Optional<String> = Optional.Missing(),
    val contentType: Optional<String> = Optional.Missing(),
    val size: Int,
    val url: String,
    val proxyUrl: String,
    val height: OptionalInt? = OptionalInt.Missing,
    val width: OptionalInt? = OptionalInt.Missing,
    val ephemeral: OptionalBoolean = OptionalBoolean.Missing
) {
    public companion object {
        public fun from(entity: DiscordAttachment): AttachmentData = with(entity) {
            AttachmentData(id, filename, description, contentType, size, url, proxyUrl, height, width, ephemeral)
        }
    }
}
