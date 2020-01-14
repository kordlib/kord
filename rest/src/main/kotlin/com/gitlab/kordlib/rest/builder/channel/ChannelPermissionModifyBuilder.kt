package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.ChannelPermissionEditRequest

class ChannelPermissionModifyBuilder(private var type: String) : RequestBuilder<ChannelPermissionEditRequest> {

    var reason: String? = null
    var allowed: Permissions = Permissions()
    var denied: Permissions = Permissions()

    override fun toRequest(): ChannelPermissionEditRequest = ChannelPermissionEditRequest(allowed, denied, type)

}
