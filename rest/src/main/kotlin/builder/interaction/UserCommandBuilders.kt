package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest

@KordDsl
public class UserCommandModifyBuilder : ApplicationCommandModifyBuilder {

    private val state = ApplicationCommandModifyStateHolder()

    override var name: String? by state::name.delegate()

    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()
    override var defaultPermission: Boolean? by state::defaultPermission.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest {
        return ApplicationCommandModifyRequest(
            name = state.name,
            defaultMemberPermissions = state.defaultMemberPermissions,
            defaultPermission = state.defaultPermission
        )
    }
}

@KordDsl
public class UserCommandCreateBuilder(override var name: String) : ApplicationCommandCreateBuilder {
    override val type: ApplicationCommandType
        get() = ApplicationCommandType.User


    private val state = ApplicationCommandModifyStateHolder()

    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()
    override var defaultPermission: Boolean? by state::defaultPermission.delegate()

    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(
            name = name,
            type = type,
            defaultMemberPermissions = state.defaultMemberPermissions,
            dmPermission = state.dmPermission,
            defaultPermission = state.defaultPermission
        )
    }
}
