package dev.kord.core.cache.data

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.Serializable

@Serializable
data class ComponentData(
    val type: ComponentType,
    val style: Optional<ButtonStyle> = Optional.Missing(),
    val label: Optional<String> = Optional.Missing(),
    //TODO: turn this emoji into a EmojiData, it's lacking the guild id
    val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    val customId: Optional<String> = Optional.Missing(),
    val url: Optional<String> = Optional.Missing(),
    val disabled: OptionalBoolean = OptionalBoolean.Missing,
    val components: Optional<List<ComponentData>> = Optional.Missing()
) {

    companion object {

        fun from(entity: DiscordComponent) = with(entity) {
            ComponentData(
                type,
                style,
                label,
                emoji,
                customId,
                url,
                disabled
            )
        }

    }

}
