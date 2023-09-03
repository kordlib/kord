package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.ForumTagRequest
import dev.kord.rest.json.request.GuildChannelCreateRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

@KordDsl
public class MediaChannelCreateBuilder(public var name: String) :
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
    )
}
