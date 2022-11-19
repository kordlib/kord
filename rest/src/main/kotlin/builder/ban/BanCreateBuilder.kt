package dev.kord.rest.builder.ban

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildBanCreateRequest
import kotlin.DeprecationLevel.ERROR
import kotlin.time.Duration

@KordDsl
public class BanCreateBuilder : AuditRequestBuilder<GuildBanCreateRequest> {

    override var reason: String? = null

    private var _deleteMessagesDays: OptionalInt = OptionalInt.Missing

    /**
     * The number of days to delete messages for (0-7).
     *
     * @suppress
     */
    @Deprecated("Use 'deleteMessageDuration' instead.", ReplaceWith("this.deleteMessageDuration"), level = ERROR)
    public var deleteMessagesDays: Int? by ::_deleteMessagesDays.delegate()

    private var _deleteMessageDuration: Optional<Duration> = Optional.Missing()

    /** [Duration] to delete messages for, between 0 and 604800 seconds (7 days). */
    public var deleteMessageDuration: Duration? by ::_deleteMessageDuration.delegate()

    override fun toRequest(): GuildBanCreateRequest = @Suppress("DEPRECATION_ERROR") GuildBanCreateRequest(
        deleteMessagesDays = _deleteMessagesDays,
        deleteMessageSeconds = _deleteMessageDuration,
    )
}
