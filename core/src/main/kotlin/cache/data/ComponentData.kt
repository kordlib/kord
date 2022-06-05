package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.mapList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("_type") // would otherwise conflict with `type` property
public sealed class ComponentData {
    public abstract val type: ComponentType
    public abstract val label: Optional<String>
    //TODO: turn this emoji into a EmojiData, it's lacking the guild id
    public abstract val emoji: Optional<DiscordPartialEmoji>
    public abstract val customId: Optional<String>
    public abstract val url: Optional<String>
    public abstract val disabled: OptionalBoolean
    public abstract val components: Optional<List<ComponentData>>
    public abstract val placeholder: Optional<String>
    public abstract val minValues: OptionalInt
    public abstract val maxValues: OptionalInt
    public abstract val options: Optional<List<SelectOptionData>>
    public abstract val minLength: OptionalInt
    public abstract val maxLength: OptionalInt
    public abstract val required: OptionalBoolean
    public abstract val value: Optional<String>

    public companion object {
        public fun from(entity: DiscordComponent): ComponentData = with (entity) {
            when (entity) {
                is DiscordChatComponent -> {
                    ChatComponentData(
                        type,
                        entity.style,
                        label,
                        emoji,
                        customId,
                        url,
                        disabled,
                        components.mapList { from(it) },
                        placeholder = placeholder,
                        minValues = minValues,
                        maxValues = maxValues,
                        options = options.mapList { SelectOptionData.from(it) },
                        minLength = minLength,
                        maxLength = maxLength,
                        required = required,
                        value = value
                    )
                }
                is DiscordTextInputComponent -> {
                    TextInputComponentData(
                        type,
                        entity.style,
                        label,
                        emoji,
                        customId,
                        url,
                        disabled,
                        components.mapList { from(it) },
                        placeholder = placeholder,
                        minValues = minValues,
                        maxValues =  maxValues,
                        options = options.mapList { SelectOptionData.from(it) },
                        minLength = minLength,
                        maxLength = maxLength,
                        required = required,
                        value = value
                    )
                }
            }
        }
    }
}

@Serializable
public data class ChatComponentData(
    override val type: ComponentType,
    val style: Optional<ButtonStyle> = Optional.Missing(),
    override val label: Optional<String> = Optional.Missing(),
    //TODO: turn this emoji into a EmojiData, it's lacking the guild id
    override val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    override val customId: Optional<String> = Optional.Missing(),
    override val url: Optional<String> = Optional.Missing(),
    override val disabled: OptionalBoolean = OptionalBoolean.Missing,
    override val components: Optional<List<ComponentData>> = Optional.Missing(),
    override val placeholder: Optional<String> = Optional.Missing(),
    override val minValues: OptionalInt = OptionalInt.Missing,
    override val maxValues: OptionalInt = OptionalInt.Missing,
    override val options: Optional<List<SelectOptionData>> = Optional.Missing(),
    override val minLength: OptionalInt = OptionalInt.Missing,
    override val maxLength: OptionalInt = OptionalInt.Missing,
    override val required: OptionalBoolean = OptionalBoolean.Missing,
    override val value: Optional<String> = Optional.Missing()
) : ComponentData()

@Serializable
public data class TextInputComponentData(
    override val type: ComponentType,
    val style: Optional<TextInputStyle> = Optional.Missing(),
    override val label: Optional<String> = Optional.Missing(),
    //TODO: turn this emoji into a EmojiData, it's lacking the guild id
    override val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    override val customId: Optional<String> = Optional.Missing(),
    override val url: Optional<String> = Optional.Missing(),
    override val disabled: OptionalBoolean = OptionalBoolean.Missing,
    override val components: Optional<List<ComponentData>> = Optional.Missing(),
    override val placeholder: Optional<String> = Optional.Missing(),
    override val minValues: OptionalInt = OptionalInt.Missing,
    override val maxValues: OptionalInt = OptionalInt.Missing,
    override val options: Optional<List<SelectOptionData>> = Optional.Missing(),
    override val minLength: OptionalInt = OptionalInt.Missing,
    override val maxLength: OptionalInt = OptionalInt.Missing,
    override val required: OptionalBoolean = OptionalBoolean.Missing,
    override val value: Optional<String> = Optional.Missing()
) : ComponentData()
