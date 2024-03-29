package dev.kord.rest.builder.channel.thread

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.ChannelFlags
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import kotlin.time.Duration

@KordDsl
public class ThreadModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _archived: OptionalBoolean = OptionalBoolean.Missing
    public var archived: Boolean? by ::_archived.delegate()

    private var _locked: OptionalBoolean = OptionalBoolean.Missing
    public var locked: Boolean? by ::_locked.delegate()

    private var _rateLimitPerUser: Optional<Duration?> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    private var _autoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing()
    public var autoArchiveDuration: ArchiveDuration? by ::_autoArchiveDuration.delegate()

    private var _invitable: OptionalBoolean = OptionalBoolean.Missing
    public var invitable: Boolean? by ::_invitable.delegate()

    private var _flags: Optional<ChannelFlags> = Optional.Missing()
    public var flags: ChannelFlags? by ::_flags.delegate()

    private var _appliedTags: Optional<MutableList<Snowflake>> = Optional.Missing()
    public var appliedTags: MutableList<Snowflake>? by ::_appliedTags.delegate()

    override fun toRequest(): ChannelModifyPatchRequest {
        return ChannelModifyPatchRequest(
            name = _name,
            locked = _locked,
            archived = _archived,
            autoArchiveDuration = _autoArchiveDuration,
            rateLimitPerUser = _rateLimitPerUser,
            invitable = _invitable,
            flags = _flags,
            appliedTags = _appliedTags
        )
    }

    override var reason: String? = null
}

/** Add a [tagId] to [appliedTags][ThreadModifyBuilder.appliedTags]. */
public fun ThreadModifyBuilder.applyTag(tagId: Snowflake) {
    appliedTags?.add(tagId) ?: run { appliedTags = mutableListOf(tagId) }
}
