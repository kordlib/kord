package dev.kord.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * A _Stage Instance_ holds information about a live stage.
 *
 * @property id The id of this Stage instance
 * @property guildId The guild id of the associated Stage channel
 * @property channelId The id of the associated Stage channel
 * @property topic The topic of the Stage instance (1-120 characters)
 */
@Serializable
data class DiscordStageInstance(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake,
    val topic: String
)
