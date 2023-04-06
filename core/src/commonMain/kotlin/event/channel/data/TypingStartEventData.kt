package dev.kord.core.event.channel.data

import dev.kord.common.entity.DiscordGuildMember
import dev.kord.common.entity.DiscordTyping
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.InstantInEpochSecondsSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * The data for the event dispatched when a user starts typing in a channel.
 *
 * See [Typing Start](https://discord.com/developers/docs/topics/gateway-events#typing-start)
 */
@Serializable
public data class TypingStartEventData(
    val channelId: Snowflake,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val userId: Snowflake,
    @Serializable(with = InstantInEpochSecondsSerializer::class)
    val timestamp: Instant,
    val member: Optional<DiscordGuildMember> = Optional.Missing()
) {
    public companion object {
        public fun from(entity: DiscordTyping): TypingStartEventData = with(entity) {
            TypingStartEventData(channelId, guildId, userId, timestamp, member)
        }
    }
}
