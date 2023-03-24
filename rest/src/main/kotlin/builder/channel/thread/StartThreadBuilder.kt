package dev.kord.rest.builder.channel.thread

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.StartThreadRequest
import kotlin.DeprecationLevel.WARNING
import kotlin.time.Duration

@KordDsl
public class StartThreadBuilder(
    public var name: String,
    public val type: ChannelType,
) : AuditRequestBuilder<StartThreadRequest> {
    @Deprecated(
        "'autoArchiveDuration' is no longer required, use other constructor instead.",
        ReplaceWith(
            "StartThreadBuilder(name, type).apply { this@apply.autoArchiveDuration = autoArchiveDuration }",
            imports = ["dev.kord.rest.builder.channel.thread.StartThreadBuilder"]
        ),
        level = WARNING,
    )
    public constructor(name: String, autoArchiveDuration: ArchiveDuration, type: ChannelType) : this(name, type) {
        this.autoArchiveDuration = autoArchiveDuration
    }

    override var reason: String? = null

    private var _autoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing()
    public var autoArchiveDuration: ArchiveDuration? by ::_autoArchiveDuration.delegate()

    private var _rateLimitPerUser: Optional<Duration?> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    private var _invitable: OptionalBoolean = OptionalBoolean.Missing
    public var invitable: Boolean? by ::_invitable.delegate()

    override fun toRequest(): StartThreadRequest {
        return StartThreadRequest(
            name = name,
            autoArchiveDuration = _autoArchiveDuration,
            type = type.optional(), // currently optional, will be required in the future according to Discord's docs
            invitable = _invitable,
            rateLimitPerUser = _rateLimitPerUser
        )
    }
}
