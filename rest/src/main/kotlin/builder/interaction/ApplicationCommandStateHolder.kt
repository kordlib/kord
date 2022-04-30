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


    @Deprecated("danger default_permission will soon be deprecated. You can instead set default_member_permissions to \"0\" to disable the command by default and/or set dm_permission to false to disable globally-scoped commands inside of DMs with your app")
    @SerialName("default_permission")
    var defaultPermission: OptionalBoolean = OptionalBoolean.Missing

}
