package dev.kord.core.entity

import dev.kord.common.entity.InviteTargetType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.InviteData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * An instance of a [Discord Invite](https://discord.com/developers/docs/resources/invite).
 */
public class Invite(
    public val data: InviteData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {

    public inner class Metadata(public val data: InviteData.Metadata) {

        /** Number of times this invite has been used. */
        public val uses: Int get() = data.uses

        /** Max number of times this invite can be used. */
        public val maxUses: Int get() = data.maxUses

        /** Duration after which the invite expires. */
        public val maxAge: Duration get() = data.maxAge.toDuration(DurationUnit.SECONDS)

        /** Whether this invite only grants temporary membership. */
        public val temporary: Boolean get() = data.temporary

        /** When this invite was created */
        public val createdAt: Instant get() = data.createdAt
    }

    /** Extra information about this invite. */
    public val metadata: Metadata? get() = data.metadata.value?.let { Metadata(it) }

    /**
     * The unique code of this invite.
     */
    public val code: String get() = data.code

    /**
     * The id of the channel this invite is for.
     */
    public val channelId: Snowflake? get() = data.channelId

    /**
     * Returns [PartialGuild] if this invite is for a guild.
     */
    public val partialGuild: PartialGuild? get() = data.guild.value?.let { PartialGuild(it, kord) }

    /**
     * The id of the user who created this invite, if present.
     */
    public val inviterId: Snowflake? get() = data.inviterId.value

    /**
     * The id of the user whose stream to display for this voice channel stream invite
     */
    public val targetUserId: Snowflake? get() = data.targetUserId.value

    /** The embedded application to open for this voice channel embedded application invite. */
    public val targetApplication: PartialApplication?
        get() = data.targetApplication.value?.let { PartialApplication(it, kord) }

    /**
     * The behavior of the channel this invite is for.
     */
    public val channel: ChannelBehavior? get() = channelId?.let { ChannelBehavior(it, kord) }


    /**
     * The user behavior of the user who created this invite, if present.
     */
    public val inviter: UserBehavior? get() = inviterId?.let { UserBehavior(it, kord) }

    /**
     * The [type of target][InviteTargetType] for this voice channel invite.
     */
    public val targetType: InviteTargetType? get() = data.targetType.value

    /**
     * The behavior of the user whose stream to display for this voice channel stream invite
     */
    public val targetUser: UserBehavior? get() = targetUserId?.let { UserBehavior(it, kord) }

    /**
     * The type of user target for this invite, if present.
     */
    @Suppress("DEPRECATION")
    @Deprecated("This is no longer documented. Use 'targetType' instead.", ReplaceWith("this.targetType"))
    public val targetUserType: dev.kord.common.entity.TargetUserType? get() = data.targetUserType.value

    /**
     * Approximate count of total members.
     */
    public val approximateMemberCount: Int? get() = data.approximateMemberCount.value

    /**
     * Approximate count of online members.
     */
    public val approximatePresenceCount: Int? get() = data.approximatePresenceCount.value

    /** The expiration date of this invite. */
    public val expiresAt: Instant? get() = data.expiresAt.value

    /** The event this invite is for. */
    public val guildScheduledEvent: GuildScheduledEvent?
        get() = data.guildScheduledEvent.value?.let { GuildScheduledEvent(it, kord) }

    /**
     * Requests to get the channel this invite is for.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Channel] wasn't present.
     */
    public suspend fun getChannel(): Channel? = channelId?.let { supplier.getChannelOf(it) }

    /**
     * Requests to get the channel this invite is for,
     * returns null if the [Channel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): Channel? = channelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Requests to get the creator of the invite for,
     * returns null if the [User] isn't present or [inviterId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getInviter(): User? = inviterId?.let { supplier.getUserOrNull(it) }

    /**
     * Requests to get the user this invite was created for,
     * returns null if the [User] isn't present or [targetUserId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getTargetUser(): User? = targetUserId?.let { supplier.getUserOrNull(it) }

    /**
     * Requests to delete this invite.
     *
     * @param reason the reason showing up in the audit log
     */
    public suspend fun delete(reason: String? = null): Invite {
        val response = kord.rest.invite.deleteInvite(data.code, reason)
        val data = InviteData.from(response)
        return Invite(data, kord)
    }

    /**
     * Returns a new [Invite] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Invite =
        Invite(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "Invite(data=$data, kord=$kord, supplier=$supplier)"
    }

}
