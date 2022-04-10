package dev.kord.rest.builder.stage

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.StageInstancePrivacyLevel.GuildOnly
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.StageInstanceCreateRequest

@KordDsl
public class StageInstanceCreateBuilder(
    /** The id of the Stage channel. */
    public var channelId: Snowflake,
    /** The topic of the Stage instance (1-120 characters). */
    public var topic: String,
) : AuditRequestBuilder<StageInstanceCreateRequest> {

    override var reason: String? = null

    private var _privacyLevel: Optional<StageInstancePrivacyLevel> = Optional.Missing()

    /** The [privacy level][StageInstancePrivacyLevel] of the Stage instance (default [GuildOnly]). */
    public var privacyLevel: StageInstancePrivacyLevel? by ::_privacyLevel.delegate()

    private var _sendStartNotification: OptionalBoolean = OptionalBoolean.Missing

    /** Notify @everyone that a Stage instance has started. */
    public var sendStartNotification: Boolean? by ::_sendStartNotification.delegate()

    override fun toRequest(): StageInstanceCreateRequest = StageInstanceCreateRequest(
        channelId,
        topic,
        _privacyLevel,
        _sendStartNotification,
    )
}
