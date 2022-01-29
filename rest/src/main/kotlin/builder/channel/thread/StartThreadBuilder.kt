package dev.kord.rest.builder.channel.thread

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.StartThreadRequest

public class StartThreadBuilder(
    public var name: String,
    public var autoArchiveDuration: ArchiveDuration,
    public val type: ChannelType,
) : AuditRequestBuilder<StartThreadRequest> {
    override var reason: String? = null

    private var _invitable: OptionalBoolean = OptionalBoolean.Missing
    public var invitable: Boolean? by ::_invitable.delegate()

    override fun toRequest(): StartThreadRequest {
        return StartThreadRequest(
            name = name,
            autoArchiveDuration = autoArchiveDuration,
            type = type.optional(), // Currently this is optional, but in API v10 it will be required according to Discord's docs.
            invitable = _invitable
        )
    }
}
