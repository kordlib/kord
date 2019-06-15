package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.common.entity.Shard

data class GatewayConfiguration(
        val token: String,
        val name: String,
        val shard: Shard,
        val presence: Presence?,
        val threshold: Int
)

data class GatewayConfigurationBuilder(
        val token: String,
        var name: String = "Kord",
        var shard: Shard = Shard(0, 1),
        var presence: Presence? = null,
        var threshold: Int = 250
) {
    fun build(): GatewayConfiguration = GatewayConfiguration(token, name, shard, presence, threshold)
}