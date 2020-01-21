package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.cache.data.InviteCreateData
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.toInstant
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.seconds

/**
 * Sent when a new invite to a channel is created.
 */
class InviteCreateEvent(val data: InviteCreateData, override val kord: Kord) : Event {

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
     */
    suspend fun getChannel(): GuildChannel = kord.getChannel(guildId) as GuildChannel

    /**
     * Requests to get the [Guild] of the invite.
     */
    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

    /**
     * Requests to get the [User] that created the invite.
     */
    suspend fun getInviter(): User = kord.getUser(inviterId)!!

    /**
     * Requests to get the [User] that created the invite as a [Member] of the [Guild][getGuild].
     */
    suspend fun getInviterAsMember(): Member = kord.getMember(guildId = guildId, userId = inviterId)!!

}
