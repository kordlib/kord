package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.ThreadParentChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

public class DeletedThreadChannel(
    public val data: ChannelData,
    public val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Strategizable {

    public val id: Snowflake
        get() = data.id

    public val type: ChannelType get() = data.type

    public val guildId: Snowflake get() = data.guildId.value!!

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public val parentId: Snowflake get() = data.parentId!!.value!!

    public val parent: ThreadParentChannelBehavior get() = ThreadParentChannelBehavior(guildId, id, kord)

    /**
     * Requests to get this channel's [Guild].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get this channel's [Guild],
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)


    /**
     * Requests to get this channel's [ThreadParentChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the thread parent wasn't present.
     */
    public suspend fun getParent(): ThreadParentChannel {
        return supplier.getChannelOf(parentId)
    }

    /**
     * Requests to get this channel's [ThreadParentChannel],
     * returns null if the thread parent isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getParentOrNull(): ThreadParentChannel? {
        return supplier.getChannelOfOrNull(parentId)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DeletedThreadChannel {
        return DeletedThreadChannel(data, kord, strategy.supply(kord))
    }
}
