package dev.kord.rest.builder.channel.thread

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.StartThreadRequest
import kotlin.time.Duration

public open class StartThreadWithMessageBuilder(public var name: String) : AuditRequestBuilder<StartThreadRequest> {
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
}

@Deprecated("Renamed to StartThreadWithMessageBuilder", replaceWith = ReplaceWith("dev.kord.rest.builder.channel.thread.StartThreadWithMessageBuilder"))
public typealias StartThreadBuilder = StartThreadWithMessageBuilder
