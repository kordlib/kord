package dev.kord.gateway

import dev.kord.common.entity.DiscordShard
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.optional
import dev.kord.gateway.builder.PresenceBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

data class GatewayConfiguration(
    val token: String,
    val name: String,
    val shard: DiscordShard,
    val presence: Optional<DiscordPresence> = Optional.Missing(),
    val threshold: Int,
    val intents: Intents
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
    var presence: DiscordPresence? = null,
    /**
     * A value between 50 and 250, representing the maximum amount of members in a guild
     * before the gateway will stop sending info on offline members.
     */
    var threshold: Int = 250,

    var intents: Intents = Intents.nonPrivileged
) {

    /**
     * Calls the [builder] on a new [PresenceBuilder] and assigns the result to [presence].
     */
    @OptIn(ExperimentalContracts::class)
    inline fun presence(builder: PresenceBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        presence = PresenceBuilder().apply(builder).toPresence()
    }

    /**
     * Returns an immutable version of this builder.
     */
    fun build(): GatewayConfiguration = GatewayConfiguration(
        token,
        name,
        shard,
        presence.optional().coerceToMissing(),
        threshold,
        intents
    )

    companion object
}