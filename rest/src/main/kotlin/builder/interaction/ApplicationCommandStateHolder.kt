package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
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
    var dmPermission: OptionalBoolean? = OptionalBoolean.Missing


    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    @SerialName("default_permission")
    var defaultPermission: OptionalBoolean = OptionalBoolean.Missing

}
