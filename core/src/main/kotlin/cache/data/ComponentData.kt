package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.mapList
import kotlinx.serialization.Serializable

@Serializable
public data class ComponentData(
    val type: ComponentType,
    val style: Optional<ButtonStyle> = Optional.Missing(),
    val label: Optional<String> = Optional.Missing(),
    //TODO: turn this emoji into a EmojiData, it's lacking the guild id
    val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    val customId: Optional<String> = Optional.Missing(),
    val url: Optional<String> = Optional.Missing(),
    val disabled: OptionalBoolean = OptionalBoolean.Missing,
    val components: Optional<List<ComponentData>> = Optional.Missing(),
    val placeholder: Optional<String> = Optional.Missing(),
    val minValues: OptionalInt = OptionalInt.Missing,
    val maxValues: OptionalInt = OptionalInt.Missing,
    val options: Optional<List<SelectOptionData>> = Optional.Missing()
) {

    public companion object {

        public fun from(entity: DiscordComponent): ComponentData = with(entity) {
            ComponentData(
                type,
                style,
                label,
                emoji,
                customId,
                url,
                disabled,
                components.mapList { from(it) },
                placeholder = placeholder,
                minValues = minValues,
                maxValues =  maxValues,
                options = options.mapList { SelectOptionData.from(it) }
            )
        }

    }

}
