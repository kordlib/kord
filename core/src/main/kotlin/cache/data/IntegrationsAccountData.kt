package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordIntegrationsAccount
import kotlinx.serialization.Serializable

@Serializable
data class IntegrationsAccountData(
    val id: String,
    val name: String,
) {
    companion object {
        fun from(entity: DiscordIntegrationsAccount): IntegrationsAccountData = with(entity) {
            IntegrationsAccountData(id, name)
        }
    }
}