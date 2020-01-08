package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.builder.KordDsl
import com.gitlab.kordlib.rest.json.request.ChannelModifyPatchRequest

@KordDsl
class CategoryModifyBuilder: AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var positon: Int? = null
    var permissionOverwrites: MutableSet<Overwrite> = mutableSetOf()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = positon,
            permissionOverwrites = permissionOverwrites.toList()
    )
}