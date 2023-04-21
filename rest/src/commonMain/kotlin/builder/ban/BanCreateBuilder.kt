package dev.kord.rest.builder.ban

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildBanCreateRequest
import kotlin.time.Duration

@KordDsl
public class BanCreateBuilder : AuditRequestBuilder<GuildBanCreateRequest> {

    override var reason: String? = null

    private var _deleteMessageDuration: Optional<Duration> = Optional.Missing()

    /** [Duration] to delete messages for, between 0 and 604800 seconds (7 days). */
    public var deleteMessageDuration: Duration? by ::_deleteMessageDuration.delegate()

    override fun toRequest(): GuildBanCreateRequest = GuildBanCreateRequest(
        deleteMessageSeconds = _deleteMessageDuration,
    )
}
