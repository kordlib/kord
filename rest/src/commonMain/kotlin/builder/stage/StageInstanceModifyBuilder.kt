package dev.kord.rest.builder.stage

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.StageInstanceModifyRequest

@KordDsl
public class StageInstanceModifyBuilder : AuditRequestBuilder<StageInstanceModifyRequest> {

    override var reason: String? = null

    private var _topic: Optional<String> = Optional.Missing()

    /** The topic of the Stage instance (1-120 characters). */
    public var topic: String? by ::_topic.delegate()

    private var _privacyLevel: Optional<StageInstancePrivacyLevel> = Optional.Missing()

    /** The [privacy level][StageInstancePrivacyLevel] of the Stage instance. */
    public var privacyLevel: StageInstancePrivacyLevel? by ::_privacyLevel.delegate()

    override fun toRequest(): StageInstanceModifyRequest = StageInstanceModifyRequest(
        _topic,
        _privacyLevel,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StageInstanceModifyBuilder

        if (reason != other.reason) return false
        if (topic != other.topic) return false
        if (privacyLevel != other.privacyLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reason?.hashCode() ?: 0
        result = 31 * result + (topic?.hashCode() ?: 0)
        result = 31 * result + (privacyLevel?.hashCode() ?: 0)
        return result
    }

}
