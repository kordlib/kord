package com.gitlab.kordlib.core.`object`.builder.role

import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.core.`object`.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.`object`.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.GuildRoleModifyRequest
import java.awt.Color

class RoleModifyBuilder : AuditRequestBuilder<GuildRoleModifyRequest> {
    override var reason: String? = null
    var name: String? = null
    var color: Color? = null
    var hoist: Boolean? = null
    var mentionable: Boolean? = null
    var permissions: Permissions? = null

    override fun toRequest(): GuildRoleModifyRequest = GuildRoleModifyRequest(
            name = name,
            color = color?.rgb,
            separate = hoist,
            mentionable = mentionable,
            permissions = permissions
    )
}