package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TargetUserType
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
import dev.kord.core.toSnowflakeOrNull

/**
 * An instance of a [Discord Invite](https://discord.com/developers/docs/resources/invite).
 */
data class Invite(
    val data: InviteData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {

    /**
     * The unique code of this invite.
     */
    val code: String get() = data.code

    /**
     * The id of the channel this invite is associated to.
     */
    val channelId: Snowflake get() = data.channelId

    /**
     * Returns [PartialGuild] if the invite was made in a guild, or null if not.
     */
    val partialGuild: PartialGuild? get() = data.guild.value?.let { PartialGuild(it, kord) }

    /**
     * The id of the user who created this invite, if present.
     */
    val inviterId: Snowflake? get() = data.inviterId.value

    /**
     * The id of the target user this invite is associated to, if present.
     */
    val targetUserId: Snowflake? get() = data.targetUserId.value

    /**
     * The behavior of the channel this invite is associated to.
     */
    val channel: ChannelBehavior get() = ChannelBehavior(channelId, kord)


    /**
     * The user behavior of the user who created this invite, if present.
     */
    val inviter: UserBehavior? get() = inviterId?.let { UserBehavior(it, kord) }

    /**
     * The user behavior of the target user this invite is associated to, if present.
     */
    val targetUser: UserBehavior? get() = targetUserId?.let { UserBehavior(it, kord) }

    /**
     * The type of user target for this invite, if present.
     */
    val targetUserType: TargetUserType? get() = data.targetUserType.value

    /**
     * Approximate count of members in the channel this invite is associated to, if present.
     */
    val approximateMemberCount: Int? get() = data.approximateMemberCount.value

    /**
     * Approximate count of members online in the channel this invite is associated to, if present. (only present when the target user isn't null)
     */
    val approximatePresenceCount: Int? get() = data.approximatePresenceCount.value

    /**
     * Requests to get the channel this invite is for.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Channel] wasn't present.
     */
    suspend fun getChannel(): Channel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel this invite is for,
     * returns null if the [Channel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getChannelOrNull(): Channel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the creator of the invite for,
     * returns null if the [User] isn't present or [inviterId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getInviter(): User? = inviterId?.let { supplier.getUserOrNull(it) }

    /**
     * Requests to get the user this invite was created for,
     * returns null if the [User] isn't present or [targetUserId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getTargetUser(): User? = targetUserId?.let { supplier.getUserOrNull(it) }

    /**
     * Requests to delete the invite.
     *
     * @param reason the reason showing up in the audit log
     */
    suspend fun delete(reason: String? = null) {
        kord.rest.invite.deleteInvite(data.code, reason)
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
