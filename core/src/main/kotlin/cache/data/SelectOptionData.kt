package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.DiscordSelectOption
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.Serializable

@Serializable
data class SelectOptionData(
    val label: String,
    val value: String,
    val description: Optional<String> = Optional.Missing(),
    val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    val default: OptionalBoolean = OptionalBoolean.Missing
) {

    companion object {

        fun from(entity: DiscordSelectOption): SelectOptionData = with(entity){
            SelectOptionData(
                label = label,
                value = value,
                description = description,
                emoji = emoji,
                default = default
            )
        }

    }

}
