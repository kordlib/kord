package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordMessageReference
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReferenceData(
    @SerialName("message_id")
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
) {
    companion object {
        fun from(entity: DiscordMessageReference): MessageReferenceData = with(entity) {
            return MessageReferenceData(id, channelId, guildId)
        }
    }
}