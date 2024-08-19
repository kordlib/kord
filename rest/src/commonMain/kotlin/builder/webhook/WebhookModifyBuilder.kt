package dev.kord.rest.builder.webhook

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.WebhookModifyRequest

@KordDsl
public class WebhookModifyBuilder : AuditRequestBuilder<WebhookModifyRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _avatar: Optional<Image?> = Optional.Missing()
    public var avatar: Image? by ::_avatar.delegate()

    private var _channelId: OptionalSnowflake = OptionalSnowflake.Missing
    public var channelId: Snowflake? by ::_channelId.delegate()

    override fun toRequest(): WebhookModifyRequest = WebhookModifyRequest(
        name = _name,
        avatar = _avatar.map { it.dataUri },
        channelId = _channelId
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WebhookModifyBuilder

        if (reason != other.reason) return false
        if (name != other.name) return false
        if (avatar != other.avatar) return false
        if (channelId != other.channelId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reason?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + (channelId?.hashCode() ?: 0)
        return result
    }

}
