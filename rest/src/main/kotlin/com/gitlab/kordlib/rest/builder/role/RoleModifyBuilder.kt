package com.gitlab.kordlib.rest.builder.role

import com.gitlab.kordlib.common.Color
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.json.request.GuildRoleModifyRequest

@KordDsl
class RoleModifyBuilder : AuditRequestBuilder<GuildRoleModifyRequest> {
    override var reason: String? = null
    var name: String? = null
    var color: Color? = null
    var hoist: Boolean? = null
    var mentionable: Boolean? = null
    var permissions: Permissions? = null

    override fun toRequest(): GuildRoleModifyRequest = GuildRoleModifyRequest(
            name = name,
            color = color?.rgb?.and(0xFFFFFF),
            separate = hoist,
            mentionable = mentionable,
            permissions = permissions
    )
}