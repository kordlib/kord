package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.threads.ThreadChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

sealed class ThreadChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : GuildMessageChannel, ThreadChannelBehavior {

    private val threadData get() = data.threadsMetadata.value!!

    override val id: Snowflake get() = data.id

    override val guildId: Snowflake get() = data.guildId.value!!

    override val name: String get() = data.name.value!!

    val ownerId: Snowflake
        get() = data.ownerId.value!!

    val owner: UserBehavior
        get() = UserBehavior(ownerId, kord)

    val isArchived: Boolean get() = threadData.archived

    val isLocked: Boolean get() = threadData.locked.orElse(false)

    /**
     * Whether the channel is nsfw.
     */
    val isNsfw: Boolean get() = data.nsfw.discordBoolean

    val archiveTimeStamps: String get() = threadData.archiveTimestamp

    val autoArchiveDuration: ArchiveDuration get() = threadData.autoArchiveDuration

    val rateLimitPerUser: Int? get() = data.rateLimitPerUser.value

    val memberCount get() = data.memberCount

    val messageCount get() = data.messageCount

    val defaultAutoArchiveDuration: ArchiveDuration? get() = data.defaultAutoArchiveDuration.value

    val member: ThreadUser? get() = data.member.unwrap { ThreadUser(it, kord) }

    override suspend fun asChannel(): ThreadChannel {
        return super<GuildMessageChannel>.asChannel() as ThreadChannel
    }

    override suspend fun asChannelOrNull(): ThreadChannel? {
        return super<GuildMessageChannel>.asChannelOrNull() as? ThreadChannel
    }

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadChannel

}