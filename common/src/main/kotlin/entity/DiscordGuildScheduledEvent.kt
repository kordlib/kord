package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray

/**
 * Representation of a [Guild Scheduled Event Structure](ADD LINK).
 *
 * @property id the id of the event
 * @property guildId the id of the guild the event is on
 * @property channelId the id of the channel the event is in
 * @property name the name of the event
 * @property description the description of the event
 * @property image the image of the event
 * @property scheduledStartTime the [Instant] in which the event will start
 * @property scheduledEndTime the [Instant] in which the event wil stop, if any
 * @property privacyLevel the [event privacy level][StageInstancePrivacyLevel]
 * @property status the [event status][GuildScheduledEventStatus]
 * @property entityType the [ScheduledEntityType] of the event
 * @property entityId entity id
 * @property entityMetadata [metadata][GuildScheduledEventEntityMetadata] for the event
 * @property skuIds sku ids
 * @property skus skus
 * @property userCount users subscribed to the event
 */
@Serializable
data class DiscordGuildScheduledEvent(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val channelId: Snowflake?,
    val name: String,
    val description: Optional<String> = Optional.Missing(),
    val image: String?,
    @SerialName("scheduled_start_time")
    val scheduledStartTime: Instant,
    @SerialName("scheduled_end_time")
    val scheduledEndTime: Instant?,
    @SerialName("privacy_level")
    val privacyLevel: StageInstancePrivacyLevel,
    val status: GuildScheduledEventStatus,
    val type: ScheduledEntityType,
    @SerialName("entity_id")
    val entityId: Snowflake?,
    @SerialName("entity_type")
    val entityType: ScheduledEntityType,
    @SerialName("entity_metadata")
    val entityMetadata: GuildScheduledEventEntityMetadata,
    @SerialName("sku_ids")
    val skuIds: List<Snowflake>,
    val skus: JsonArray,
    @SerialName("user_count")
    val userCount: Int
)

@Serializable(with = ScheduledEntityType.Serializer::class)
sealed class ScheduledEntityType(val value: Int) {
    object None : ScheduledEntityType(0)
    object StageInstance : ScheduledEntityType(1)
    object Voice : ScheduledEntityType(2)
    object Location : ScheduledEntityType(3)
    class Unknown(value: Int) : ScheduledEntityType(value)

    companion object Serializer : KSerializer<ScheduledEntityType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ScheduledEntityType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ScheduledEntityType {
            return when (val value = decoder.decodeInt()) {
                0 -> None
                1 -> StageInstance
                2 -> Voice
                3 -> Location
                else -> Unknown(value)
            }
        }

        override fun serialize(encoder: Encoder, value: ScheduledEntityType) = encoder.encodeInt(value.value)

    }
}

@Serializable(with = GuildScheduledEventStatus.Serializer::class)
sealed class GuildScheduledEventStatus(val value: Int) {
    object Scheduled : GuildScheduledEventStatus(1)
    object Active : GuildScheduledEventStatus(2)
    object Completed : GuildScheduledEventStatus(3)
    object Cancelled : GuildScheduledEventStatus(4)
    class Unknown(value: Int) : GuildScheduledEventStatus(value)

    companion object Serializer : KSerializer<GuildScheduledEventStatus> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GuildScheduledEventStatus", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): GuildScheduledEventStatus {
            return when (val value = decoder.decodeInt()) {
                1 -> Scheduled
                2 -> Active
                3 -> Completed
                4 -> Cancelled
                else -> Unknown(value)
            }
        }

        override fun serialize(encoder: Encoder, value: GuildScheduledEventStatus) = encoder.encodeInt(value.value)

    }
}

/**
 * Entity metadata for [DiscordGuildScheduledEvent].
 *
 * @property speakerIds the speakers of the stage channel
 * @property location location of the event
 */
@Serializable
data class GuildScheduledEventEntityMetadata(
    val speakerIds: Optional<List<Snowflake>> = Optional.Missing(),
    val location: Optional<String> = Optional.Missing()
)
