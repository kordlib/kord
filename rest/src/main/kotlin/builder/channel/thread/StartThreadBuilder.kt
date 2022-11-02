package dev.kord.rest.builder.channel.thread

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.serialization.DurationInSeconds
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.StartThreadRequest

public open class StartThreadBuilder(public var name: String) : AuditRequestBuilder<StartThreadRequest> {
    override var reason: String? = null

    internal var _autoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing()
    public var autoArchiveDuration: ArchiveDuration? by ::_autoArchiveDuration.delegate()

    internal var _rateLimitPerUser: Optional<DurationInSeconds?> = Optional.Missing()
    public var rateLimitPerUser: DurationInSeconds? by ::_rateLimitPerUser.delegate()

    override fun toRequest(): StartThreadRequest {
        return StartThreadRequest(
            name = name,
            autoArchiveDuration = _autoArchiveDuration,
            rateLimitPerUser = _rateLimitPerUser
        )
    }
}
