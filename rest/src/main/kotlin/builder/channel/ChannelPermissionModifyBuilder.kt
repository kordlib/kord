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

}
