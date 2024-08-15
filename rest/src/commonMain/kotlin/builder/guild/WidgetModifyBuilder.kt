package dev.kord.rest.builder.guild

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildWidgetModifyRequest

@KordDsl
public class GuildWidgetModifyBuilder : AuditRequestBuilder<GuildWidgetModifyRequest> {
    override var reason: String? = null

    private var _enabled: OptionalBoolean = OptionalBoolean.Missing

    public var enabled: Boolean? by ::_enabled.delegate()
    private var _channelId: OptionalSnowflake? = OptionalSnowflake.Missing

    public var channelId: Snowflake? by ::_channelId.delegate()

    override fun toRequest(): GuildWidgetModifyRequest =
        GuildWidgetModifyRequest(_enabled, _channelId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GuildWidgetModifyBuilder

        if (reason != other.reason) return false
        if (enabled != other.enabled) return false
        if (channelId != other.channelId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reason?.hashCode() ?: 0
        result = 31 * result + (enabled?.hashCode() ?: 0)
        result = 31 * result + (channelId?.hashCode() ?: 0)
        return result
    }

}
