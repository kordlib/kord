package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.PresenceUpdateData
import com.gitlab.kordlib.common.entity.Status
import kotlinx.serialization.Serializable

val PresenceData.id get() = "$userId$guildId"

@Serializable
data class PresenceData(
        val userId: String,
        val roles: List<String>,
        val game: ActivityData?,
        val guildId: String,
        val status: Status,
        val clientStatus: ClientStatusData
) {

    companion object {
        val description = description(PresenceData::id)

        fun from(entity: PresenceUpdateData) = with(entity) {
            PresenceData(user.id, roles, game?.let { ActivityData.from(it) }, guildId, status, ClientStatusData.from(clientStatus))
        }
    }

}