@file:GenerateKordEnum(
    name = "ChannelType", valueType = INT,
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
    ],
)

@file:GenerateKordEnum(
    name = "VideoQualityMode", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/channel#channel-object-video-quality-modes",
    entries = [
        Entry("Auto", intValue = 1, kDoc = "Discord chooses the quality for optimal performance."),
        Entry("Full", intValue = 2, kDoc = "720p."),
    ],
)

@file:GenerateKordEnum(
    name = "SortOrderType", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/channel#channel-object-sort-order-types",
    entries = [
        Entry("LatestActivity", intValue = 0, kDoc = "Sort forum posts by activity."),
        Entry("CreationDate", intValue = 1, kDoc = "Sort forum posts by creation time (from most recent to oldest)."),
    ],
)

@file:GenerateKordEnum(
    name = "ForumLayoutType", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/channel#channel-object-forum-layout-types",
    entries = [
        Entry("NotSet", intValue = 0, kDoc = "No default has been set for forum channel."),
        Entry("ListView", intValue = 1, kDoc = "Display posts as a list."),
        Entry("GalleryView", intValue = 2, kDoc = "Display posts as a collection of tiles."),
    ],
)

@file:GenerateKordEnum(
    name = "OverwriteType", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/channel#overwrite-object-overwrite-structure",
    entries = [Entry("Role", intValue = 0), Entry("Member", intValue = 1)],
)

package dev.kord.common.entity

import dev.kord.common.entity.ChannelType.GuildForum
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
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.DeprecationLevel.HIDDEN
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName
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
    // Forum thread original message
    // see in: https://discord.com/developers/docs/resources/channel#start-thread-in-forum-channel
    val message: Optional<DiscordMessage> = Optional.Missing(),
)

public enum class ChannelFlag(public val code: Int) {

    /** This thread is pinned to the top of its parent [GuildForum] channel. */
    Pinned(1 shl 1),

    /** Whether a tag is required to be specified when creating a thread in a [GuildForum] channel. */
    RequireTag(1 shl 4);


    public operator fun plus(flag: ChannelFlag): ChannelFlags = ChannelFlags(this.code or flag.code)

    public operator fun plus(flags: ChannelFlags): ChannelFlags = flags + this
}

@Serializable(with = ChannelFlags.Serializer::class)
public data class ChannelFlags internal constructor(public val code: Int) {

    public val flags: List<ChannelFlag> get() = ChannelFlag.values().filter { it in this }

    public operator fun contains(flag: ChannelFlag): Boolean = this.code and flag.code == flag.code

    public operator fun contains(flags: ChannelFlags): Boolean = this.code and flags.code == flags.code

    public operator fun plus(flag: ChannelFlag): ChannelFlags = ChannelFlags(this.code or flag.code)

    public operator fun plus(flags: ChannelFlags): ChannelFlags = ChannelFlags(this.code or flags.code)

    public operator fun minus(flag: ChannelFlag): ChannelFlags = ChannelFlags(this.code and flag.code.inv())

    public operator fun minus(flags: ChannelFlags): ChannelFlags = ChannelFlags(this.code and flags.code.inv())


    public inline fun copy(builder: Builder.() -> Unit): ChannelFlags {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }


    internal object Serializer : KSerializer<ChannelFlags> {

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("dev.kord.common.entity.ChannelFlags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ChannelFlags {
            val code = decoder.decodeInt()
            return ChannelFlags(code)
        }

        override fun serialize(encoder: Encoder, value: ChannelFlags) {
            encoder.encodeInt(value.code)
        }
    }


    public class Builder(private var code: Int = 0) {

        public operator fun ChannelFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ChannelFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ChannelFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun ChannelFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun build(): ChannelFlags = ChannelFlags(code)
    }
}

public inline fun ChannelFlags(builder: ChannelFlags.Builder.() -> Unit): ChannelFlags {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return ChannelFlags.Builder().apply(builder).build()
}

public fun ChannelFlags(vararg flags: ChannelFlag): ChannelFlags = ChannelFlags { flags.forEach { +it } }

public fun ChannelFlags(vararg flags: ChannelFlags): ChannelFlags = ChannelFlags { flags.forEach { +it } }

public fun ChannelFlags(flags: Iterable<ChannelFlag>): ChannelFlags = ChannelFlags { flags.forEach { +it } }

@JvmName("ChannelFlags0")
public fun ChannelFlags(flags: Iterable<ChannelFlags>): ChannelFlags = ChannelFlags { flags.forEach { +it } }

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

// this should actually be generated with @file:GenerateKordEnum,
// but it's not worth adding support for Duration just for this class
@Serializable(with = ArchiveDuration.NewSerializer::class)
public sealed class ArchiveDuration(
    /** The raw [Duration] used by Discord. */
    public val duration: Duration,
) {
    final override fun equals(other: Any?): Boolean =
        this === other || (other is ArchiveDuration && this.duration == other.duration)

    final override fun hashCode(): Int = duration.hashCode()
    final override fun toString(): String = "ArchiveDuration.${this::class.simpleName}(duration=$duration)"

    /**
     * An unknown [ArchiveDuration].
     *
     * This is used as a fallback for [ArchiveDuration]s that haven't been added to Kord yet.
     */
    public class Unknown(duration: Duration) : ArchiveDuration(duration)
    public object Hour : ArchiveDuration(60.minutes)
    public object Day : ArchiveDuration(1440.minutes)
    public object ThreeDays : ArchiveDuration(4320.minutes)
    public object Week : ArchiveDuration(10080.minutes)

    internal object NewSerializer : KSerializer<ArchiveDuration> {
        override val descriptor get() = DurationInMinutesSerializer.descriptor

        override fun serialize(encoder: Encoder, value: ArchiveDuration) =
            encoder.encodeSerializableValue(DurationInMinutesSerializer, value.duration)

        override fun deserialize(decoder: Decoder): ArchiveDuration {
            val duration = decoder.decodeSerializableValue(DurationInMinutesSerializer)
            return entries.firstOrNull { it.duration == duration } ?: Unknown(duration)
        }
    }

    public companion object {
        /** A [List] of all known [ArchiveDuration]s. */
        public val entries: List<ArchiveDuration> by lazy(mode = PUBLICATION) {
            listOf(Hour, Day, ThreeDays, Week)
        }

        @Deprecated("Renamed to 'entries'.", ReplaceWith("this.entries"), level = HIDDEN)
        public val values: Set<ArchiveDuration> get() = entries.toSet()
    }

    @Deprecated(
        "Use 'ArchiveDuration.serializer()' instead.",
        ReplaceWith("ArchiveDuration.serializer()", "dev.kord.common.entity.ArchiveDuration"),
        level = HIDDEN,
    )
    // TODO rename internal `NewSerializer` to `Serializer` when this is removed
    public object Serializer : KSerializer<ArchiveDuration> by NewSerializer
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
