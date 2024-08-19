package dev.kord.rest.builder.webhook

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.WebhookCreateRequest

@KordDsl
public class WebhookCreateBuilder(public var name: String) : AuditRequestBuilder<WebhookCreateRequest> {
    override var reason: String? = null

    private var _avatar: Optional<Image> = Optional.Missing()
    public var avatar: Image? by ::_avatar.delegate()

    override fun toRequest(): WebhookCreateRequest = WebhookCreateRequest(name, _avatar.map { it.dataUri })

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WebhookCreateBuilder

        if (name != other.name) return false
        if (reason != other.reason) return false
        if (avatar != other.avatar) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (avatar?.hashCode() ?: 0)
        return result
    }

}
