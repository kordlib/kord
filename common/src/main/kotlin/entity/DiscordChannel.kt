@file:GenerateKordEnum(
    name = "ChannelType", valueType = INT,
    entries = [
        Entry("GuildText", intValue = 0, kDoc = "A text channel within a server."),
        Entry("DM", intValue = 1, kDoc = "A direct message between users."),
        Entry("GuildVoice", intValue = 2, kDoc = "A voice channel within a server."),
        Entry("GroupDM", intValue = 3, kDoc = "A direct message between multiple users."),
        Entry(
            "GuildCategory", intValue = 4,
            kDoc = "An [organizational·category](https://support.discord.com/hc/en-us/articles/115001580171-Channel-" +
                    "Categories-101) that contains up to 50 channels.",
        ),
        Entry(
            "GuildNews", intValue = 5,
            kDoc = "A channel that [users·can·follow·and·crosspost·into·their·own·server]" +
                    "(https://support.discord.com/hc/en-us/articles/360032008192).",
        ),
        Entry("PublicNewsThread", intValue = 10, kDoc = "A temporary sub-channel within a [GuildNews] channel."),
        Entry("PublicGuildThread", intValue = 11, kDoc = "A temporary sub-channel within a [GuildText] channel."),
        Entry(
            "PrivateThread", intValue = 12,
            kDoc = "A temporary sub-channel within a [GuildText] channel that is only viewable by those invited and " +
                    "those with the [ManageThreads][dev.kord.common.entity.Permission.ManageThreads] permission.",
        ),
        Entry(
            "GuildStageVoice", intValue = 13,
            kDoc = "A voice channel for [hosting·events·with·an·audience]" +
                    "(https://support.discord.com/hc/en-us/articles/1500005513722).",
        ),
        Entry(
            "GuildDirectory", intValue = 14,
            kDoc = "The channel in a [hub](https://support.discord.com/hc/en-us/articles/4406046651927-Discord-" +
                    "Student-Hubs-FAQ) containing the listed servers.",
        ),
    ],
    deprecatedEntries = [
        Entry(
            "GuildStore", intValue = 6,
            kDoc = "A channel in which game developers can sell their game on Discord.\n\n@suppress",
            deprecationMessage = "Discord no longer offers the ability to purchase a license to sell PC games on " +
                    "Discord and store channels were removed on March 10, 2022. See " +
                    "https://support-dev.discord.com/hc/en-us/articles/6309018858647-Self-serve-Game-Selling-Deprecation" +
                    " for more information.",
            deprecationLevel = HIDDEN,
        ),
    ],
)

@file:GenerateKordEnum(
    name = "VideoQualityMode", valueType = INT,
    entries = [
        Entry("Auto", intValue = 1, kDoc = "Discord chooses the quality for optimal performance."),
        Entry("Full", intValue = 2, kDoc = "720p."),
    ],
)

@file:GenerateKordEnum(
    name = "OverwriteType", valueType = INT,
    entries = [Entry("Role", intValue = 0), Entry("Member", intValue = 1)],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInMinutesSerializer
import dev.kord.common.serialization.DurationInSeconds
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.DeprecationLevel.HIDDEN
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

@Serializable
public data class Overwrite(
    val id: Snowflake,
    val type: OverwriteType,
    val allow: Permissions,
    val deny: Permissions,
)

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
