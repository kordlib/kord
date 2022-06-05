package dev.kord.core.event.guild

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.InviteTargetType
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.InviteCreateData
import dev.kord.core.cache.data.InviteData
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Channel
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Sent when a new invite to a channel is created.
 */
public class InviteCreateEvent(
    public val data: InviteCreateData,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    /**
     * The id of the [Channel] the invite is for.
     */
    public val channelId: Snowflake get() = data.channelId

    /**
     * The behavior of the [Channel] the invite is for.
     */
    public val channel: ChannelBehavior get() = ChannelBehavior(id = channelId, kord = kord)

    /**
     * The unique invite code.
     */
    public val code: String get() = data.code

    /**
     * The time at which the invite was created.
     */
    public val createdAt: Instant get() = data.createdAt

    /**
     * The id of the [Guild] of the invite.
     */
    public val guildId: Snowflake? get() = data.guildId.value

    /**
     * The behavior of the [Guild] of the invite.
     */
    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(id = it, kord = kord) }

    /**
     * The id of the [User] that created the invite.
     */
    public val inviterId: Snowflake? get() = data.inviterId.value

    /**
     * The behavior of the [User] that created the invite.
     */
    public val inviter: UserBehavior? get() = inviterId?.let { UserBehavior(id = it, kord = kord) }

    /**
     * The behavior of the [Member] that created the invite.
     */
    public val inviterMember: MemberBehavior?
        get() {
            return MemberBehavior(guildId = guildId ?: return null, id = inviterId ?: return null, kord = kord)
        }

    /**
     * How long the invite is valid for.
     */
    public val maxAge: Duration get() = data.maxAge

    /**
     * The maximum number of times the invite can be used.
     */
    public val maxUses: Int get() = data.maxUses

    /**
     * The [type of target][InviteTargetType] for this voice channel invite.
     */
    public val targetType: InviteTargetType? get() = data.targetType.value

    /**
     * The id of the [User] whose stream to display for this voice channel stream invite.
     */
    public val targetUserId: Snowflake? get() = data.targetUserId.value

    /**
     * The behavior of the [User] whose stream to display for this voice channel stream invite.
     */
    public val targetUser: UserBehavior? get() = targetUserId?.let { UserBehavior(id = it, kord) }

    /**
     * The behavior of the [Member] whose stream to display for this voice channel stream invite.
     */
    public val targetMember: MemberBehavior?
        get() {
            return MemberBehavior(guildId = guildId ?: return null, id = targetUserId ?: return null, kord)
        }

    /**
     * The embedded [application][PartialApplication] to open for this voice channel embedded application invite.
     */
    public val targetApplication: PartialApplication?
        get() = data.targetApplication.value?.let { PartialApplication(it, kord) }

    /**
     * Whether the invite is temporary (invited users will be kicked on disconnect unless they're assigned a role).
     */
    public val isTemporary: Boolean get() = data.temporary

    /**
     * How many times the invite has been used (always will be 0).
     */
    public val uses: Int get() = data.uses

    /**
     * Requests to get the [Channel] this invite is for.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    public suspend fun getChannel(): Channel = supplier.getChannel(channelId)

    /**
     * Requests to get the [Channel] this invite is for,
     * returns null if the channel isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNUll(): Channel? = supplier.getChannelOrNull(channelId)

    /**
     * Requests to get the [Guild] of the invite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Use getGuildOrNull instead.", ReplaceWith("getGuildOrNull()"), level = DeprecationLevel.ERROR)
    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuild(it) }

    /**
     * Requests to get the [Guild] of the invite.
     * returns null if the guild isn't present, or if invite does not target a guild.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    /**
     * Requests to get the [User] that created the invite, or null if no inviter created this invite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Use getInviterOrNull instead.", ReplaceWith("getInviterOrNull()"), level = DeprecationLevel.ERROR)
    public suspend fun getInviter(): User? = inviterId?.let { supplier.getUser(it) }

    /**
     * Requests to get the [User] that created the invite,
     * returns null if the user isn't present or no inviter created this invite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getInviterOrNull(): User? = inviterId?.let { supplier.getUserOrNull(it) }

    /**
     * Requests to get the [User] that created the invite as a [Member] of the [Guild][getGuild].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated(
        "Use getInviterAsMemberOrNull instead.",
        ReplaceWith("getInviterAsMemberOrNull()"),
        level = DeprecationLevel.ERROR
    )
    public suspend fun getInviterAsMember(): Member? {
        return supplier.getMember(guildId = guildId ?: return null, userId = inviterId ?: return null)
    }

    /**
     * Requests to get the [User] that created the invite as a [Member] of the [Guild][getGuildOrNull],
     * returns null if the user isn't present, the invite did not target a guild, or no inviter created the event.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getInviterAsMemberOrNull(): Member? {
        return supplier.getMemberOrNull(guildId = guildId ?: return null, userId = inviterId ?: return null)
    }

    /**
     * Requests to get the target [User] of this invite,
     * returns null if the user isn't present or the invite did not target a user.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getTargetUserOrNull(): User? = targetUserId?.let { supplier.getUserOrNull(it) }

    /**
     * Requests to get the target [User] of this invite as a [Member] of the [Guild][getGuildOrNull],
     * returns null if the user isn't present, the invite did not target a guild, or the invite did not target a user.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getTargetUserAsMemberOrNull(): Member? {
        return supplier.getMemberOrNull(guildId = guildId ?: return null, userId = targetUserId ?: return null)
    }

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

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InviteCreateEvent =
        InviteCreateEvent(data, kord, shard, supplier)

    override fun toString(): String {
        return "InviteCreateEvent(data=$data, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
