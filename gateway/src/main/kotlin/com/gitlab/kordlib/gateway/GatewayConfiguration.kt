package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.entity.DiscordShard
import com.gitlab.kordlib.gateway.builder.PresenceBuilder

data class GatewayConfiguration(
        val token: String,
        val name: String,
        val shard: DiscordShard,
        val presence: Presence?,
        val threshold: Int,
        val intents: Intents?
)

data class GatewayConfigurationBuilder(
        /**
         * The token of the bot.
         */
        val token: String,
        /**
         * The name of the library.
         */
        var name: String = "Kord",
        /**
         * The shard the gateway will connect to.
         */
        var shard: DiscordShard = DiscordShard(0, 1),
        /**
         * The presence the bot should show on login.
         */
        var presence: Presence? = null,
        /**
         * A value between 50 and 250, representing the maximum amount of members in a guild
         * before the gateway will stop sending info on offline members.
         */
        var threshold: Int = 250,

        var intents: Intents? = null
) {

    /**
     * Calls the [builder] on a new [PresenceBuilder] and assigns the result to [presence].
     */
    inline fun presence(builder: PresenceBuilder.() -> Unit) {
        presence = PresenceBuilder().apply(builder).toPresence()
    }

    /**
     * Returns an immutable version of this builder.
     */
    fun build(): GatewayConfiguration = GatewayConfiguration(token, name, shard, presence, threshold, intents)

    companion object
}