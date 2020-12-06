package dev.kord.rest.builder.role

import dev.kord.common.Color
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildRoleModifyRequest

@KordDsl
class RoleModifyBuilder : AuditRequestBuilder<GuildRoleModifyRequest> {
    override var reason: String? = null

    private var _color: Optional<Color?> = Optional.Missing()
    var color: Color? by ::_color.delegate()

    private var _hoist: OptionalBoolean? = OptionalBoolean.Missing
    var hoist: Boolean? by ::_hoist.delegate()

    private var _name: Optional<String?> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _mentionable: OptionalBoolean? = OptionalBoolean.Missing
    var mentionable: Boolean? by ::_mentionable.delegate()

    private var _permissions: Optional<Permissions?> = Optional.Missing()
    var permissions: Permissions? by ::_permissions.delegate()

    override fun toRequest(): GuildRoleModifyRequest = GuildRoleModifyRequest(
            name = _name,
            color = _color,
            separate = _hoist,
            mentionable = _mentionable,
            permissions = _permissions
    )
}