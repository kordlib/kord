package dev.kord.core.event.guild

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.InviteCreateData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import kotlin.coroutines.CoroutineContext
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
     * The [TopGuildChannel] the invite is for.
     */
    public val channelId: Snowflake get() = data.channelId

    /**
     * The behavior of the [TopGuildChannel] the invite is for.
     */
    public val channel: ChannelBehavior get() = ChannelBehavior(id = channelId, kord = kord)

    /**
     * The unique invite code.
     */
    public val code: String get() = data.code

    /**
     * The time at which the invite was created.
     */
    public val createdAt: Instant get() = data.createdAt.toInstant()

    /**
     * The [Guild] of the invite.
     */
    public val guildId: Snowflake? get() = data.guildId.value

    /**
     * The behavior of the [Guild] of the invite.
     */
    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(id = it, kord = kord) }

    /**
     * The [User] that created the invite, if present.
     */
    public val inviterId: Snowflake? get() = data.inviterId.value

    /**
     * The behavior of the [User] that created the invite, if present.
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
     * How long the invite is valid for (in seconds).
     */
    public val maxAge: Duration get() = Duration.seconds(data.maxAge)

    /**
     * The maximum number of times the invite can be used.
     */
    public val maxUses: Int get() = data.maxUses

    /**
     * Whether or not the invite is temporary (invited users will be kicked on disconnect unless they're assigned a role).
     */
    public val isTemporary: Boolean get() = data.temporary

    /**
     * How many times the invite has been used (always will be 0).
     */
    public val uses: Int get() = data.uses

    /**
     * Requests to get the [TopGuildChannel] this invite is for.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    public suspend fun getChannel(): TopGuildChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the [Guild] of the invite,
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNUll(): TopGuildChannel? = supplier.getChannelOfOrNull(channelId)

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
     * Requests to get the [User] that created the invite as a [Member] of the [Guild][getGuild],
     * returns null if the user isn't present, the invite did not target a guild, or no inviter created the event.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getInviterAsMemberOrNull(): Member? {
        return supplier.getMemberOrNull(guildId = guildId ?: return null, userId = inviterId ?: return null)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InviteCreateEvent =
        InviteCreateEvent(data, kord, shard, supplier)

    override fun toString(): String {
        return "InviteCreateEvent(data=$data, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
