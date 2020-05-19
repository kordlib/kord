package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.core.toSnowflakeOrNull

data class VoiceState(val data: VoiceStateData, override val kord: Kord) : KordObject {

    val guildId: Snowflake? get() = data.guildId.toSnowflakeOrNull()

    val channelId: Snowflake? get() = data.channelId.toSnowflakeOrNull()

    val userId: Snowflake get() = Snowflake(data.userId)

    val sessionId: String get() = data.sessionId

    val isDeafened: Boolean get() = data.deaf

    val isMuted: Boolean get() = data.mute

    val isSelfDeafened: Boolean get() = data.selfDeaf

    val isSelfMuted: Boolean get() = data.selfMute

    val isSuppressed: Boolean get() = data.suppress

    /**
     * Whether this user is streaming using "Go Live".
     */
    val isSelfSteaming: Boolean get() = data.selfStream

    suspend fun getChannel(): VoiceChannel? = channelId?.let { kord.getChannel(it) } as? VoiceChannel

}