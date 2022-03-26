package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName


/**
 * Utility container for application modify builder. This class contains
 * all possible fields as optionals.
 */
internal class ApplicationCommandModifyStateHolder {

    var name: Optional<String> = Optional.Missing()
    var nameLocalizations: MutableMap<Locale, String>? = null

    var description: Optional<String> = Optional.Missing()
    var descriptionLocalizations: MutableMap<Locale, String>? = null

    var options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()

    @SerialName("default_permission")
    var defaultPermission: OptionalBoolean = OptionalBoolean.Missing

}
