package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordClientStatus
import com.gitlab.kordlib.common.entity.PresenceStatus
import com.gitlab.kordlib.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
data class ClientStatusData(
        val desktop: Optional<PresenceStatus> = Optional.Missing(),
        val mobile: Optional<PresenceStatus> = Optional.Missing(),
        val web: Optional<PresenceStatus> = Optional.Missing()
) {
    companion object {
        fun from(entity: DiscordClientStatus) = with(entity) {
            ClientStatusData(desktop, mobile, web)
        }
    }
}