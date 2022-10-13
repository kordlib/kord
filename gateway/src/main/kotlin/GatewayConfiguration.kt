package dev.kord.gateway

import dev.kord.common.entity.DiscordShard
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.optional
import dev.kord.gateway.builder.PresenceBuilder
import kotlinx.serialization.Serializable
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Serializable
public data class GatewayConfiguration(
    val token: String,
    val name: String,
    val shard: DiscordShard,
    val presence: Optional<DiscordPresence> = Optional.Missing(),
    val threshold: Int,
    val intents: Intents
)

public class GatewayConfigurationBuilder(
    /**
     * The token of the bot.
     */
    public val token: String,
    /**
     * The name of the library.
     */
    public var name: String = "Kord",
    /**
     * The shard the gateway will connect to.
     */
    public var shard: DiscordShard = DiscordShard(0, 1),
    /**
     * The presence the bot should show on login.
     */
    public var presence: DiscordPresence? = null,
    /**
     * A value between 50 and 250, representing the maximum amount of members in a guild
     * before the gateway will stop sending info on offline members.
     */
    public var threshold: Int = 250,

    public var intents: Intents = Intents.nonPrivileged,
) {

    /**
     * Calls the [builder] on a new [PresenceBuilder] and assigns the result to [presence].
     */
    public inline fun presence(builder: PresenceBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        presence = PresenceBuilder().apply(builder).toPresence()
    }

    /**
     * Returns an immutable version of this builder.
     */
    public fun build(): GatewayConfiguration = GatewayConfiguration(
        token,
        name,
        shard,
        presence.optional().coerceToMissing(),
        threshold,
        intents
    )
}

@Serializable
public data class GatewayResumeConfiguration(
    val session: GatewaySession?,
    val startConfiguration: GatewayConfiguration
)