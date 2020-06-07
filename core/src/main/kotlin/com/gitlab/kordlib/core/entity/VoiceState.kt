package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.*
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException

class VoiceState(
        val data: VoiceStateData,
        override val kord: Kord,
        override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
) : KordObject, Strategizable {

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

    /**
     * Requests to get the voice channel of this voice state through the [strategy].
     * Returns null if the [VoiceChannel] isn't present, or [channelId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getChannel(): VoiceChannel? = channelId?.let { strategy.supply(kord).getChannelOfOrNull(it) }

    /**
     * Requests to get the guild of this voice state through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    suspend fun getGuild(): Guild = strategy.supply(kord).getGuild(guildId)

    /**
     * Requests to get the guild of this voice state through the [strategy],
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = strategy.supply(kord).getGuildOrNull(guildId)

    /**
     * Requests to get the member that belongs to this voice state through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Member] wasn't present.
     */
    suspend fun getMember(): Member = strategy.supply(kord).getMember(guildId, userId)

    /**
     * Requests to get the member that belongs to this voice state through the [strategy],
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getMemberOrNull(): Member? = strategy.supply(kord).getMemberOrNull(guildId, userId)

    /**
     * Returns a new [VoiceState] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): VoiceState = VoiceState(data, kord, strategy)

}