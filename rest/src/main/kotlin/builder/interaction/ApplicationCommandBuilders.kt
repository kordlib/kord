package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.*
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest
import dev.kord.rest.json.request.ApplicationCommandPermissionsEditRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public interface ApplicationCommandCreateBuilder : LocalizedNameCreateBuilder,
    RequestBuilder<ApplicationCommandCreateRequest> {

    public var defaultMemberPermissions: Permissions?

    @Deprecated("danger default_permission will soon be deprecated. You can instead set default_member_permissions to \"0\" to disable the command by default and/or set dm_permission to false to disable globally-scoped commands inside of DMs with your app")
    public var defaultPermission: Boolean?
    public val type: ApplicationCommandType
}

@KordDsl
public interface GlobalApplicationCommandCreateBuilder : ApplicationCommandCreateBuilder,
    RequestBuilder<ApplicationCommandCreateRequest> {
    public var dmPermission: Boolean?
}

@KordDsl
public interface GlobalApplicationCommandModifyBuilder : ApplicationCommandModifyBuilder,
    RequestBuilder<ApplicationCommandModifyRequest> {
    public var dmPermission: Boolean?
}

@KordDsl
public interface ApplicationCommandModifyBuilder : LocalizedNameModifyBuilder,
    RequestBuilder<ApplicationCommandModifyRequest> {

    @Deprecated("danger default_permission will soon be deprecated. You can instead set default_member_permissions to \"0\" to disable the command by default and/or set dm_permission to false to disable globally-scoped commands inside of DMs with your app")
    public var defaultPermission: Boolean?
    public var defaultMemberPermissions: Permissions?
}

@KordDsl
public class ApplicationCommandPermissionsBulkModifyBuilder(@PublishedApi internal val guildId: Snowflake) :
    RequestBuilder<List<PartialDiscordGuildApplicationCommandPermissions>> {

    @PublishedApi
    internal val permissions: MutableMap<Snowflake, ApplicationCommandPermissionsModifyBuilder> = mutableMapOf()

    public inline fun command(
        commandId: Snowflake,
        builder: ApplicationCommandPermissionsModifyBuilder.() -> Unit,
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        permissions[commandId] = ApplicationCommandPermissionsModifyBuilder(guildId).apply(builder)
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
public class ApplicationCommandPermissionsModifyBuilder(private val guildId: Snowflake) :
    RequestBuilder<ApplicationCommandPermissionsEditRequest> {

    public var permissions: MutableList<DiscordGuildApplicationCommandPermission> = mutableListOf()

    public fun role(id: Snowflake, allow: Boolean = true) {
        permissions.add(
            DiscordGuildApplicationCommandPermission(
                id,
                DiscordGuildApplicationCommandPermission.Type.Role,
                allow
            )
        )
    }

    public fun user(id: Snowflake, allow: Boolean = true) {
        permissions.add(
            DiscordGuildApplicationCommandPermission(
                id,
                DiscordGuildApplicationCommandPermission.Type.User,
                allow
            )
        )
    }

    public fun channel(id: Snowflake, allow: Boolean = true) {
        permissions.add(
            DiscordGuildApplicationCommandPermission(
                id,
                DiscordGuildApplicationCommandPermission.Type.Channel,
                allow
            )
        )
    }

    public fun everyone(allow: Boolean = true): Unit = role(guildId, allow)
    public fun allChannels(allow: Boolean = true): Unit = channel(Snowflake(guildId.value - 1UL), allow)

    override fun toRequest(): ApplicationCommandPermissionsEditRequest =
        ApplicationCommandPermissionsEditRequest(permissions)

}
