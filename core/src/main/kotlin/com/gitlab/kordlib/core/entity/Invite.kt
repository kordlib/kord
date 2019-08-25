package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.`object`.data.InviteData
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.channel.GuildChannel

data class Invite(val data: InviteData, override val kord: Kord) : KordObject {

    val code: String get() = data.code

    val channelId: Snowflake get() = Snowflake(data.channelId)

    val guildId: Snowflake get() = Snowflake(data.guildId!!)

    val channel: GuildChannelBehavior get() = GuildChannelBehavior(guildId, channelId, kord)

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getChannel(): GuildChannel = kord.getChannel(channelId) as GuildChannel

    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

    suspend fun delete(reason: String? = null) = kord.rest.invite.deleteInvite(data.code, reason)
}