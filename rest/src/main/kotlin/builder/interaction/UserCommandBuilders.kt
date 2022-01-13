package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest


@KordDsl
class UserCommandModifyBuilder : ApplicationCommandModifyBuilder {

    private val state = ApplicationCommandModifyStateHolder()

    override var name: String? by state::name.delegate()

    override var defaultPermission: Boolean? by state::defaultPermission.delegate()
    override var dmPermissions: Boolean? by state::dmPermissions.delegate()
    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest {
        return ApplicationCommandModifyRequest(
            name = state.name,
            defaultPermission = state.defaultPermission,
            dmPermissions = state.dmPermissions,
            defaultMemberPermissions = state.defaultMemberPermissions
        )

    }

}

@KordDsl
class UserCommandCreateBuilder(override var name: String) : ApplicationCommandCreateBuilder {
    override val type: ApplicationCommandType
        get() = ApplicationCommandType.User


    private val state = ApplicationCommandModifyStateHolder()

    override var defaultPermission: Boolean? by state::defaultPermission.delegate()
    override var dmPermissions: Boolean? by state::dmPermissions.delegate()
    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()

    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(
            name = name,
            type = type,
            defaultPermission = state.defaultPermission,
            dmPermissions = state.dmPermissions,
            defaultMemberPermissions = state.defaultMemberPermissions
        )
    }
}