package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.InteractionContextType
import dev.kord.common.entity.Permissions
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest

@KordDsl
public interface ApplicationCommandCreateBuilder : LocalizedNameCreateBuilder,
    RequestBuilder<ApplicationCommandCreateRequest> {

    public var defaultMemberPermissions: Permissions?

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'contexts'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'contexts' to empty InteractionContextType ('contexts' is only available for global commands).")
    public var defaultPermission: Boolean?
    public val type: ApplicationCommandType

    /**
     * Disables the command for everyone except admins by default.
     *
     * **This does not ensure normal users cannot execute the command, any admin can change this setting.**
     */
    public fun disableCommandInGuilds() {
        defaultMemberPermissions = Permissions()
    }

    /** Indicates whether the command is age-restricted. Defaults to `false`. */
    public var nsfw: Boolean?
}

@KordDsl
public interface GlobalApplicationCommandCreateBuilder : ApplicationCommandCreateBuilder {
    @Deprecated("'dmPermission' is deprecated in favor of 'contexts'. Setting 'dmPermission' to false can be replaced by setting 'contexts' to empty InteractionContextType ('context' is only available for global commands).")
    public var dmPermission: Boolean?
    public var contexts: MutableList<InteractionContextType>?
}

@KordDsl
public interface GlobalApplicationCommandModifyBuilder : ApplicationCommandModifyBuilder {
    @Deprecated("'dmPermission' is deprecated in favor of 'contexts'. Setting 'dmPermission' to false can be replaced by setting 'contexts' to empty InteractionContextType ('context' is only available for global commands).")
    public var dmPermission: Boolean?
    public var contexts: MutableList<InteractionContextType>?
}

@KordDsl
public interface ApplicationCommandModifyBuilder : LocalizedNameModifyBuilder,
    RequestBuilder<ApplicationCommandModifyRequest> {

    public var defaultMemberPermissions: Permissions?

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'contexts'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'contexts' to empty InteractionContextType ('contexts' is only available for global commands).")
    public var defaultPermission: Boolean?

    /** Indicates whether the command is age-restricted. */
    public var nsfw: Boolean?
}
