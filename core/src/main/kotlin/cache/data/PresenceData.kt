package dev.kord.core.cache.data

import dev.kord.cache.api.data.description
import dev.kord.common.entity.*
import kotlinx.serialization.Serializable

val PresenceData.id get() = "$userId$guildId"

@Serializable
data class PresenceData(
        val userId: Snowflake,
        val guildId: Snowflake,
        val status: PresenceStatus,
        val activities: List<ActivityData>,
        val clientStatus: ClientStatusData,
) {

    companion object {
        val description = description(PresenceData::id)

        fun from(guildId: Snowflake, entity: DiscordPresenceUpdate) = with(entity) {
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