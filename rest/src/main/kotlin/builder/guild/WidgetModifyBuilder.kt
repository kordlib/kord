package dev.kord.rest.builder.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildWidgetModifyRequest

public class GuildWidgetModifyBuilder : AuditRequestBuilder<GuildWidgetModifyRequest> {
    override var reason: String? = null

    private var _enabled: OptionalBoolean = OptionalBoolean.Missing

    public var enabled: Boolean? by ::_enabled.delegate()
    private var _channelId: OptionalSnowflake? = OptionalSnowflake.Missing

    public var channelId: Snowflake? by ::_channelId.delegate()

    override fun toRequest(): GuildWidgetModifyRequest =
        GuildWidgetModifyRequest(_enabled, _channelId)
}
