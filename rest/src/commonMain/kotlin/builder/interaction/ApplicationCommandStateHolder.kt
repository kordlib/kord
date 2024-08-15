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

    var nsfw: OptionalBoolean = OptionalBoolean.Missing

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ApplicationCommandModifyStateHolder

        if (name != other.name) return false
        if (nameLocalizations != other.nameLocalizations) return false
        if (description != other.description) return false
        if (descriptionLocalizations != other.descriptionLocalizations) return false
        if (options != other.options) return false
        if (defaultMemberPermissions != other.defaultMemberPermissions) return false
        if (dmPermission != other.dmPermission) return false
        if (defaultPermission != other.defaultPermission) return false
        if (nsfw != other.nsfw) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + nameLocalizations.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + descriptionLocalizations.hashCode()
        result = 31 * result + options.hashCode()
        result = 31 * result + defaultMemberPermissions.hashCode()
        result = 31 * result + (dmPermission?.hashCode() ?: 0)
        result = 31 * result + defaultPermission.hashCode()
        result = 31 * result + nsfw.hashCode()
        return result
    }

}
