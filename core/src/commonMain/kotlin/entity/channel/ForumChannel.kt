package dev.kord.core.entity.channel

import dev.kord.common.entity.ForumLayoutType
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ForumChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class ForumChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadOnlyChannel, ForumChannelBehavior {

    /**
     * The default layout of the forum, if present.
     */
    public val defaultForumLayout: ForumLayoutType? get() = data.defaultForumLayout.value

    override suspend fun asChannel(): ForumChannel = this
    override suspend fun asChannelOrNull(): ForumChannel = this
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ForumChannel =
        ForumChannel(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean =
        other is GuildChannelBehavior && this.id == other.id && this.guildId == other.guildId

    override fun hashCode(): Int = hash(id, guildId)
    override fun toString(): String = "ForumChannel(data=$data, kord=$kord, supplier=$supplier)"
}
