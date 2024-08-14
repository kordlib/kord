@file:Generate(
    INT_KORD_ENUM, name = "ChannelType",
    docUrl = "https://discord.com/developers/docs/resources/channel#channel-object-channel-types",
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
        Entry(
            "PublicGuildThread", intValue = 11,
            kDoc = "A temporary sub-channel within a [GuildText] or [GuildForum] channel."
        ),
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
        Entry("GuildForum", intValue = 15, kDoc = "A channel that can only contain threads."),
        Entry(
            "GuildMedia", intValue = 16,
            kDoc = "A channel that can only contain threads, similar to [GuildForum] channels.",
        ),
    ],
)

@file:Generate(
    INT_FLAGS, name = "ChannelFlag", valueName = "code",
    docUrl = "https://discord.com/developers/docs/resources/channel#channel-object-channel-flags",
    entries = [
        Entry(
            "Pinned", shift = 1,
            kDoc = "This thread is pinned to the top of its parent [GuildForum][ChannelType.GuildForum] or " +
                "[GuildMedia][ChannelType.GuildMedia] channel.",
        ),
        Entry(
            "RequireTag", shift = 4,
            kDoc = "Whether a tag is required to be specified when creating a thread in a " +
                "[GuildForum][ChannelType.GuildForum] or [GuildMedia][ChannelType.GuildMedia] channel.",
        ),
        Entry(
            "HideMediaDownloadOptions", shift = 15,
            kDoc = "When set hides the embedded media download options. Available only for " +
                "[GuildMedia][ChannelType.GuildMedia] channels.",
        ),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "VideoQualityMode",
    docUrl = "https://discord.com/developers/docs/resources/channel#channel-object-video-quality-modes",
    entries = [
        Entry("Auto", intValue = 1, kDoc = "Discord chooses the quality for optimal performance."),
        Entry("Full", intValue = 2, kDoc = "720p."),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "SortOrderType",
    docUrl = "https://discord.com/developers/docs/resources/channel#channel-object-sort-order-types",
    entries = [
        Entry("LatestActivity", intValue = 0, kDoc = "Sort forum posts by activity."),
        Entry("CreationDate", intValue = 1, kDoc = "Sort forum posts by creation time (from most recent to oldest)."),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "ForumLayoutType",
    docUrl = "https://discord.com/developers/docs/resources/channel#channel-object-forum-layout-types",
    entries = [
        Entry("NotSet", intValue = 0, kDoc = "No default has been set for forum channel."),
        Entry("ListView", intValue = 1, kDoc = "Display posts as a list."),
        Entry("GalleryView", intValue = 2, kDoc = "Display posts as a collection of tiles."),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "OverwriteType",
    docUrl = "https://discord.com/developers/docs/resources/channel#overwrite-object-overwrite-structure",
    entries = [Entry("Role", intValue = 0), Entry("Member", intValue = 1)],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInMinutesSerializer
import dev.kord.common.serialization.DurationInSeconds
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_FLAGS
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

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
    val member: Optional<DiscordThreadMember> = Optional.Missing(),
    val flags: Optional<ChannelFlags> = Optional.Missing(),
    @SerialName("total_message_sent")
    val totalMessageSent: OptionalInt = OptionalInt.Missing,
    @SerialName("available_tags")
    val availableTags: Optional<List<ForumTag>> = Optional.Missing(),
    @SerialName("applied_tags")
    val appliedTags: Optional<List<Snowflake>> = Optional.Missing(),
    @SerialName("default_reaction_emoji")
    val defaultReactionEmoji: Optional<DefaultReaction?> = Optional.Missing(),
    @SerialName("default_thread_rate_limit_per_user")
    val defaultThreadRateLimitPerUser: Optional<DurationInSeconds> = Optional.Missing(),
    @SerialName("default_sort_order")
    val defaultSortOrder: Optional<SortOrderType?> = Optional.Missing(),
    @SerialName("default_forum_layout")
    val defaultForumLayout: Optional<ForumLayoutType> = Optional.Missing(),
    // original message when starting thread in forum or media channel, see
    // https://discord.com/developers/docs/resources/channel#start-thread-in-forum-or-media-channel
    val message: Optional<DiscordMessage> = Optional.Missing(),
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

// this should actually be generated with @file:Generate,
// but it's not worth adding support for Duration just for this class
@Serializable(with = ArchiveDuration.Serializer::class)
public sealed class ArchiveDuration(
    /** The raw [Duration] used by Discord. */
    public val duration: Duration,
) {
    final override fun equals(other: Any?): Boolean =
        this === other || (other is ArchiveDuration && this.duration == other.duration)

    final override fun hashCode(): Int = duration.hashCode()
    final override fun toString(): String =
        if (this is Unknown) "ArchiveDuration.Unknown(duration=$duration)"
        else "ArchiveDuration.${this::class.simpleName}"

    /**
     * An unknown [ArchiveDuration].
     *
     * This is used as a fallback for [ArchiveDuration]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        duration: Duration,
        @Suppress("UNUSED_PARAMETER") unused: Nothing?,
    ) : ArchiveDuration(duration) {
        @Deprecated(
            "Replaced by 'ArchiveDuration.from()'.",
            ReplaceWith("ArchiveDuration.from(duration)", imports = ["dev.kord.common.entity.ArchiveDuration"]),
            DeprecationLevel.HIDDEN,
        )
        public constructor(duration: Duration) : this(duration, unused = null)
    }

    public object Hour : ArchiveDuration(60.minutes)
    public object Day : ArchiveDuration(1440.minutes)
    public object ThreeDays : ArchiveDuration(4320.minutes)
    public object Week : ArchiveDuration(10080.minutes)

    internal object Serializer : KSerializer<ArchiveDuration> {
        override val descriptor =
            PrimitiveSerialDescriptor("dev.kord.common.entity.ArchiveDuration", PrimitiveKind.LONG)

        override fun serialize(encoder: Encoder, value: ArchiveDuration) =
            encoder.encodeSerializableValue(DurationInMinutesSerializer, value.duration)

        override fun deserialize(decoder: Decoder) = from(decoder.decodeSerializableValue(DurationInMinutesSerializer))
    }

    public companion object {
        /** A [List] of all known [ArchiveDuration]s. */
        public val entries: List<ArchiveDuration> by lazy(mode = PUBLICATION) { listOf(Hour, Day, ThreeDays, Week) }

        private val entriesByDuration by lazy(mode = PUBLICATION) { entries.associateBy(ArchiveDuration::duration) }

        /**
         * Returns an instance of [ArchiveDuration] with [ArchiveDuration.duration] equal to the specified [duration].
         */
        public fun from(duration: Duration): ArchiveDuration =
            entriesByDuration[duration] ?: Unknown(duration, unused = null)
    }
}

@Serializable
public data class DefaultReaction(
    @SerialName("emoji_id")
    val emojiId: Snowflake?,
    @SerialName("emoji_name")
    val emojiName: String?,
)

@Serializable
public data class ForumTag(
    val id: Snowflake,
    val name: String,
    val moderated: Boolean,
    @SerialName("emoji_id")
    val emojiId: Snowflake?,
    @SerialName("emoji_name")
    val emojiName: String?,
)
