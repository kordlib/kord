package dev.kord.rest.builder.channel.thread

import dev.kord.common.entity.ArchiveDuration
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.StartThreadRequest

public class StartThreadWithMessageBuilder(
    public var name: String,
    public var autoArchiveDuration: ArchiveDuration,
) : AuditRequestBuilder<StartThreadRequest> {
    override var reason: String? = null

    override fun toRequest(): StartThreadRequest {
        return StartThreadRequest(
            name = name,
            autoArchiveDuration = autoArchiveDuration
        )
    }
}
