package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permissions
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.ChannelPermissionEditRequest

@KordDsl
public class ChannelPermissionModifyBuilder(private var type: OverwriteType) :
    AuditRequestBuilder<ChannelPermissionEditRequest> {

    override var reason: String? = null

    /**
     * The permissions that are explicitly allowed for this channel.
     */
    public var allowed: Permissions = Permissions()

    /**
     * The permissions that are explicitly denied for this channel.
     */
    public var denied: Permissions = Permissions()

    override fun toRequest(): ChannelPermissionEditRequest = ChannelPermissionEditRequest(allowed, denied, type)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ChannelPermissionModifyBuilder

        if (type != other.type) return false
        if (reason != other.reason) return false
        if (allowed != other.allowed) return false
        if (denied != other.denied) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + allowed.hashCode()
        result = 31 * result + denied.hashCode()
        return result
    }

}
