package com.gitlab.kordlib.rest.builder.webhook

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.common.entity.optional.map
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.json.request.WebhookModifyRequest

@KordDsl
class WebhookModifyBuilder: AuditRequestBuilder<WebhookModifyRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _avatar: Optional<Image> = Optional.Missing()
    var avatar: Image? by ::_avatar.delegate()

    private var _channelId: OptionalSnowflake = OptionalSnowflake.Missing
    var channelId: Snowflake? by ::_channelId.delegate()

    override fun toRequest(): WebhookModifyRequest = WebhookModifyRequest(
            name = _name,
            avatar = _avatar.map { it.dataUri },
            channelId = _channelId
    )
}
