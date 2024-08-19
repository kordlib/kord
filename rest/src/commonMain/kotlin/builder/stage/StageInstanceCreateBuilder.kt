package dev.kord.rest.builder.stage

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.StageInstancePrivacyLevel.GuildOnly
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
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

    private var _guildScheduledEventId: OptionalSnowflake = OptionalSnowflake.Missing

    /** The guild scheduled event associated with this Stage instance. */
    public var guildScheduledEventId: Snowflake? by ::_guildScheduledEventId.delegate()

    override fun toRequest(): StageInstanceCreateRequest = StageInstanceCreateRequest(
        channelId,
        topic,
        _privacyLevel,
        _sendStartNotification,
        _guildScheduledEventId,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StageInstanceCreateBuilder

        if (channelId != other.channelId) return false
        if (topic != other.topic) return false
        if (reason != other.reason) return false
        if (privacyLevel != other.privacyLevel) return false
        if (sendStartNotification != other.sendStartNotification) return false
        if (guildScheduledEventId != other.guildScheduledEventId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelId.hashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (privacyLevel?.hashCode() ?: 0)
        result = 31 * result + (sendStartNotification?.hashCode() ?: 0)
        result = 31 * result + (guildScheduledEventId?.hashCode() ?: 0)
        return result
    }

}
