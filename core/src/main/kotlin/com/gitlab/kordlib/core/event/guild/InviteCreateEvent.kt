package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
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
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    /**
     * The [GuildChannel] the invite is for.
     */
    val channelId: Snowflake get() = Snowflake(data.channelId)

    /**
     * The behavior of the [GuildChannel] the invite is for.
     */
    val channel: GuildChannelBehavior get() = GuildChannelBehavior(guildId = guildId, id = channelId, kord = kord)

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
    val guildId: Snowflake get() = Snowflake(data.guildId)

    /**
     * The behavior of the [Guild] of the invite.
     */
    val guild: GuildBehavior get() = GuildBehavior(id = guildId, kord = kord)

    /**
     * The [User] that created the invite.
     */
    val inviterId: Snowflake get() = Snowflake(data.inviterId)

    /**
     * The behavior of the [User] that created the invite.
     */
    val inviter: UserBehavior get() = UserBehavior(id = inviterId, kord = kord)

    /**
     * The behavior of the [Member] that created the invite.
     */
    val inviterMember: MemberBehavior get() = MemberBehavior(guildId = guildId, id = inviterId, kord = kord)

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
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] of the invite,
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the [User] that created the invite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    suspend fun getInviter(): User = supplier.getUser(inviterId)

    /**
     * Requests to get the [User] that created the invite,
     * returns null if the user isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getInviterOrNull(): User? = supplier.getUserOrNull(inviterId)

    /**
     * Requests to get the [User] that created the invite as a [Member] of the [Guild][getGuild].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    suspend fun getInviterAsMember(): Member = supplier.getMember(guildId = guildId, userId = inviterId)

    /**
     * Requests to get the [User] that created the invite as a [Member] of the [Guild][getGuild],
     * returns null if the user isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getInviterAsMemberOrNull(): Member? = supplier.getMemberOrNull(guildId = guildId, userId = inviterId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InviteCreateEvent =
            InviteCreateEvent(data, kord, shard, supplier)
}
