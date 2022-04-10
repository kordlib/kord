package dev.kord.common.entity

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * A [_Stage Instance_](https://discord.com/developers/docs/resources/stage-instance) holds information about a live
 * stage.
 *
 * @property id The id of this Stage instance.
 * @property guildId The guild id of the associated Stage channel.
 * @property channelId The id of the associated Stage channel.
 * @property topic The topic of the Stage instance.
 * @property privacyLevel The [privacy level][StageInstancePrivacyLevel] of the Stage instance.
 * @property discoverableDisabled Whether or not Stage Discovery is disabled.
 * @property guildScheduledEventId The id of the scheduled event for this Stage instance.
 */
@Serializable
public data class DiscordStageInstance(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake,
    val topic: String,
    @SerialName("privacy_level")
    val privacyLevel: StageInstancePrivacyLevel,
    @Deprecated("Stages are no longer discoverable")
    @SerialName("discoverable_disabled")
    val discoverableDisabled: Boolean,
    @SerialName("guild_scheduled_event_id")
    val guildScheduledEventId: Snowflake?,
)

/**
 * Privacy level of a [DiscordStageInstance].
 */
@Serializable(with = StageInstancePrivacyLevel.Serializer::class)
public sealed class StageInstancePrivacyLevel(public val value: Int) {

    /**
     * The Stage instance is visible publicly, such as on Stage Discovery.
     */
    @Deprecated("Stages are no longer discoverable")
    public object Public : StageInstancePrivacyLevel(1)

    /**
     * The Stage instance is visible to only guild members.
     */
    public object GuildOnly : StageInstancePrivacyLevel(2)

    /**
     * An unknown privacy level.
     */
    public class Unknown(value: Int) : StageInstancePrivacyLevel(value)

    public companion object Serializer : KSerializer<StageInstancePrivacyLevel> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StageInstancePrivacyLevel", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): StageInstancePrivacyLevel {
            @Suppress("DEPRECATION")
            return when (val value = decoder.decodeInt()) {
                1 -> Public
                2 -> GuildOnly
                else -> Unknown(value)
            }
        }

        override fun serialize(encoder: Encoder, value: StageInstancePrivacyLevel): Unit = encoder.encodeInt(value.value)

    }
}
