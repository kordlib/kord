package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.*
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.core.entity.channel.VoiceChannel

data class VoiceState(val data: VoiceStateData, override val kord: Kord, val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy) : KordObject {

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

    /**
     * Whether this user is streaming using "Go Live".
     */
    val isSelfSteaming: Boolean get() = data.selfStream

    suspend fun getChannel(): VoiceChannel =  strategy.supply(kord).getChannelOf(channelId!!)

    suspend fun getGuild(): Guild = strategy.supply(kord).getGuild(guildId)

    suspend fun getMember(): User = strategy.supply(kord).getMember(guildId, userId)

    suspend fun getChannelOrNull(): VoiceChannel? = channelId?.let { strategy.supply(kord).getChannelOfOrNull(it) }

    suspend fun getGuildOrNull(): Guild? = strategy.supply(kord).getGuildOrNull(guildId)

    suspend fun getMemberOrNull(): User? = strategy.supply(kord).getMemberOrNull(guildId, userId)


    /**
     * returns a new [VoiceState] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    fun withStrategy(strategy: EntitySupplyStrategy) = VoiceState(data, kord,strategy)

}