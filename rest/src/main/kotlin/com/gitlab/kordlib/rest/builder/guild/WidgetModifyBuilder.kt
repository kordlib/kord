package com.gitlab.kordlib.rest.builder.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.GuildWidgetModifyRequest

class GuildWidgetModifyBuilder: RequestBuilder<GuildWidgetModifyRequest> {
    private var _enabled: OptionalBoolean = OptionalBoolean.Missing
    var enabled: Boolean? by ::_enabled.delegate()

    private var _channelId: OptionalSnowflake? = OptionalSnowflake.Missing
    var channelId: Snowflake? by ::_channelId.delegate()

    override fun toRequest(): GuildWidgetModifyRequest =
            GuildWidgetModifyRequest(_enabled, _channelId)
}
