package dev.kord.core.entity.channel

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.MediaChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class MediaChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : ThreadOnlyChannel, MediaChannelBehavior {

    override suspend fun asChannel(): MediaChannel = this
    override suspend fun asChannelOrNull(): MediaChannel = this
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MediaChannel =
        MediaChannel(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean =
        other is GuildChannelBehavior && this.id == other.id && this.guildId == other.guildId

    override fun hashCode(): Int = hash(id, guildId)
    override fun toString(): String = "MediaChannel(data=$data, kord=$kord, supplier=$supplier)"
}
