package dev.kord.rest.builder.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.json.request.MessageCreateRequest
import dev.kord.rest.json.request.StartThreadRequest

public class StartForumThreadBuilder(name: String) : StartThreadBuilder(name) {
    private var _appliedTags: Optional<List<Snowflake>?> = Optional.Missing()
    public var appliedTags: List<Snowflake>? by ::_appliedTags.delegate()

    private var _message: Optional<MessageCreateRequest> = Optional.Missing()
    public var message: MessageCreateRequest? by ::_message.delegate()

    public inline fun createPostMessage(builder: UserMessageCreateBuilder.() -> Unit) {
        message = UserMessageCreateBuilder().apply(builder).toRequest().request
    }

    override fun toRequest(): StartThreadRequest {
        return StartThreadRequest(
            name = name,
            autoArchiveDuration = _autoArchiveDuration,
            rateLimitPerUser = _rateLimitPerUser,
            message = _message,
            appliedTags = _appliedTags
        )
    }

}
