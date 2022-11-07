package dev.kord.rest.builder.channel.thread

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.json.request.MultipartMessageCreateRequest
import dev.kord.rest.json.request.MultipartStartThreadRequest
import dev.kord.rest.json.request.StartThreadRequest
import kotlin.time.Duration

public class StartForumThreadBuilder(public var name: String) : AuditRequestBuilder<MultipartStartThreadRequest> {
    override var reason: String? = null

    private var _autoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing()
    public var autoArchiveDuration: ArchiveDuration? by ::_autoArchiveDuration.delegate()

    private var _rateLimitPerUser: Optional<Duration?> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    private var _appliedTags: Optional<List<Snowflake>?> = Optional.Missing()
    public var appliedTags: List<Snowflake>? by ::_appliedTags.delegate()

    private var _message: Optional<MultipartMessageCreateRequest> = Optional.Missing()
    public var message: MultipartMessageCreateRequest? by ::_message.delegate()

    public inline fun createThreadMessage(builder: UserMessageCreateBuilder.() -> Unit) {
        message = UserMessageCreateBuilder().apply(builder).toRequest()
    }

    override fun toRequest(): MultipartStartThreadRequest {
        return MultipartStartThreadRequest(
            StartThreadRequest(
                name = name,
                autoArchiveDuration = _autoArchiveDuration,
                rateLimitPerUser = _rateLimitPerUser,
                message = _message.value?.request.optional(),
                appliedTags = _appliedTags
            ),
            _message.value?.files.optional()
        )
    }

}
