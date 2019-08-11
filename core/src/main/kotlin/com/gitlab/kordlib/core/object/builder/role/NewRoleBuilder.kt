package com.gitlab.kordlib.core.`object`.builder.role

import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.rest.json.request.GuildRoleCreateRequest
import java.awt.Color

class NewRoleBuilder (
        var color: Color? = null,
        var hoist: Boolean? = null,
        var name: String? = null,
        var mentionable: Boolean? = null,
        var permissions: Permissions? = null
) {
    fun toRequest(): GuildRoleCreateRequest = GuildRoleCreateRequest(
            color = color?.rgb ?: 0,
            separate = hoist ?: false,
            name = name,
            mentionable = mentionable ?: false,
            permissions = permissions
    )
}