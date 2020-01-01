package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.entity.DiscordShard

data class GatewayConfiguration(
        val token: String,
        val name: String,
        val shard: DiscordShard,
        val presence: Presence?,
        val threshold: Int
)

data class GatewayConfigurationBuilder(
        val token: String,
        var name: String = "Kord",
        var shard: DiscordShard = DiscordShard(0, 1),
        var presence: Presence? = null,
        var threshold: Int = 250
) {
    fun build(): GatewayConfiguration = GatewayConfiguration(token, name, shard, presence, threshold)
}