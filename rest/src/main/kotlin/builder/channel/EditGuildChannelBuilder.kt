package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import kotlin.time.Duration

@KordDsl
public class TextChannelModifyBuilder : PermissionOverwritesModifyBuilder,
    AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    private var _topic: Optional<String?> = Optional.Missing()
    public var topic: String? by ::_topic.delegate()

    private var _nsfw: OptionalBoolean? = OptionalBoolean.Missing
    public var nsfw: Boolean? by ::_nsfw.delegate()

    private var _parentId: OptionalSnowflake? = OptionalSnowflake.Missing
    public var parentId: Snowflake? by ::_parentId.delegate()

    private var _rateLimitPerUser: Optional<Duration?> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()
    override var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    private var _defaultAutoArchiveDuration: Optional<ArchiveDuration?> = Optional.Missing()

    /**
     * The default [duration][ArchiveDuration] that the clients use (not the API) for newly created threads in the
     * channel, to automatically archive the thread after recent activity.
     */
    public var defaultAutoArchiveDuration: ArchiveDuration? by ::_defaultAutoArchiveDuration.delegate()

    private var _defaultThreadRateLimitPerUser: Optional<Duration> = Optional.Missing()
    public var defaultThreadRateLimitPerUser: Duration? by ::_defaultThreadRateLimitPerUser.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        topic = _topic,
        nsfw = _nsfw,
        rateLimitPerUser = _rateLimitPerUser,
        permissionOverwrites = _permissionOverwrites,
        parentId = _parentId,
        defaultAutoArchiveDuration = _defaultAutoArchiveDuration,
        defaultThreadRateLimitPerUser = _defaultThreadRateLimitPerUser,
    )
}

@KordDsl
public class ForumChannelModifyBuilder : PermissionOverwritesModifyBuilder,
    AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    private var _topic: Optional<String?> = Optional.Missing()
    public var topic: String? by ::_topic.delegate()

    private var _nsfw: OptionalBoolean? = OptionalBoolean.Missing
    public var nsfw: Boolean? by ::_nsfw.delegate()

    private var _parentId: OptionalSnowflake? = OptionalSnowflake.Missing
    public var parentId: Snowflake? by ::_parentId.delegate()

    private var _rateLimitPerUser: Optional<Duration?> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()
    override var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    private var _defaultAutoArchiveDuration: Optional<ArchiveDuration?> = Optional.Missing()

    /**
     * The default [duration][ArchiveDuration] that the clients use (not the API) for newly created threads in the
     * channel, to automatically archive the thread after recent activity.
     */
    public var defaultAutoArchiveDuration: ArchiveDuration? by ::_defaultAutoArchiveDuration.delegate()

    private var _flags: Optional<ChannelFlags> = Optional.Missing()
    public var flags: ChannelFlags? by ::_flags.delegate()

    private var _defaultReactionEmoji: Optional<DiscordDefaultReaction?> = Optional.Missing()
    public var defaultReactionEmoji: DiscordDefaultReaction? by ::_defaultReactionEmoji.delegate()
    public var defaultReactionEmojiId: Snowflake? = null
    public var defaultReactionEmojiName: String? = null

    private var _availableTags: Optional<List<DiscordForumTag>> = Optional.Missing()
    public var availableTags: List<DiscordForumTag>? by ::_availableTags.delegate()

    private var _defaultThreadRateLimitPerUser: Optional<Duration> = Optional.Missing()
    public var defaultThreadRateLimitPerUser: Duration? by ::_defaultThreadRateLimitPerUser.delegate()

    private var _defaultSortOrder: Optional<SortOrderType?> = Optional.Missing()
    public var defaultSortOrder: SortOrderType? by ::_defaultSortOrder.delegate()

    private var _defaultForumLayout: Optional<ForumLayoutType?> = Optional.Missing()
    public var defaultForumLayout: ForumLayoutType? by ::_defaultForumLayout.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        topic = _topic,
        nsfw = _nsfw,
        rateLimitPerUser = _rateLimitPerUser,
        permissionOverwrites = _permissionOverwrites,
        parentId = _parentId,
        defaultAutoArchiveDuration = _defaultAutoArchiveDuration,
        defaultReactionEmoji = when {
            defaultReactionEmojiId != null || defaultReactionEmojiName != null ->
                DiscordDefaultReaction(
                    emojiId = defaultReactionEmojiId,
                    emojiName = defaultReactionEmojiName,
                ).optional()
            else -> _defaultReactionEmoji
        },
        defaultThreadRateLimitPerUser = _defaultThreadRateLimitPerUser,
        availableTags = _availableTags,
        defaultSortOrder = _defaultSortOrder,
        defaultForumLayout = _defaultForumLayout,
        flags = _flags
    )

}

@KordDsl
public class VoiceChannelModifyBuilder : PermissionOverwritesModifyBuilder,
    AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _rtcRegion: Optional<String?> = Optional.Missing()
    public var rtcRegion: String? by ::_rtcRegion.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    private var _topic: Optional<String?> = Optional.Missing()
    public var topic: String? by ::_topic.delegate()

    private var _nsfw: OptionalBoolean? = OptionalBoolean.Missing
    public var nsfw: Boolean? by ::_nsfw.delegate()

    private var _parentId: OptionalSnowflake? = OptionalSnowflake.Missing
    public var parentId: Snowflake? by ::_parentId.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()
    override var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    private var _bitrate: OptionalInt? = OptionalInt.Missing
    public var bitrate: Int? by ::_bitrate.delegate()

    private var _userLimit: OptionalInt? = OptionalInt.Missing
    public var userLimit: Int? by ::_userLimit.delegate()

    private var _videoQualityMode: Optional<VideoQualityMode?> = Optional.Missing()

    /** The camera [video quality mode][VideoQualityMode] of the voice channel. */
    public var videoQualityMode: VideoQualityMode? by ::_videoQualityMode.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        nsfw = _nsfw,
        parentId = _parentId,
        bitrate = _bitrate,
        userLimit = _userLimit,
        topic = _topic,
        permissionOverwrites = _permissionOverwrites,
        rtcRegion = _rtcRegion,
        videoQualityMode = _videoQualityMode,
    )

}


@KordDsl
public class StageVoiceChannelModifyBuilder : PermissionOverwritesModifyBuilder,
    AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _rtcRegion: Optional<String?> = Optional.Missing()
    public var rtcRegion: String? by ::_rtcRegion.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    private var _topic: Optional<String?> = Optional.Missing()
    public var topic: String? by ::_topic.delegate()

    private var _parentId: OptionalSnowflake? = OptionalSnowflake.Missing
    public var parentId: Snowflake? by ::_parentId.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()
    override var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    private var _bitrate: OptionalInt? = OptionalInt.Missing
    public var bitrate: Int? by ::_bitrate.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        parentId = _parentId,
        bitrate = _bitrate,
        topic = _topic,
        permissionOverwrites = _permissionOverwrites,
        rtcRegion = _rtcRegion,
    )

}

@KordDsl
public class NewsChannelModifyBuilder : PermissionOverwritesModifyBuilder,
    AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    private var _topic: Optional<String?> = Optional.Missing()
    public var topic: String? by ::_topic.delegate()

    private var _nsfw: OptionalBoolean? = OptionalBoolean.Missing
    public var nsfw: Boolean? by ::_nsfw.delegate()

    private var _parentId: OptionalSnowflake? = OptionalSnowflake.Missing
    public var parentId: Snowflake? by ::_parentId.delegate()

    private var _rateLimitPerUser: Optional<Duration?> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()
    override var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    private var _defaultAutoArchiveDuration: Optional<ArchiveDuration?> = Optional.Missing()

    /**
     * The default [duration][ArchiveDuration] that the clients use (not the API) for newly created threads in the
     * channel, to automatically archive the thread after recent activity.
     */
    public var defaultAutoArchiveDuration: ArchiveDuration? by ::_defaultAutoArchiveDuration.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        topic = _topic,
        nsfw = _nsfw,
        parentId = _parentId,
        rateLimitPerUser = _rateLimitPerUser,
        permissionOverwrites = _permissionOverwrites,
        defaultAutoArchiveDuration = _defaultAutoArchiveDuration,
    )
}
