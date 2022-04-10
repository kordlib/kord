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
import kotlinx.datetime.Instant

public class VoiceState(
    public val data: VoiceStateData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {

    /** The guild id this voice state is for. */
    public val guildId: Snowflake get() = data.guildId

    /** The channel id this user is connected to. */
    public val channelId: Snowflake? get() = data.channelId

    /** The user id this voice state is for. */
    public val userId: Snowflake get() = data.userId

    /** The session id for this voice state. */
    public val sessionId: String get() = data.sessionId

    /** Whether this user is deafened by the server. */
    public val isDeafened: Boolean get() = data.deaf

    /** Whether this user is muted by the server. */
    public val isMuted: Boolean get() = data.mute

    /** Whether this user is locally deafened. */
    public val isSelfDeafened: Boolean get() = data.selfDeaf

    /** Whether this user is locally muted. */
    public val isSelfMuted: Boolean get() = data.selfMute

    /** Whether this user is streaming using "Go Live". */
    public val isSelfStreaming: Boolean get() = data.selfStream.discordBoolean

    /** Whether this user's camera is enabled. */
    public val isSelfVideo: Boolean get() = data.selfVideo

    /** Whether this user is muted by the current user. */
    public val isSuppressed: Boolean get() = data.suppress

    /** The [Instant] at which the user requested to speak. */
    public val requestToSpeakTimestamp: Instant? get() = data.requestToSpeakTimestamp

    /** Discord does not support anger detection. */
    @Deprecated("I can't see any steam...", ReplaceWith("this.isSelfStreaming"), DeprecationLevel.ERROR)
    public val isSelfSteaming: Boolean
        get() = isSelfStreaming

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
