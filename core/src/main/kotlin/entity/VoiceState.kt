package dev.kord.core.entity

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.channel.BaseVoiceChannelBehavior
import dev.kord.core.cache.data.VoiceStateData
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOfOrNull

public class VoiceState(
    public val data: VoiceStateData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {

    public val guildId: Snowflake get() = data.guildId

    public val channelId: Snowflake? get() = data.channelId

    public val userId: Snowflake get() = data.userId

    public val sessionId: String get() = data.sessionId

    public val isDeafened: Boolean get() = data.deaf

    public val isMuted: Boolean get() = data.mute

    public val isSelfDeafened: Boolean get() = data.selfDeaf

    public val isSelfMuted: Boolean get() = data.selfMute

    public val isSelfVideo: Boolean get() = data.selfVideo

    public val isSuppressed: Boolean get() = data.suppress

    public val requestToSpeakTimestamp: String? get() = data.requestToSpeakTimestamp

    /**
     * Whether this user is streaming using "Go Live".
     */
    public val isSelfSteaming: Boolean get() = data.selfStream.orElse(false)

    /**
     * Requests to get the voice channel of this voice state.
     * Returns null if the [VoiceChannel] isn't present, or [channelId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Use getChannelOrNull instead.", ReplaceWith("getChannelOrNull"), DeprecationLevel.ERROR)
    public suspend fun getChannel(): BaseVoiceChannelBehavior? = channelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Requests to get the voice channel, returns null if the [VoiceChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): BaseVoiceChannelBehavior? = channelId?.let { supplier.getChannelOfOrNull(it) }


    /**
     * Requests to get the guild of this voice state.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild of this voice state,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the member that belongs to this voice state.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Member] wasn't present.
     */
    public suspend fun getMember(): Member = supplier.getMember(guildId, userId)

    /**
     * Requests to get the member that belongs to this voice state,
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getMemberOrNull(): Member? = supplier.getMemberOrNull(guildId, userId)

    /**
     * Returns a new [VoiceState] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): VoiceState =
        VoiceState(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "VoiceState(data=$data, kord=$kord, supplier=$supplier)"
    }

}
