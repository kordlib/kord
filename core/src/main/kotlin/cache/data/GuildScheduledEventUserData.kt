package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordGuildMember
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class GuildScheduledEventUserData(
    @SerialName("guild_scheduled_event_id")
    val guildScheduledEventId: Snowflake,
    val user: UserData,
    val member: Optional<MemberData> = Optional.Missing(),
)
public val GuildScheduledEventUserData.guildId: Snowflake get() = this.user.id
