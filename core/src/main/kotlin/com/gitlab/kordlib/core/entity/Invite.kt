package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.`object`.data.InviteData
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.toSnowflakeOrNull

/**
 * An instance of a [Discord Invite](https://discordapp.com/developers/docs/resources/invite).
 */
data class Invite(val data: InviteData, override val kord: Kord) : KordObject {

    /**
     * The unique code of this invite.
     */
    val code: String get() = data.code

    /**
     * The id of the channel this invite is associated to.
     */
    val channelId: Snowflake get() = Snowflake(data.channelId)

    /**
     * The id of the guild this invite is associated to.
     */
    val guildId: Snowflake get() = Snowflake(data.guildId!!)

    /**
     * The id of the target user this invite is associated to, if present.
     */
    val targetUserId: Snowflake? get() = data.targetUserId.toSnowflakeOrNull()

    /**
     * The behavior of the channel this invite is associated to.
     */
    val channel: GuildChannelBehavior get() = GuildChannelBehavior(guildId, channelId, kord)

    /**
     * The behavior of the guild this invite is associated to.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * The user behavior of the target user this invite is associated to, if present.
     */
    val targetUser: UserBehavior? get() = targetUserId?.let { UserBehavior(it, kord) }

    /**
     * Approximate count of members in the channel this invite is associated to, if present.
     */
    val approximateMemberCount: Int? get() = data.approximateMemberCount

    /**
     * Approximate count of members online in the channel this invite is associated to, if present. (only present when the target user isn't null)
     */
    val approximatePresenceCount: Int? get() = data.approximatePresenceCount

    /**
     * Requests to get the channel this invite is associated to.
     */
    suspend fun getChannel(): GuildChannel = kord.getChannel(channelId) as GuildChannel

    /**
     * Requests to get the guild this invite is associated to.
     */
    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

    /**
     * Requests to get the target user this invite is associated to, if present.
     */
    suspend fun getTargetUser(): User? = targetUserId?.let { kord.getUser(it) }

    /**
     * Requests to delete the invite.
     */
    suspend fun delete(reason: String? = null) = kord.rest.invite.deleteInvite(data.code, reason)
}