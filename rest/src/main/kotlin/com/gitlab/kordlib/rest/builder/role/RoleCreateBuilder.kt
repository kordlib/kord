package com.gitlab.kordlib.rest.builder.role

import com.gitlab.kordlib.common.Color
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.json.request.GuildRoleCreateRequest

@KordDsl
class RoleCreateBuilder : AuditRequestBuilder<GuildRoleCreateRequest>{
    override var reason: String? = null
    var color: Color? = null
    var hoist: Boolean = false
    var name: String? = null
    var mentionable: Boolean = false
    var permissions: Permissions? = null

    override fun toRequest(): GuildRoleCreateRequest = GuildRoleCreateRequest(
            color = color?.rgb?.and(0xFFFFFF) ?: 0,
            separate = hoist,
            name = name,
            mentionable = mentionable,
            permissions = permissions
    )
}