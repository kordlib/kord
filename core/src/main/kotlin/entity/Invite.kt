package dev.kord.core.entity

import dev.kord.common.entity.InviteTargetType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.BaseInviteData
import dev.kord.core.cache.data.InviteData
import dev.kord.core.cache.data.InviteWithMetadataData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * An instance of a [Discord Invite](https://discord.com/developers/docs/resources/invite).
 */
public open class Invite(
    public open val data: BaseInviteData,
    final override val kord: Kord,
    final override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordObject, Strategizable {

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
    public val targetUserType: dev.kord.common.entity.TargetUserType?
        get() = (data as? InviteData)?.targetUserType?.value

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
    @Deprecated("Use 'getChannelOrNull' instead.", ReplaceWith("this.getChannelOrNull()"), DeprecationLevel.ERROR)
    public suspend fun getChannel(): Channel? = channelId?.let { supplier.getChannel(it) }

    /**
     * Requests to get the channel this invite is for,
     * returns null if the [Channel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): Channel? = channelId?.let { supplier.getChannelOrNull(it) }

    @Deprecated("Renamed to 'getInviterOrNull'", ReplaceWith("this.getInviterOrNull()"), DeprecationLevel.ERROR)
    public suspend fun getInviter(): User? = getInviterOrNull()

    /**
     * Requests to get the creator of the invite for,
     * returns null if the [User] isn't present or [inviterId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getInviterOrNull(): User? = inviterId?.let { supplier.getUserOrNull(it) }

    @Deprecated("Renamed to 'getTargetUserOrNull'", ReplaceWith("this.getTargetUserOrNull()"), DeprecationLevel.ERROR)
    public suspend fun getTargetUser(): User? = getTargetUserOrNull()

    /**
     * Requests to get the user this invite was created for,
     * returns null if the [User] isn't present or [targetUserId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getTargetUserOrNull(): User? = targetUserId?.let { supplier.getUserOrNull(it) }

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

    override fun toString(): String = "Invite(data=$data, kord=$kord, supplier=$supplier)"
}

/**
 * An instance of a [Discord Invite](https://discord.com/developers/docs/resources/invite) with
 * [extra information](https://discord.com/developers/docs/resources/invite#invite-metadata-object).
 */
public class InviteWithMetadata(
    override val data: InviteWithMetadataData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
) : Invite(data, kord, supplier) {

    /** Number of times this invite has been used. */
    public val uses: Int get() = data.uses

    /** Max number of times this invite can be used. */
    public val maxUses: Int get() = data.maxUses

    /** Duration after which the invite expires. */
    public val maxAge: Duration get() = data.maxAge

    /** Whether this invite only grants temporary membership. */
    public val temporary: Boolean get() = data.temporary

    /** When this invite was created. */
    public val createdAt: Instant get() = data.createdAt

    /** Returns a new [InviteWithMetadata] with the given [strategy]. */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InviteWithMetadata =
        InviteWithMetadata(data, kord, strategy.supply(kord))

    override fun toString(): String = "InviteWithMetadata(data=$data, kord=$kord, supplier=$supplier)"
}
