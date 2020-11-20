package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import com.gitlab.kordlib.core.toSnowflakeOrNull

class VoiceState(
        val data: VoiceStateData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {

    val guildId: Snowflake get() = data.guildId

    val channelId: Snowflake? get() = data.channelId

    val userId: Snowflake get() = data.userId

    val sessionId: String get() = data.sessionId

    val isDeafened: Boolean get() = data.deaf

    val isMuted: Boolean get() = data.mute

    val isSelfDeafened: Boolean get() = data.selfDeaf

    val isSelfMuted: Boolean get() = data.selfMute

    val isSuppressed: Boolean get() = data.suppress

    /**
     * Whether this user is streaming using "Go Live".
     */
    val isSelfSteaming: Boolean get() = data.selfStream.orElse(false)

    /**
     * Requests to get the voice channel of this voice state.
     * Returns null if the [VoiceChannel] isn't present, or [channelId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("User getChannelOrNull instead.", ReplaceWith("getChannelOrNull"), DeprecationLevel.ERROR)
    suspend fun getChannel(): VoiceChannel? = channelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Requests to get the voice channel through the [strategy],
     * returns null if the [VoiceChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getChannelOrNull(): VoiceChannel? = channelId?.let { supplier.getChannelOfOrNull(it) }


    /**
     * Requests to get the guild of this voice state.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild of this voice state,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the member that belongs to this voice state.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Member] wasn't present.
     */
    suspend fun getMember(): Member = supplier.getMember(guildId, userId)

    /**
     * Requests to get the member that belongs to this voice state,
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getMemberOrNull(): Member? = supplier.getMemberOrNull(guildId, userId)

    /**
     * Returns a new [VoiceState] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): VoiceState = VoiceState(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "VoiceState(data=$data, kord=$kord, supplier=$supplier)"
    }

}