package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.ForumChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.ThreadParentChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A thread channel instance whose parent is a [TextChannel] or [ForumChannel].
 */
public class TextChannelThread(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : ThreadChannel, MaybeThreadChannel {

    public override val id: Snowflake = data.id

    /**
     * Whether this thread is private.
     */
    public val isPrivate: Boolean get() = data.type == ChannelType.PrivateThread

    public override val parentId: Snowflake = super<ThreadChannel>.parentId

    public override val parent: ThreadParentChannelBehavior = super<ThreadChannel>.parent

    public override val type: ChannelType = super<ThreadChannel>.type

    /**
     * Whether non-moderators can add other non-moderators to a thread.
     *
     * This is only applicable to [private][isPrivate] threads and will always be `false` for public threads.
     */
    public val isInvitable: Boolean get() = data.threadMetadata.value!!.invitable.discordBoolean

    override val guildId: Snowflake
        get() = data.guildId.value!!

    override val guild: GuildBehavior = super<ThreadChannel>.guild

    override suspend fun getGuild(): Guild = super<ThreadChannel>.getGuild()

    override suspend fun getGuildOrNull(): Guild? = super<ThreadChannel>.getGuildOrNull()

    override suspend fun getParent(): ThreadParentChannel = super<ThreadChannel>.getParent()

    override suspend fun getParentOrNull(): ThreadParentChannel? = super<ThreadChannel>.getParentOrNull()

    override suspend fun asChannel(): TextChannelThread = this

    override suspend fun asChannelOrNull(): TextChannelThread = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TextChannelThread {
        return TextChannelThread(data, kord, strategy.supply(kord))
    }
}
