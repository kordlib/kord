package dev.kord.core.entity.channel

import dev.kord.common.entity.optional.getOrThrow
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.TopGuildChannelBehavior
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * An instance of a Discord Text Channel associated to a guild.
 */
class TextChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, TextChannelBehavior, ThreadParentChannel {

    /**
     * Whether the channel is nsfw.
     */
    val isNsfw: Boolean get() = data.nsfw.discordBoolean

    /**
     * The amount of seconds a user has to wait before sending another message.
     */
    val userRateLimit: Int get() = data.rateLimitPerUser.getOrThrow()

    /**
     * returns a new [TextChannel] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.cacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TextChannel =
        TextChannel(data, kord, strategy.supply(kord))

    override suspend fun asChannel(): TextChannel = this

    override suspend fun asChannelOrNull(): TextChannel = this

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "TextChannel(data=$data, kord=$kord, supplier=$supplier)"
    }

}
