package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Permissions
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest

@KordDsl
public interface ApplicationCommandCreateBuilder : LocalizedNameCreateBuilder,
    RequestBuilder<ApplicationCommandCreateRequest> {

    public var defaultMemberPermissions: Permissions?

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
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

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    public var defaultPermission: Boolean?
    public var defaultMemberPermissions: Permissions?
}
