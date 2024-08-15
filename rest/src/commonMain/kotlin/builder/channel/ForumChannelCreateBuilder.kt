package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ForumTagRequest
import dev.kord.rest.json.request.GuildChannelCreateRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

@KordDsl
public class ForumChannelCreateBuilder(public var name: String) :
    PermissionOverwritesCreateBuilder,
    AuditRequestBuilder<GuildChannelCreateRequest> {
    override var reason: String? = null

    private var _topic: Optional<String> = Optional.Missing()
    public var topic: String? by ::_topic.delegate()

    private var _rateLimitPerUser: Optional<Duration> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    private var _position: OptionalInt = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    private var _parentId: OptionalSnowflake = OptionalSnowflake.Missing
    public var parentId: Snowflake? by ::_parentId.delegate()

    private var _nsfw: OptionalBoolean = OptionalBoolean.Missing
    public var nsfw: Boolean? by ::_nsfw.delegate()

    private var _defaultAutoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing()

    /**
     * The default [duration][ArchiveDuration] that the clients use (not the API) for newly created threads in the
     * channel, to automatically archive the thread after recent activity.
     */
    public var defaultAutoArchiveDuration: ArchiveDuration? by ::_defaultAutoArchiveDuration.delegate()

    override var permissionOverwrites: MutableSet<Overwrite> = mutableSetOf()

    private var _defaultReactionEmoji: Optional<DefaultReaction?> = Optional.Missing()
    public var defaultReactionEmoji: DefaultReaction? by ::_defaultReactionEmoji.delegate()
    public var defaultReactionEmojiId: Snowflake? = null
    public var defaultReactionEmojiName: String? = null

    private var _availableTags: Optional<MutableList<ForumTagRequest>?> = Optional.Missing()
    public var availableTags: MutableList<ForumTagRequest>? by ::_availableTags.delegate()

    public fun tag(name: String, builder: ForumTagBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        if (availableTags == null) availableTags = mutableListOf()

        val tagBuilder = ForumTagBuilder(name).apply(builder)
        availableTags?.add(tagBuilder.toRequest())
    }

    private var _defaultThreadRateLimitPerUser: Optional<Duration> = Optional.Missing()
    public var defaultThreadRateLimitPerUser: Duration? by ::_defaultThreadRateLimitPerUser.delegate()

    private var _defaultSortOrder: Optional<SortOrderType?> = Optional.Missing()
    public var defaultSortOrder: SortOrderType? by ::_defaultSortOrder.delegate()

    private var _defaultForumLayout: Optional<ForumLayoutType> = Optional.Missing()
    public var defaultForumLayout: ForumLayoutType? by ::_defaultForumLayout.delegate()

    private var _flags: Optional<ChannelFlags> = Optional.Missing()
    public var flags: ChannelFlags? by ::_flags.delegate()

    override fun toRequest(): GuildChannelCreateRequest = GuildChannelCreateRequest(
        name = name,
        type = ChannelType.GuildForum,
        topic = _topic,
        rateLimitPerUser = _rateLimitPerUser,
        position = _position,
        parentId = _parentId,
        nsfw = _nsfw,
        permissionOverwrite = Optional.missingOnEmpty(permissionOverwrites),
        defaultAutoArchiveDuration = _defaultAutoArchiveDuration,
        defaultReactionEmoji = when {
            defaultReactionEmojiId != null || defaultReactionEmojiName != null ->
                DefaultReaction(
                    emojiId = defaultReactionEmojiId,
                    emojiName = defaultReactionEmojiName,
                ).optional()
            else -> _defaultReactionEmoji
        },
        defaultThreadRateLimitPerUser = _defaultThreadRateLimitPerUser,
        availableTags = _availableTags,
        defaultSortOrder = _defaultSortOrder,
        flags = _flags,
        defaultForumLayout = _defaultForumLayout,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ForumChannelCreateBuilder

        if (name != other.name) return false
        if (reason != other.reason) return false
        if (topic != other.topic) return false
        if (rateLimitPerUser != other.rateLimitPerUser) return false
        if (position != other.position) return false
        if (parentId != other.parentId) return false
        if (nsfw != other.nsfw) return false
        if (defaultAutoArchiveDuration != other.defaultAutoArchiveDuration) return false
        if (permissionOverwrites != other.permissionOverwrites) return false
        if (defaultReactionEmoji != other.defaultReactionEmoji) return false
        if (defaultReactionEmojiId != other.defaultReactionEmojiId) return false
        if (defaultReactionEmojiName != other.defaultReactionEmojiName) return false
        if (availableTags != other.availableTags) return false
        if (defaultThreadRateLimitPerUser != other.defaultThreadRateLimitPerUser) return false
        if (defaultSortOrder != other.defaultSortOrder) return false
        if (defaultForumLayout != other.defaultForumLayout) return false
        if (flags != other.flags) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (topic?.hashCode() ?: 0)
        result = 31 * result + (rateLimitPerUser?.hashCode() ?: 0)
        result = 31 * result + (position ?: 0)
        result = 31 * result + (parentId?.hashCode() ?: 0)
        result = 31 * result + (nsfw?.hashCode() ?: 0)
        result = 31 * result + (defaultAutoArchiveDuration?.hashCode() ?: 0)
        result = 31 * result + permissionOverwrites.hashCode()
        result = 31 * result + (defaultReactionEmoji?.hashCode() ?: 0)
        result = 31 * result + (defaultReactionEmojiId?.hashCode() ?: 0)
        result = 31 * result + (defaultReactionEmojiName?.hashCode() ?: 0)
        result = 31 * result + (availableTags?.hashCode() ?: 0)
        result = 31 * result + (defaultThreadRateLimitPerUser?.hashCode() ?: 0)
        result = 31 * result + (defaultSortOrder?.hashCode() ?: 0)
        result = 31 * result + (defaultForumLayout?.hashCode() ?: 0)
        result = 31 * result + (flags?.hashCode() ?: 0)
        return result
    }

}

@KordDsl
public class ForumTagBuilder(public var name: String) : RequestBuilder<ForumTagRequest> {
    private var _moderated: OptionalBoolean = OptionalBoolean.Missing
    public var moderated: Boolean? by ::_moderated.delegate()

    private var _reactionEmojiId: Optional<Snowflake?> = Optional.Missing()
    public var reactionEmojiId: Snowflake? by ::_reactionEmojiId.delegate()

    private var _reactionEmojiName: Optional<String?> = Optional.Missing()
    public var reactionEmojiName: String? by ::_reactionEmojiName.delegate()

    override fun toRequest(): ForumTagRequest {
        return ForumTagRequest(
            name = name,
            moderated = _moderated,
            emojiId = _reactionEmojiId,
            emojiName = _reactionEmojiName
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ForumTagBuilder

        if (name != other.name) return false
        if (moderated != other.moderated) return false
        if (reactionEmojiId != other.reactionEmojiId) return false
        if (reactionEmojiName != other.reactionEmojiName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (moderated?.hashCode() ?: 0)
        result = 31 * result + (reactionEmojiId?.hashCode() ?: 0)
        result = 31 * result + (reactionEmojiName?.hashCode() ?: 0)
        return result
    }

}
