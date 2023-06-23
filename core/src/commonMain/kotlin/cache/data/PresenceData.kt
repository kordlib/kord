package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.*
import kotlinx.serialization.Serializable

public val PresenceData.id: String get() = "$userId$guildId"

@Serializable
public data class PresenceData(
    val userId: Snowflake,
    val guildId: Snowflake,
    val status: PresenceStatus,
    val activities: List<ActivityData>,
    val clientStatus: ClientStatusData,
) {
    public companion object {
        public val description: DataDescription<PresenceData, String> = description(PresenceData::id)

        public fun from(guildId: Snowflake, entity: DiscordPresenceUpdate): PresenceData = with(entity) {
            PresenceData(
                user.id,
                guildId,
                status,
                activities.map { ActivityData.from(it) },
                ClientStatusData.from(clientStatus),
            )
        }
    }
}
