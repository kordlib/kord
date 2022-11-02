package dev.kord.rest.builder.channel.thread

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.StartThreadRequest

public class StartThreadWithoutMessageBuilder(name: String) : StartThreadBuilder(name) {
    private var _invitable: OptionalBoolean = OptionalBoolean.Missing
    public var invitable: Boolean? by ::_invitable.delegate()

    private var _type: Optional<ChannelType> = Optional.Missing()
    public var type: ChannelType? by ::_type.delegate()

    override fun toRequest(): StartThreadRequest {
        return StartThreadRequest(
            name = name,
            autoArchiveDuration = _autoArchiveDuration,
            type = _type,
            invitable = _invitable,
            rateLimitPerUser = _rateLimitPerUser
        )
    }
}
