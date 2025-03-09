package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.entity.InteractionContextType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName


/**
 * Utility container for application modify builder. This class contains
 * all possible fields as optionals.
 */
internal class ApplicationCommandModifyStateHolder {

    var name: Optional<String> = Optional.Missing()
    var nameLocalizations: Optional<MutableMap<Locale, String>?> = Optional.Missing()

    var description: Optional<String> = Optional.Missing()
    var descriptionLocalizations: Optional<MutableMap<Locale, String>?> = Optional.Missing()

    var options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()

    var defaultMemberPermissions: Optional<Permissions?> = Optional.Missing()
    @Deprecated("'dmPermission' is deprecated in favor of 'contexts'. Setting 'dmPermission' to false can be replaced by setting 'contexts' to empty. ('contexts' is only available to global commands).")
    var dmPermission: OptionalBoolean? = OptionalBoolean.Missing


    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'contexts'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'contexts' to empty InteractionContextType ('contexts' is only available for global commands).")
    @SerialName("default_permission")
    var defaultPermission: OptionalBoolean = OptionalBoolean.Missing

    var nsfw: OptionalBoolean = OptionalBoolean.Missing

    var contexts: Optional<MutableList<InteractionContextType>> = Optional.Missing()
}
