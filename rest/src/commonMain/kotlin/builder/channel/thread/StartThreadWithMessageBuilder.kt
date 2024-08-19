package dev.kord.rest.builder.channel.thread

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.StartThreadRequest
import kotlin.time.Duration

@KordDsl
public class StartThreadWithMessageBuilder(public var name: String) : AuditRequestBuilder<StartThreadRequest> {

    override var reason: String? = null

    private var _autoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing()
    public var autoArchiveDuration: ArchiveDuration? by ::_autoArchiveDuration.delegate()

    private var _rateLimitPerUser: Optional<Duration?> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    override fun toRequest(): StartThreadRequest {
        return StartThreadRequest(
            name = name,
            autoArchiveDuration = _autoArchiveDuration,
            rateLimitPerUser = _rateLimitPerUser
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StartThreadWithMessageBuilder

        if (name != other.name) return false
        if (reason != other.reason) return false
        if (autoArchiveDuration != other.autoArchiveDuration) return false
        if (rateLimitPerUser != other.rateLimitPerUser) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (autoArchiveDuration?.hashCode() ?: 0)
        result = 31 * result + (rateLimitPerUser?.hashCode() ?: 0)
        return result
    }

}
