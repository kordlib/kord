package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.rest.json.request.ChannelPermissionEditRequest

@KordDsl
class ChannelPermissionModifyBuilder(private var type: String) : AuditRequestBuilder<ChannelPermissionEditRequest> {

    override var reason: String? = null

    /**
     * The permissions that are explicitly allowed for this channel.
     */
    var allowed: Permissions = Permissions()

    /**
     * The permisssions that are explicitly denied for this channel.
     */
    var denied: Permissions = Permissions()

    override fun toRequest(): ChannelPermissionEditRequest = ChannelPermissionEditRequest(allowed, denied, type)

}
