package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Permission.ManageChannels
import dev.kord.common.entity.Permission.ManageMessages
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.threads.ThreadChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.datetime.Instant
import kotlin.time.Duration

public interface ThreadChannel : GuildMessageChannel, ThreadChannelBehavior {

    private val threadData get() = data.threadMetadata.value!!

    /**
     * The id of the user who created the thread.
     */
    public val ownerId: Snowflake
        get() = data.ownerId.value!!

    public val owner: UserBehavior
        get() = UserBehavior(ownerId, kord)

    override val parentId: Snowflake get() = data.parentId.value!!


    /**
     * Whether the channel is archived.
     * Users cannot edit messages, add reactions, use slash commands, or join archived threads.
     * The only operation that should happen within an archived thread is messages being deleted.
     * Sending a message will automatically unarchive the thread, unless the thread has been locked by a moderator.
     */
    public val isArchived: Boolean get() = threadData.archived

    /**
     * Threads that have [isLocked] set to true can only be unarchived by a user with
     * the [Manage Threads][dev.kord.common.entity.Permission.ManageThreads] permission.
     */
    public val isLocked: Boolean get() = threadData.locked.orElse(false)

    /**
     * Whether the channel is nsfw.
     * This is inherited from the parent channel.
     */
    public val isNsfw: Boolean get() = data.nsfw.discordBoolean

    /**
     * timestamp when the thread's archive status was last changed.
     */
    @Deprecated(
        "archiveTimeStamp was renamed to archiveTimestamp.",
        ReplaceWith("archiveTimestamp"),
        DeprecationLevel.ERROR,
    )
    public val archiveTimeStamp: Instant
        get() = archiveTimestamp

    /**
     * The timestamp when the thread's archive status was last changed.
     */
    public val archiveTimestamp: Instant get() = threadData.archiveTimestamp

    /**
     * The time in which the thread will be auto archived after inactivity.
     */
    public val autoArchiveDuration: ArchiveDuration get() = threadData.autoArchiveDuration

    /**
     * The timestamp when the thread was created.
     *
     * This is only available for threads created after 2022-01-09.
     */
    public val createTimestamp: Instant? get() = threadData.createTimestamp.value

    /**
     * The amount of time a user has to wait before sending another message.
     *
     * Bots, as well as users with the permission [ManageMessages] or [ManageChannels], are unaffected.
     */
    public val rateLimitPerUser: Duration? get() = data.rateLimitPerUser.value

    /**
     * member count for this thread.
     * approximate maximum value is 50.
     */
    public val memberCount: OptionalInt get() = data.memberCount

    /**
     * message count for this thread.
     * approximate maximum value is 50.
     */
    public val messageCount: OptionalInt get() = data.messageCount

    /**
     * The default duration setup pre-selected for this thread.
     */
    public val defaultAutoArchiveDuration: ArchiveDuration? get() = data.defaultAutoArchiveDuration.value

    /**
     * The member of the current user in the thread.
     */
    public val member: ThreadMember? get() = data.member.unwrap { ThreadMember(it, kord) }


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadChannel {
        return ThreadChannel(data, kord, strategy.supply(kord))
    }

}

internal fun ThreadChannel(data: ChannelData, kord: Kord, supplier: EntitySupplier = kord.defaultSupplier): ThreadChannel {
    return object : ThreadChannel {

        override val data: ChannelData
            get() = data
        override val kord: Kord
            get() = kord
        override val supplier: EntitySupplier
            get() = supplier
        override val guildId: Snowflake
            get() = data.guildId.value!!
    }
}
