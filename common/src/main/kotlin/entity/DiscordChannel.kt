package dev.kord.common.entity

import dev.kord.common.entity.Permission.ManageThreads
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInMinutesSerializer
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.DeprecationLevel.WARNING
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * A representation of a [Discord Channel Structure](https://discord.com/developers/docs/resources/channel).
 *
 * @param id The id of the channel.
 * @param type the Type of channel.
 * @param guildId the id of the guild.
 * @param position The sorting position of the channel.
 * @param permissionOverwrites The explicit permission overwrite for members and roles.
 * @param name The name of the channel.
 * @param topic The channel topic.
 * @param nsfw Whether the channel is nsfw.
 * @param lastMessageId The id of the last message sent in this channel (may not point to an existing or valid message).
 * @param bitrate The bitrate (in bits) of the voice channel.
 * @param userLimit The user limit of the voice channel.
 * @param rateLimitPerUser amount of time a user has to wait before sending another message; bots,
 * as well as users with the permission [Permission.ManageMessages] or [Permission.ManageChannels] are unaffected.
 * @param recipients The recipients of the DM.
 * @param icon The icon hash.
 * @param ownerId The id of DM creator.
 * @param applicationId The application id of the group DM creator if it is bot-created.
 * @param parentId The id of the parent category for a channel.
 * @param lastPinTimestamp When the last pinned message was pinned.
 */
@Serializable
public data class DiscordChannel(
    val id: Snowflake,
    val type: ChannelType,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val position: OptionalInt = OptionalInt.Missing,
    @SerialName("permission_overwrites")
    val permissionOverwrites: Optional<List<Overwrite>> = Optional.Missing(),
    val name: Optional<String?> = Optional.Missing(),
    val topic: Optional<String?> = Optional.Missing(),
    val nsfw: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("last_message_id")
    val lastMessageId: OptionalSnowflake? = OptionalSnowflake.Missing,
    val bitrate: OptionalInt = OptionalInt.Missing,
    @SerialName("user_limit")
    val userLimit: OptionalInt = OptionalInt.Missing,
    @SerialName("rate_limit_per_user")
    val rateLimitPerUser: Optional<DurationInSeconds> = Optional.Missing(),
    val recipients: Optional<List<DiscordUser>> = Optional.Missing(),
    val icon: Optional<String?> = Optional.Missing(),
    @SerialName("owner_id")
    val ownerId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("application_id")
    val applicationId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("parent_id")
    val parentId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("last_pin_timestamp")
    val lastPinTimestamp: Optional<Instant?> = Optional.Missing(),
    @SerialName("rtc_region")
    val rtcRegion: Optional<String?> = Optional.Missing(),
    @SerialName("video_quality_mode")
    val videoQualityMode: Optional<VideoQualityMode> = Optional.Missing(),
    val permissions: Optional<Permissions> = Optional.Missing(),
    @SerialName("message_count")
    val messageCount: OptionalInt = OptionalInt.Missing,
    @SerialName("member_count")
    val memberCount: OptionalInt = OptionalInt.Missing,
    @SerialName("thread_metadata")
    val threadMetadata: Optional<DiscordThreadMetadata> = Optional.Missing(),
    @SerialName("default_auto_archive_duration")
    val defaultAutoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing(),
    val member: Optional<DiscordThreadMember> = Optional.Missing()
)

@Serializable(with = ChannelType.Serializer::class)
public sealed class ChannelType(public val value: Int) {

    /** The default code for unknown values. */
    public class Unknown(value: Int) : ChannelType(value)

    /** A text channel within a server. */
    public object GuildText : ChannelType(0)

    /** A direct message between users. */
    public object DM : ChannelType(1)

    /** A voice channel within a server. */
    public object GuildVoice : ChannelType(2)

    /** A direct message between multiple users. */
    public object GroupDM : ChannelType(3)

    /**
     * An [organizational category](https://support.discord.com/hc/en-us/articles/115001580171-Channel-Categories-101)
     * that contains up to 50 channels.
     */
    public object GuildCategory : ChannelType(4)

    /**
     * A channel that
     * [users can follow and crosspost into their own server](https://support.discord.com/hc/en-us/articles/360032008192).
     */
    public object GuildNews : ChannelType(5)

    /** A channel in which game developers can sell their game on Discord. */
    @Deprecated(
        """
        Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
        removed on March 10, 2022.
        
        See https://support-dev.discord.com/hc/en-us/articles/4414590563479 for more information.
        """,
        level = WARNING,
    )
    public object GuildStore : ChannelType(6)

    /** A temporary sub-channel within a [GuildNews] channel. */
    public object PublicNewsThread : ChannelType(10)

    /** A temporary sub-channel within a [GuildText] channel. */
    public object PublicGuildThread : ChannelType(11)

    /**
     * A temporary sub-channel within a [GuildText] channel that is only viewable by those invited and those with the
     * [ManageThreads] permission.
     */
    public object PrivateThread : ChannelType(12)

    /**
     * A voice channel for
     * [hosting events with an audience](https://support.discord.com/hc/en-us/articles/1500005513722).
     */
    public object GuildStageVoice : ChannelType(13)

    /**
     * The channel in a [hub](https://support.discord.com/hc/en-us/articles/4406046651927-Discord-Student-Hubs-FAQ)
     * containing the listed servers.
     */
    public object GuildDirectory : ChannelType(14)


    internal object Serializer : KSerializer<ChannelType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ChannelType = when (val code = decoder.decodeInt()) {
            0 -> GuildText
            1 -> DM
            2 -> GuildVoice
            3 -> GroupDM
            4 -> GuildCategory
            5 -> GuildNews
            6 -> @Suppress("DEPRECATION") GuildStore
            10 -> PublicNewsThread
            11 -> PublicGuildThread
            12 -> PrivateThread
            13 -> GuildStageVoice
            14 -> GuildDirectory
            else -> Unknown(code)
        }

        override fun serialize(encoder: Encoder, value: ChannelType) = encoder.encodeInt(value.value)
    }

}

@Serializable
public data class Overwrite(
    val id: Snowflake,
    val type: OverwriteType,
    val allow: Permissions,
    val deny: Permissions,
)

@Serializable(with = OverwriteType.Serializer::class)
public sealed class OverwriteType(public val value: Int) {

    public class Unknown(value: Int) : OverwriteType(value)
    public object Role : OverwriteType(0)
    public object Member : OverwriteType(1)

    internal object Serializer : KSerializer<OverwriteType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.Overwrite.Type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): OverwriteType = when (val value = decoder.decodeInt()) {
            0 -> Role
            1 -> Member
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: OverwriteType) {
            encoder.encodeInt(value.value)
        }
    }
}

@Serializable(with = VideoQualityMode.Serializer::class)
public sealed class VideoQualityMode(public val value: Int) {

    final override fun equals(other: Any?): Boolean =
        this === other || (other is VideoQualityMode && other.value == this.value)

    final override fun hashCode(): Int = value


    /** An unknown Video Quality Mode. */
    public class Unknown(value: Int) : VideoQualityMode(value)

    /** Discord chooses the quality for optimal performance. */
    public object Auto : VideoQualityMode(1)

    /** 720p. */
    public object Full : VideoQualityMode(2)


    internal object Serializer : KSerializer<VideoQualityMode> {
        override val descriptor =
            PrimitiveSerialDescriptor("dev.kord.common.entity.VideoQualityMode", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: VideoQualityMode) = encoder.encodeInt(value.value)

        override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> Auto
            2 -> Full
            else -> Unknown(value)
        }
    }
}

@Serializable
public data class DiscordThreadMetadata(
    val archived: Boolean,
    @SerialName("archive_timestamp")
    val archiveTimestamp: Instant,
    @SerialName("auto_archive_duration")
    val autoArchiveDuration: ArchiveDuration,
    val locked: OptionalBoolean = OptionalBoolean.Missing,
    val invitable: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("create_timestamp")
    val createTimestamp: Optional<Instant?> = Optional.Missing(),
)

@Serializable(with = ArchiveDuration.Serializer::class)
public sealed class ArchiveDuration(public val duration: Duration) {
    public class Unknown(duration: Duration) : ArchiveDuration(duration)
    public object Hour : ArchiveDuration(60.minutes)
    public object Day : ArchiveDuration(1440.minutes)
    public object ThreeDays : ArchiveDuration(4320.minutes)
    public object Week : ArchiveDuration(10080.minutes)

    public object Serializer : KSerializer<ArchiveDuration> {

        override val descriptor: SerialDescriptor get() = DurationInMinutesSerializer.descriptor

        override fun deserialize(decoder: Decoder): ArchiveDuration {
            val value = decoder.decodeSerializableValue(DurationInMinutesSerializer)
            return values.firstOrNull { it.duration == value } ?: Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: ArchiveDuration) {
            encoder.encodeSerializableValue(DurationInMinutesSerializer, value.duration)
        }
    }

    public companion object {
        public val values: Set<ArchiveDuration>
            get() = setOf(
                Hour,
                Day,
                ThreeDays,
                Week,
            )
    }
}
