package dev.kord.core.entity.channel

import dev.kord.common.entity.Permission.ManageChannels
import dev.kord.common.entity.Permission.ManageMessages
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*
import kotlin.time.Duration

/**
 * An instance of a Discord Text Channel associated to a guild.
 */
public class TextChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, TextChannelBehavior, ThreadParentChannel {

    /**
     * Whether the channel is nsfw.
     */
    public val isNsfw: Boolean get() = data.nsfw.discordBoolean

    /**
     * The amount of time a user has to wait before sending another message.
     *
     * Bots, as well as users with the permission [ManageMessages] or [ManageChannels], are unaffected.
     */
    public val userRateLimit: Duration? get() = data.rateLimitPerUser.value

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
