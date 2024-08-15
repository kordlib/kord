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
import kotlin.time.Duration

@KordDsl
public class StartThreadBuilder(
    public var name: String,
    public val type: ChannelType,
) : AuditRequestBuilder<StartThreadRequest> {

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StartThreadBuilder

        if (name != other.name) return false
        if (type != other.type) return false
        if (reason != other.reason) return false
        if (autoArchiveDuration != other.autoArchiveDuration) return false
        if (rateLimitPerUser != other.rateLimitPerUser) return false
        if (invitable != other.invitable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (autoArchiveDuration?.hashCode() ?: 0)
        result = 31 * result + (rateLimitPerUser?.hashCode() ?: 0)
        result = 31 * result + (invitable?.hashCode() ?: 0)
        return result
    }

}
