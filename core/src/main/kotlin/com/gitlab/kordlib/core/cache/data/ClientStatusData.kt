package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.DiscordClientStatus
import com.gitlab.kordlib.common.entity.Status
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class ClientStatusData(
        val desktop: Status?,
        val mobile: Status?,
        val web: Status?
) {
    companion object {
        fun from(entity: DiscordClientStatus) = with(entity) {
            ClientStatusData(desktop, mobile, web)
        }
    }
}