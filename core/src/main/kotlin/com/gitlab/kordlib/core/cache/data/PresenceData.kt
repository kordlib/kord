package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordPresenceUpdateData
import com.gitlab.kordlib.common.entity.Status
import kotlinx.serialization.Serializable

val PresenceData.id get() = "$userId$guildId"

@Serializable
data class PresenceData(
        val userId: Long,
        val roles: List<Long>? = null,
        val game: ActivityData?,
        val guildId: Long? = null,
        val status: Status,
        val activities: List<ActivityData>,
        val clientStatus: ClientStatusData
) {

    companion object {
        val description = description(PresenceData::id)

        fun from(entity: DiscordPresenceUpdateData) = with(entity) {
            PresenceData(
                    user.id.toLong(),
                    roles?.map { it.toLong() },
                    game?.let { ActivityData.from(it) },
                    guildId?.toLong(),
                    status,
                    activities.map { ActivityData.from(it) },
                    ClientStatusData.from(clientStatus)
            )
        }
    }

}