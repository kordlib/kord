package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.DiscordGuildApplicationCommandPermission
import dev.kord.common.entity.PartialDiscordGuildApplicationCommandPermissions
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest
import dev.kord.rest.json.request.ApplicationCommandPermissionsEditRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


interface ApplicationCommandCreateBuilder : RequestBuilder<ApplicationCommandCreateRequest> {
    var name: String
    var defaultPermission: Boolean?
    val type: ApplicationCommandType


}


interface ApplicationCommandModifyBuilder : RequestBuilder<ApplicationCommandModifyRequest> {
    var name: String?
    var defaultPermission: Boolean?
}

@KordDsl
class ApplicationCommandPermissionsBulkModifyBuilder :
    RequestBuilder<List<PartialDiscordGuildApplicationCommandPermissions>> {

    @PublishedApi
    internal val permissions = mutableMapOf<Snowflake, ApplicationCommandPermissionsModifyBuilder>()

    inline fun command(
        commandId: Snowflake,
        builder: ApplicationCommandPermissionsModifyBuilder.() -> Unit,
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        permissions[commandId] = ApplicationCommandPermissionsModifyBuilder().apply(builder)
    }

    override fun toRequest(): List<PartialDiscordGuildApplicationCommandPermissions> {
        return permissions.map { (id, builder) ->
            PartialDiscordGuildApplicationCommandPermissions(
                id, builder.permissions.toList()
            )
        }
    }
}

@KordDsl
class ApplicationCommandPermissionsModifyBuilder :
    RequestBuilder<ApplicationCommandPermissionsEditRequest> {

    var permissions = mutableListOf<DiscordGuildApplicationCommandPermission>()

    fun role(id: Snowflake, allow: Boolean = true) {
        permissions.add(
            DiscordGuildApplicationCommandPermission(
                id,
                DiscordGuildApplicationCommandPermission.Type.Role,
                allow
            )
        )
    }

    fun user(id: Snowflake, allow: Boolean = true) {
        permissions.add(
            DiscordGuildApplicationCommandPermission(
                id,
                DiscordGuildApplicationCommandPermission.Type.User,
                allow
            )
        )
    }

    override fun toRequest(): ApplicationCommandPermissionsEditRequest =
        ApplicationCommandPermissionsEditRequest(permissions)

}
