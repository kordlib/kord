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


    @Deprecated(
        "'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'." +
            " Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty " +
            "Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands). The " +
            "deprecation level will be raised to HIDDEN in 0.19.0 and this declaration will be removed in 0.20.0",
        level = DeprecationLevel.ERROR
    )
    @SerialName("default_permission")
    var defaultPermission: OptionalBoolean = OptionalBoolean.Missing

    var nsfw: OptionalBoolean = OptionalBoolean.Missing
}
