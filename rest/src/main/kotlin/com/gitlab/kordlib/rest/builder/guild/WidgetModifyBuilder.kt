package com.gitlab.kordlib.rest.builder.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.delegate.provideDelegate
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.GuildWidgetModifyRequest

class GuildWidgetModifyBuilder: RequestBuilder<GuildWidgetModifyRequest> {
    private var _enabled: OptionalBoolean = OptionalBoolean.Missing
    private var _channelId: OptionalSnowflake? = OptionalSnowflake.Missing

    var enabled: Boolean? by this::_enabled
    var channelId: Snowflake? by this::_channelId

    override fun toRequest(): GuildWidgetModifyRequest =
            GuildWidgetModifyRequest(_enabled, _channelId)
}
