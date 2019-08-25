package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.`object`.data.VoiceStateData
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.core.toSnowflakeOrNull

data class VoiceState(val data: VoiceStateData, override val kord: Kord) : KordObject {

    val guildId: Snowflake get() = Snowflake(data.guildId)

    val channelId: Snowflake? get() = data.channelId.toSnowflakeOrNull()

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    val userId: Snowflake get() = Snowflake(data.userId)

    val member: MemberBehavior get() = MemberBehavior(guildId, userId, kord)

    val sessionId: String get() = data.sessionId

    val isDeafened: Boolean get() = data.deaf

    val isMuted: Boolean get() = data.mute

    val isSelfDeafened: Boolean get() = data.selfDeaf

    val isSelfMuted: Boolean get() = data.selfMute

    val isSuppressed: Boolean get() = data.suppress

    suspend fun getChannel(): VoiceChannel? = channelId?.let { kord.getChannel(it) } as? VoiceChannel

    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

    suspend fun getMember(): User = kord.getMember(guildId, userId)!!

}