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

interface GuildBehavior : Entity {

    suspend fun delete()= kord.rest.guild.deleteGuild(id.value)

    suspend fun leave() = kord.rest.user.leaveGuild(id.value)

    suspend fun getChannels(): Flow<Nothing/*Channel*/> = TODO()

    @ExperimentalCoroutinesApi
    suspend fun getMembers(): Flow<Nothing /*Member*/> {
        @Suppress("UNREACHABLE_CODE")
        return Pagination.after(1000, { it.user!!.id }) { position, size ->
            kord.rest.guild.getGuildMembers(id.value, position, size)
        }.map { TODO() }
    }

    //TODO addGuildMember?

    suspend fun modifySelfNickname(newNickName: String) {
        kord.rest.guild.modifyCurrentUserNickname(id.value, ModifyCurrentUserNicknameRequest(newNickName))
    }

    suspend fun kick(userId: Snowflake) {
        kord.rest.guild.deleteGuildMember(guildId = id.value, userId = userId.value)
    }

    suspend fun getBans(): Flow<Nothing /*Ban*/> = TODO()

    suspend fun getBan(userId: Snowflake): Nothing? /*Ban?*/ = TODO()

    suspend fun unBan(userId: Snowflake) {
        kord.rest.guild.deleteGuildBan(guildId = id.value, userId = userId.value)
    }

    suspend fun getRoles(): Flow<Nothing /*Role*/> = TODO()

    suspend fun getPruneCount(days: Int = 7): Int {
        return kord.rest.guild.getGuildPruneCount(id.value, GetGuildPruneRequest(days)).pruned
    }

    suspend fun prune(days: Int = 7): Int {
        return kord.rest.guild.beginGuildPrune(id.value, BeginGuildPruneRequest(days, true)).pruned!!
    }

    suspend fun getEmbedChannel(): GuildChannelBehavior {
        val response = kord.rest.guild.getGuildEmbed(id.value)
        val channelId = Snowflake(response.channelId)
        return GuildChannelBehavior(id = channelId, guildId = id, kord = kord)
    }

    suspend fun getVanityUrl(): String? = kord.rest.guild.getVanityInvite(id.value).code

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) = object : GuildBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

suspend inline fun GuildBehavior.edit(builder: EditGuildBuilder.() -> Unit): Nothing /*Guild*/ = TODO()

suspend inline fun GuildBehavior.createTextChannel(builder: NewTextChannelBuilder.() -> Unit): Nothing /*TextChannel*/ = TODO()

suspend inline fun GuildBehavior.createVoiceChannel(builder: NewVoiceChannelBuilder.() -> Unit): Nothing /*VoiceChannel*/ = TODO()

@KordPreview
suspend inline fun GuildBehavior.createNewsChannel(builder: NewNewsChannelBuilder.() -> Unit): Nothing /*NewsChannel*/ = TODO()

suspend inline fun GuildBehavior.swapChannelPositions(builder: SwapChannelPositionsBuilder.() -> Unit) {
    kord.rest.guild.modifyGuildChannelPosition(id.value, SwapChannelPositionsBuilder().apply(builder).toRequest())
}

suspend inline fun GuildBehavior.swapRolePositions(builder: SwapRolePositionsBuilder.() -> Unit): Flow<Nothing /*Role*/>  = TODO()

suspend inline fun GuildBehavior.addRole(builder: NewRoleBuilder.() -> Unit): Nothing /*Role*/ = TODO()

suspend inline fun GuildBehavior.ban(userId: Snowflake, builder: NewBanBuilder.() -> Unit) {
    kord.rest.guild.addGuildBan(guildId = id.value, userId = userId.value, ban = NewBanBuilder().apply(builder).toRequest())
}