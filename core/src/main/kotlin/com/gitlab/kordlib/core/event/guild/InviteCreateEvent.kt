package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.cache.data.InviteCreateData
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import com.gitlab.kordlib.core.toInstant
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.seconds

/**
 * Sent when a new invite to a channel is created.
 */
class InviteCreateEvent(
        val data: InviteCreateData,
        override val kord: Kord,
        override val shard: Int,
        override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    /**
     * The [GuildChannel] the invite is for.
     */
    val channelId: Snowflake get() = data.channelId

    /**
     * The behavior of the [GuildChannel] the invite is for.
     */
    val channel: ChannelBehavior get() = ChannelBehavior(id = channelId, kord = kord)

    /**
     * The unique invite code.
     */
    val code: String get() = data.code

    /**
     * The time at which the invite was created.
     */
    val createdAt: Instant get() = data.createdAt.toInstant()

    /**
     * The [Guild] of the invite.
     */
    val guildId: Snowflake? get() = data.guildId.value

    /**
     * The behavior of the [Guild] of the invite.
     */
    val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(id = it, kord = kord) }

    /**
     * The [User] that created the invite, if present.
     */
    val inviterId: Snowflake? get() = data.inviterId.value

    /**
     * The behavior of the [User] that created the invite, if present.
     */
    val inviter: UserBehavior? get() = inviterId?.let { UserBehavior(id = it, kord = kord) }

    /**
     * The behavior of the [Member] that created the invite.
     */
    val inviterMember: MemberBehavior?
        get() {
            return MemberBehavior(guildId = guildId ?: return null, id = inviterId ?: return null, kord = kord)
        }

    /**
     * How long the invite is valid for (in seconds).
     */
    val maxAge: Duration get() = data.maxAge.seconds

    /**
     * The maximum number of times the invite can be used.
     */
    val maxUses: Int get() = data.maxUses

    /**
     * Whether or not the invite is temporary (invited users will be kicked on disconnect unless they're assigned a role).
     */
    val isTemporary: Boolean get() = data.temporary

    /**
     * How many times the invite has been used (always will be 0).
     */
    val uses: Int get() = data.uses

    /**
     * Requests to get the [GuildChannel] this invite is for.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    suspend fun getChannel(): GuildChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the [Guild] of the invite,
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getChannelOrNUll(): GuildChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the [Guild] of the invite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Use getGuildOrNull instead.", ReplaceWith("getGuildOrNull()"), level = DeprecationLevel.ERROR)
    suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuild(it) }

    /**
     * Requests to get the [Guild] of the invite.
     * returns null if the guild isn't present, or if invite does not target a guild.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    /**
     * Requests to get the [User] that created the invite, or null if no inviter created this invite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Use getInviterOrNull instead.", ReplaceWith("getInviterOrNull()"), level = DeprecationLevel.ERROR)
    suspend fun getInviter(): User? = inviterId?.let { supplier.getUser(it) }

    /**
     * Requests to get the [User] that created the invite,
     * returns null if the user isn't present or no inviter created this invite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getInviterOrNull(): User? = inviterId?.let { supplier.getUserOrNull(it) }

    /**
     * Requests to get the [User] that created the invite as a [Member] of the [Guild][getGuild].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Use getInviterAsMemberOrNull instead.", ReplaceWith("getInviterAsMemberOrNull()"), level = DeprecationLevel.ERROR)
    suspend fun getInviterAsMember(): Member? {
        return supplier.getMember(guildId = guildId ?: return null, userId = inviterId ?: return null)
    }

    /**
     * Requests to get the [User] that created the invite as a [Member] of the [Guild][getGuild],
     * returns null if the user isn't present, the invite did not target a guild, or no inviter created the event.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getInviterAsMemberOrNull(): Member? {
        return supplier.getMemberOrNull(guildId = guildId ?: return null, userId = inviterId ?: return null)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InviteCreateEvent =
            InviteCreateEvent(data, kord, shard, supplier)

    override fun toString(): String {
        return "InviteCreateEvent(data=$data, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
