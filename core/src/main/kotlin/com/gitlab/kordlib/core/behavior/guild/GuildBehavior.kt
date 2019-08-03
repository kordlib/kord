package com.gitlab.kordlib.core.behavior.guild

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.Pagination
import com.gitlab.kordlib.core.`object`.PermissionOverwrite
import com.gitlab.kordlib.core.`object`.Unicode
import com.gitlab.kordlib.core.`object`.builder.ban.NewBanBuilder
import com.gitlab.kordlib.core.`object`.builder.channel.*
import com.gitlab.kordlib.core.`object`.builder.guild.EditGuildBuilder
import com.gitlab.kordlib.core.`object`.builder.role.NewRoleBuilder
import com.gitlab.kordlib.core.`object`.builder.role.SwapRolePositionsBuilder
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.TextChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.createMessage
import com.gitlab.kordlib.core.behavior.message.MessageBehavior
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.BeginGuildPruneRequest
import com.gitlab.kordlib.rest.json.request.GetGuildPruneRequest
import com.gitlab.kordlib.rest.json.request.ModifyCurrentUserNicknameRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GuildBehavior : Entity {

    suspend fun edit(builder: EditGuildBuilder): Nothing /*Guild*/ = TODO()

    suspend fun delete()= kord.rest.guild.deleteGuild(id.value)

    suspend fun leave() = kord.rest.user.leaveGuild(id.value)

    suspend fun getChannels(): Flow<Nothing/*Channel*/> = TODO()

    suspend fun createTextChannel(builder: NewTextChannelBuilder): Nothing /*TextChannel*/ = TODO()

    suspend fun createVoiceChannel(builder: NewVoiceChannelBuilder): Nothing /*VoiceChannel*/ = TODO()

    @KordPreview
    suspend fun createNewsChannel(builder: NewNewsChannelBuilder): Nothing /*NewsChannel*/ = TODO()

    suspend fun swapChannelPositions(builder: SwapChannelPositionsBuilder) {
        kord.rest.guild.modifyGuildChannelPosition(id.value, builder.toRequest())
    }

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

    suspend fun addRole(builder: NewRoleBuilder): Nothing = TODO()

    suspend fun kick(userId: Snowflake) {
        kord.rest.guild.deleteGuildMember(guildId = id.value, userId = userId.value)
    }

    suspend fun getBans(): Flow<Nothing /*Ban*/> = TODO()

    suspend fun getBan(userId: Snowflake): Nothing? /*Ban?*/ = TODO()

    suspend fun ban(userId: Snowflake, builder: NewBanBuilder) {
        kord.rest.guild.addGuildBan(guildId = id.value, userId = userId.value, ban = builder.toRequest())
    }

    suspend fun unBan(userId: Snowflake) {
        kord.rest.guild.deleteGuildBan(guildId = id.value, userId = userId.value)
    }

    suspend fun getRoles(): Flow<Nothing /*Role*/> = TODO()

    suspend fun createRole(builder: NewRoleBuilder): Nothing /*Role*/ = TODO()

    suspend fun swapRolePositions(builder: SwapRolePositionsBuilder): Flow<Nothing /*Role*/> = TODO()

    suspend fun getPruneCount(days: Int = 7): Int {
        return kord.rest.guild.getGuildPruneCount(id.value, GetGuildPruneRequest(days)).pruned
    }

    suspend fun prune(days: Int = 7): Int {
        return kord.rest.guild.beginGuildPrune(id.value, BeginGuildPruneRequest(days, true)).pruned!!
    }

    suspend fun getEmbedChannel(): GuildChannelBehavior<UpdateGuildChannelBuilder> {
        val response = kord.rest.guild.getGuildEmbed(id.value)
        val snowflake = Snowflake(response.channelId)
        return GuildChannelBehavior(snowflake, kord)
    }

    suspend fun getVanityUrl(): String? = kord.rest.guild.getVanityInvite(id.value).code

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) = object : GuildBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

suspend inline fun GuildBehavior.edit(builder: EditGuildBuilder.() -> Unit): Nothing =
        edit(EditGuildBuilder().apply(builder))

suspend inline fun GuildBehavior.createTextChannel(builder: NewTextChannelBuilder.() -> Unit): Nothing =
        createTextChannel(NewTextChannelBuilder().apply(builder))

suspend inline fun GuildBehavior.createVoiceChannel(builder: NewVoiceChannelBuilder.() -> Unit): Nothing =
        createVoiceChannel(NewVoiceChannelBuilder().apply(builder))

@KordPreview
suspend inline fun GuildBehavior.createNewsChannel(builder: NewNewsChannelBuilder.() -> Unit): Nothing =
        createNewsChannel(NewNewsChannelBuilder().apply(builder))

suspend inline fun GuildBehavior.swapChannelPositions(builder: SwapChannelPositionsBuilder.() -> Unit) =
        swapChannelPositions(SwapChannelPositionsBuilder().apply(builder))

suspend inline fun GuildBehavior.swapRolePositions(builder: SwapRolePositionsBuilder.() -> Unit) =
        swapRolePositions(SwapRolePositionsBuilder().apply(builder))
