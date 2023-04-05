package dev.kord.rest.builder.channel.thread

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.builder.message.create.ForumMessageCreateBuilder
import dev.kord.rest.json.request.MultipartStartThreadRequest
import dev.kord.rest.json.request.StartThreadRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

@KordDsl
public class StartForumThreadBuilder(public var name: String) : AuditRequestBuilder<MultipartStartThreadRequest> {
    override var reason: String? = null

    private var _autoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing()
    public var autoArchiveDuration: ArchiveDuration? by ::_autoArchiveDuration.delegate()

    private var _rateLimitPerUser: Optional<Duration?> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    private var _appliedTags: Optional<MutableList<Snowflake>> = Optional.Missing()
    public var appliedTags: MutableList<Snowflake>? by ::_appliedTags.delegate()

    public var message: ForumMessageCreateBuilder = ForumMessageCreateBuilder()

    public fun message(content: String) {
        message.content = content
    }

    public inline fun message(builder: ForumMessageCreateBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        message.apply(builder)
    }

    override fun toRequest(): MultipartStartThreadRequest {
        val messageRequest = message.toRequest()

        return MultipartStartThreadRequest(
            StartThreadRequest(
                name = name,
                autoArchiveDuration = _autoArchiveDuration,
                rateLimitPerUser = _rateLimitPerUser,
                message = Optional(messageRequest.request),
                appliedTags = _appliedTags
            ),
            files = messageRequest.files,
        )
    }
}
