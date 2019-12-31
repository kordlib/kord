package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.builder.ban.BanCreateBuilder
import com.gitlab.kordlib.core.builder.channel.GuildChannelPositionModifyBuilder
import com.gitlab.kordlib.core.builder.channel.NewsChannelCreateBuilder
import com.gitlab.kordlib.core.builder.channel.TextChannelCreateBuilder
import com.gitlab.kordlib.core.builder.channel.VoiceChannelCreateBuilder
import com.gitlab.kordlib.core.builder.guild.GuildModifyBuilder
import com.gitlab.kordlib.core.builder.role.RoleCreateBuilder
import com.gitlab.kordlib.core.builder.role.RolePositionsModifyBuilder
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.catchNotFound
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.paginateForwards
import com.gitlab.kordlib.core.sorted
import com.gitlab.kordlib.rest.json.request.CurrentUserNicknameModifyRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * The behavior of a [Discord Guild](https://discordapp.com/developers/docs/resources/guild).
 */
interface GuildBehavior : Entity {

    /**
     * Requests to get all bans for this guild.
     */
    val bans: Flow<Ban>
        get() = flow {
            for (guildBan in kord.rest.guild.getGuildBans(id.value)) {
                val data = BanData.from(guildBan)

                emit(Ban(data, kord))
            }
        }

    /**
     * Requests to get all channels in this guild in an unspecified order, call [sorted] to get a consistent order.
     */
    val channels: Flow<GuildChannel>
        get() = flow<GuildChannel /*Kotlin compiler bug, don't remove*/> {
            for (response in kord.rest.guild.getGuildChannels(id.value)) {
                val data = ChannelData.from(response)
                val channel = Channel.from(data, kord)

                if (channel is GuildChannel) {
                    emit(channel)
                }
            }
        }

    /**
     * Requests to get the cached presences of this guild, if cached.
     */
    val presences: Flow<Presence>
        get() =
            kord.cache.find<PresenceData> { PresenceData::guildId eq id.longValue }
                    .asFlow()
                    .map { Presence(it, kord) }
    /**
     * Requests to get all members in this guild.
     *
     * Unrestricted consumption of the returned [Flow] is a potentially performance intensive operation, it is thus recommended
     * to limit the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions that limit the amount
     * of messages requested.
     *
     * ```kotlin
     *  guild.members.first { it.displayName == targetName }
     * ```
     */

    val members: Flow<Member>
        get() = paginateForwards(batchSize = 10000, idSelector = { it.user!!.id }) { position ->
            kord.rest.guild.getGuildMembers(id.value, position, 1000)
        }.map {
            val memberData = MemberData.from(it.user!!.id, id.value, it)
            val userData = UserData.from(it.user!!)

            Member(memberData, userData, kord)
        }

    /**
     * Requests to get the voice regions for this guild.
     */
    val regions: Flow<Region>
        get() = flow {
            for (response in kord.rest.guild.getGuildVoiceRegions(id.value)) {
                val data = RegionData.from(response)

                emit(Region(data, kord))
            }
        }

    /**
     * Requests to get all roles in the guild.
     */
    val roles: Flow<Role>
        get() = flow {
            for (response in kord.rest.guild.getGuildRoles(id.value)) {
                val data = RoleData.from(id.value, response)

                emit(Role(data, kord))
            }
        }

    /**
     * Requests to get the cached voice states of this guild, if cached.
     */
    val voiceStates: Flow<VoiceState>
        get() = kord.cache
                .find<VoiceStateData> { VoiceStateData::guildId eq id.longValue }
                .asFlow()
                .map { VoiceState(it, kord) }

    /**
     * Requests to get this behavior as a [Guild].
     */
    suspend fun asGuild(): Guild = kord.getGuild(id)!!

    /**
     * Requests to delete this guild.
     */
    suspend fun delete() = kord.rest.guild.deleteGuild(id.value)

    /**
     * Requests to leave this guild.
     */
    suspend fun leave() = kord.rest.user.leaveGuild(id.value)

    /**
     * Requests to get the member represented by the [userId], if present.
     */
    suspend fun getMember(userId: Snowflake): Member? = kord.getMember(id, userId)

    //TODO addGuildMember?

    /**
     *  Requests to change the nickname of the bot in this guild, passing `null` will remove it.
     */
    suspend fun modifySelfNickname(newNickName: String?): String {
        return kord.rest.guild.modifyCurrentUserNickname(id.value, CurrentUserNicknameModifyRequest(newNickName))
    }

    /**
     * Requests to kick the given [userId].
     */
    suspend fun kick(userId: Snowflake) {
        kord.rest.guild.deleteGuildMember(guildId = id.value, userId = userId.value)
    }

    /**
     * Requests to get the ban for the given [userId], if present.
     */
    suspend fun getBan(userId: Snowflake): Ban? {
        val response = catchNotFound { kord.rest.guild.getGuildBan(id.value, userId.value) } ?: return null
        val data = BanData.from(response)

        return Ban(data, kord)
    }

    /**
     * Requests to unban the given [userId].
     */
    suspend fun unBan(userId: Snowflake) {
        kord.rest.guild.deleteGuildBan(guildId = id.value, userId = userId.value)
    }

    /**
     * Requests to get the amount of users that would be pruned in this guild.
     *
     * A user is pruned if they have not been seen within the given [days]
     * and don't have a [Role] assigned in this guild.
     */
    suspend fun getPruneCount(days: Int = 7): Int {
        return kord.rest.guild.getGuildPruneCount(id.value, days).pruned
    }

    /**
     * Requests to prune users in this guild.
     *
     * A user is pruned if they have not been seen within the given [days]
     * and don't have a [Role] assigned in this guild.
     */
    suspend fun prune(days: Int = 7): Int {
        return kord.rest.guild.beginGuildPrune(id.value, days, true).pruned!!
    }

    /**
     * Requests to get the vanity url of this guild, if present.
     */
    suspend fun getVanityUrl(): String? {
        val identifier = kord.rest.guild.getVanityInvite(id.value).code ?: return null
        return "https://discord.gg/$identifier"
    }

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) = object : GuildBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

/**
 * Requests to edit this guild.
 *
 * @return The edited [Guild].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.edit(builder: GuildModifyBuilder.() -> Unit): Guild {
    val builder = GuildModifyBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.guild.modifyGuild(id.value, request, reason)
    val data = GuildData.from(response)

    return Guild(data, kord)
}

/**
 * Requests to create a new text channel.
 *
 * @return The created [TextChannel].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.createTextChannel(builder: TextChannelCreateBuilder.() -> Unit): TextChannel {
    val builder = TextChannelCreateBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.guild.createGuildChannel(id.value, request, reason)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as TextChannel
}

/**
 * Requests to create a new voice channel.
 *
 * @return The created [VoiceChannel].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.createVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): VoiceChannel {
    val builder = VoiceChannelCreateBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.guild.createGuildChannel(id.value, request, reason)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as VoiceChannel
}

/**
 * Requests to create a new news channel.
 *
 * @return The created [NewsChannel].
 */
@KordPreview
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.createNewsChannel(builder: NewsChannelCreateBuilder.() -> Unit): NewsChannel {
    val builder = NewsChannelCreateBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.guild.createGuildChannel(id.value, request, reason)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as NewsChannel
}

/**
 * Requests to swap positions of channels in this guild.
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.swapChannelPositions(builder: GuildChannelPositionModifyBuilder.() -> Unit) {
    val builder = GuildChannelPositionModifyBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    kord.rest.guild.modifyGuildChannelPosition(id.value, request, reason)
}

/**
 * Requests to swap positions of roles in this guild.
 *
 * This request will execute regardless of the consumption of the return value.
 *
 * @return the roles of this guild after the update in an unspecified order, call [sorted] to get a consistent order.
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.swapRolePositions(builder: RolePositionsModifyBuilder.() -> Unit): Flow<Role> {
    val builder = RolePositionsModifyBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.guild.modifyGuildRolePosition(id.value, request, reason)
    return response.asFlow().map { RoleData.from(id.value, it) }.map { Role(it, kord) }

}

/**
 * Requests to add a new role to this guild.
 *
 * @return The created [Role].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.addRole(builder: RoleCreateBuilder.() -> Unit): Role {
    val builder = RoleCreateBuilder().also(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.guild.createGuildRole(id.value, request, reason)
    val data = RoleData.from(id.value, response)

    return Role(data, kord)
}

/**
 * Requests to ban the given [userId] in this guild.
 */
suspend inline fun GuildBehavior.ban(userId: Snowflake, builder: BanCreateBuilder.() -> Unit) {
    kord.rest.guild.addGuildBan(guildId = id.value, userId = userId.value, ban = BanCreateBuilder().apply(builder).toRequest())
}