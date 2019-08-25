package com.gitlab.kordlib.core.`object`.builder.channel

import com.gitlab.kordlib.core.`object`.PermissionOverwrite
import com.gitlab.kordlib.core.`object`.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.`object`.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.ChannelModifyPatchRequest

class CategoryModifyBuilder: AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var positon: Int? = null
    var permissionOverwrites: MutableSet<PermissionOverwrite> = mutableSetOf()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = positon,
            permissionOverwrites = permissionOverwrites.map { it.toOverwrite() }
    )
}