package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The event dispatched when a guild's emojis have been updated.
 *
 * The [old][old] [GuildEmoji] set may be `null` unless it has been stored by the cache.
 *
 * See [Guild Emoji Update](https://discord.com/developers/docs/topics/gateway-events#guild-emojis-update)
 */
public class EmojisUpdateEvent(
    public val guildId: Snowflake,
    public val emojis: Set<GuildEmoji>,
    public val old: Set<GuildEmoji>?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    /**
     * The guild this event was triggered from.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the [Guild] this update was triggered from.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] this update was triggered from, or `null` if the guild wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EmojisUpdateEvent =
        EmojisUpdateEvent(guildId, emojis, old, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "EmojisUpdateEvent(guildId=$guildId, emojis=$emojis, old=$old, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
