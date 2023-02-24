package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.Serializable

/**
 * Represent a [select option structure](https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-option-structure).
 *
 * @param label the user-facing name of the option
 * @param value the dev-defined value of the option
 * @param description an additional description of the option
 * @param emoji the emoji to show in the option
 * @param default whether to render this option as selected by default
 */
@Serializable
public data class DiscordSelectOption(
    val label: String,
    val value: String,
    val description: Optional<String> = Optional.Missing(),
    val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    val default: OptionalBoolean = OptionalBoolean.Missing,
)
