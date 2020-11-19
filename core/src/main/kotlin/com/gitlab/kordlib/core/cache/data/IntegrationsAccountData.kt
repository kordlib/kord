package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordIntegrationsAccount
import kotlinx.serialization.Serializable

@Serializable
data class IntegrationsAccountData(
        val id: String,
        val name: String,
) {
    companion object {
        fun from(entity: DiscordIntegrationsAccount) : IntegrationsAccountData = with(entity) {
            IntegrationsAccountData(id, name)
        }
    }
}