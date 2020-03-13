package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.rest.json.response.DiscordIntegrationsAccount
import com.gitlab.kordlib.rest.json.response.IntegrationExpireBehavior
import com.gitlab.kordlib.rest.json.response.IntegrationResponse
import kotlinx.serialization.Serializable

@Serializable
data class IntegrationData(
        val id: Long,
        val name: String,
        val type: String,
        val enabled: Boolean,
        val syncing: Boolean,
        val roleId: Long,
        val guildId: Long,
        val enableEmoticons: Boolean = false,
        val expireBehavior: IntegrationExpireBehavior,
        val expireGracePeriod: Int,
        val userId: Long,
        //no clue what this is, we'll ignore it for now
        val account: DiscordIntegrationsAccount,
        val syncedAt: String
) {

    companion object {

        fun from(guildId: Long, response: IntegrationResponse) = with(response) {
            IntegrationData(
                    id = id.toLong(),
                    name = name,
                    type=  type,
                    guildId = guildId,
                    roleId = roleId.toLong(),
                    account = account,
                    enabled = enabled,
                    enableEmoticons = enableEmoticons ?: false,
                    expireBehavior = expireBehavior,
                    expireGracePeriod = expireGracePeriod,
                    syncedAt = syncedAt,
                    syncing = syncing,
                    userId = user.id.toLong()
            )

        }

    }

}