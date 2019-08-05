package com.gitlab.kordlib.core.behavior.guild

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.Pagination
import com.gitlab.kordlib.core.`object`.builder.ban.NewBanBuilder
import com.gitlab.kordlib.core.`object`.builder.channel.*
import com.gitlab.kordlib.core.`object`.builder.guild.EditGuildBuilder
import com.gitlab.kordlib.core.`object`.builder.role.NewRoleBuilder
import com.gitlab.kordlib.core.`object`.builder.role.SwapRolePositionsBuilder
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.BeginGuildPruneRequest
import com.gitlab.kordlib.rest.json.request.GetGuildPruneRequest
import com.gitlab.kordlib.rest.json.request.ModifyCurrentUserNicknameRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The behavior of a [Discord Guild](https://discordapp.com/developers/docs/resources/guild).
 */
interface GuildBehavior : Entity {

    /**
     * Requests to delete this guild.
     */
    suspend fun delete()= kord.rest.guild.deleteGuild(id.value)

    /**
     * Requests to leave this guild.
     */
    suspend fun leave() = kord.rest.user.leaveGuild(id.value)

    /**
     * Requests to get all channels in this guild.
     */
    suspend fun getChannels(): Flow<Nothing/*Channel*/> = TODO()

    /**
     * Requests to get all members in this guild.
     */
    @ExperimentalCoroutinesApi
    suspend fun getMembers(): Flow<Nothing /*Member*/> {
        @Suppress("UNREACHABLE_CODE")
        return Pagination.after(1000, { it.user!!.id }) { position, size ->
            kord.rest.guild.getGuildMembers(id.value, position, size)
        }.map { TODO() }
    }

    /**
     * Requests to get the member represented by the [userId], if present.
     */
    suspend fun getMemberById(userId: Snowflake): Nothing /*Member?*/ = TODO()

    //TODO addGuildMember?

    /**
     *  Requests to change the nickname of the bot in this guild, passing `null` will remove it.
     */
    suspend fun modifySelfNickname(newNickName: String?) : String {
        kord.rest.guild.modifyCurrentUserNickname(id.value, ModifyCurrentUserNicknameRequest(newNickName))
        TODO("https://gitlab.com/kordlib/kord/issues/26")
    }

    /**
     * Requests to kick the given [userId].
     */
    suspend fun kick(userId: Snowflake) {
        kord.rest.guild.deleteGuildMember(guildId = id.value, userId = userId.value)
    }

    /**
     * Requests to get all bans for this guild.
     */
    suspend fun getBans(): Flow<Nothing /*Ban*/> = TODO()

    /**
     * Requests to get the ban for the given [userId], if present.
     */
    suspend fun getBan(userId: Snowflake): Nothing? /*Ban?*/ = TODO()

    /**
     * Requests to unban the given [userId].
     */
    suspend fun unBan(userId: Snowflake) {
        kord.rest.guild.deleteGuildBan(guildId = id.value, userId = userId.value)
    }

    /**
     * Requests to get all roles in the guild.
     */
    suspend fun getRoles(): Flow<Nothing /*Role*/> = TODO()

    /**
     * Requests to get the amount of users that would be pruned in this guild. A user is pruned
     * if they have not been seen within the given [days] and don't have a [Role] assigned in this guild.
     */
    suspend fun getPruneCount(days: Int = 7): Int {
        return kord.rest.guild.getGuildPruneCount(id.value, GetGuildPruneRequest(days)).pruned
    }

    /**
     * Requests to prune users in this guild. A user is pruned if they have not been seen within
     * the given [days] and don't have a [Role] assigned in this guild.
     */
    suspend fun prune(days: Int = 7): Int {
        return kord.rest.guild.beginGuildPrune(id.value, BeginGuildPruneRequest(days, true)).pruned!!
    }

    /**
     * Requests to get the embedded channel in this guild, if present.
     */
    suspend fun getEmbedChannel(): Nothing /*GuildChannel?*/ = TODO()

    /**
     * Requests to get the vanity url of this guild, if present.
     */
    suspend fun getVanityUrl(): String? = kord.rest.guild.getVanityInvite(id.value).code

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
suspend inline fun GuildBehavior.edit(builder: EditGuildBuilder.() -> Unit): Nothing /*Guild*/ = TODO()

/**
 * Requests to create a new text channel.
 *
 * @return The created [TextChannel].
 */
suspend inline fun GuildBehavior.createTextChannel(builder: NewTextChannelBuilder.() -> Unit): Nothing /*TextChannel*/ = TODO()

/**
 * Requests to create a new voice channel.
 *
 * @return The created [VoiceChannel].
 */
suspend inline fun GuildBehavior.createVoiceChannel(builder: NewVoiceChannelBuilder.() -> Unit): Nothing /*VoiceChannel*/ = TODO()

/**
 * Requests to create a new news channel.
 *
 * @return The created [NewsChannel].
 */
@KordPreview
suspend inline fun GuildBehavior.createNewsChannel(builder: NewNewsChannelBuilder.() -> Unit): Nothing /*NewsChannel*/ = TODO()

/**
 * Requests to swap positions of channels in this guild.
 */
suspend inline fun GuildBehavior.swapChannelPositions(builder: SwapChannelPositionsBuilder.() -> Unit) {
    kord.rest.guild.modifyGuildChannelPosition(id.value, SwapChannelPositionsBuilder().apply(builder).toRequest())
}

/**
 * Requests to swap positions of roles in this guild.
 */
suspend inline fun GuildBehavior.swapRolePositions(builder: SwapRolePositionsBuilder.() -> Unit): Flow<Nothing /*Role*/>  = TODO()

/**
 * Requests to add a new role to this guild.
 *
 * @return The created [Role].
 */
suspend inline fun GuildBehavior.addRole(builder: NewRoleBuilder.() -> Unit): Nothing /*Role*/ = TODO()

/**
 * Requests to ban the given [userId] in this guild.
 */
suspend inline fun GuildBehavior.ban(userId: Snowflake, builder: NewBanBuilder.() -> Unit) {
    kord.rest.guild.addGuildBan(guildId = id.value, userId = userId.value, ban = NewBanBuilder().apply(builder).toRequest())
}