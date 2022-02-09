package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.mapList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = ComponentData.Serializer::class)
public sealed class ComponentData {
    public abstract val type: ComponentType

    public companion object {
        public fun from(entity: DiscordComponent): ComponentData = when (entity) {
            is UnknownComponent -> UnknownComponentData(
                entity.type
            )
            is ActionRowComponent -> ActionRowComponentData(
                entity.type,
                entity.components.mapList { from(it) }
            )
            is ButtonComponent -> ButtonComponentData(
                entity.type,
                entity.style,
                entity.label,
                entity.emoji,
                entity.customId,
                entity.url,
                entity.disabled
            )
            is SelectMenuComponent -> SelectMenuComponentData(
                entity.type,
                entity.customId,
                entity.options.map { SelectOptionData.from(it) },
                entity.placeholder,
                entity.minValues,
                entity.maxValues,
                entity.disabled
            )
            is TextInputComponent -> TextInputComponentData(
                entity.type,
                entity.customId,
                entity.style,
                entity.label,
                entity.minLength,
                entity.maxLength,
                entity.required,
                entity.value,
                entity.placeholder
            )
            is ModalResponseTextInputComponent -> ModalResponseTextInputComponentData(
                entity.type,
                entity.customId,
                entity.value
            )
        }
    }

    internal object Serializer : JsonContentPolymorphicSerializer<ComponentData>(ComponentData::class) {
        override fun selectDeserializer(element: JsonElement): KSerializer<out ComponentData> {
            val componentType = element.jsonObject["type"]?.jsonPrimitive?.intOrNull ?: error("Missing component type ID!")

            return when {
                componentType == ComponentType.ActionRow.value -> ActionRowComponentData.serializer()
                componentType == ComponentType.Button.value -> ButtonComponentData.serializer()
                componentType == ComponentType.SelectMenu.value -> SelectMenuComponentData.serializer()
                componentType == ComponentType.TextInput.value && element.jsonObject.containsKey("style") -> TextInputComponentData.serializer()
                componentType == ComponentType.TextInput.value -> ModalResponseTextInputComponentData.serializer()
                else -> UnknownComponentData.serializer()
            }
        }
    }
}

@Serializable
public data class UnknownComponentData(
    public override val type: ComponentType
) : ComponentData()

@Serializable
public data class ActionRowComponentData(
    public override val type: ComponentType,
    public val components: Optional<List<ComponentData>> = Optional.Missing(),
) : ComponentData()

@Serializable
public data class ButtonComponentData(
    public override val type: ComponentType,
    public val style: ButtonStyle,
    public val label: Optional<String> = Optional.Missing(),
    //TODO: turn this emoji into a EmojiData, it's lacking the guild id
    public val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
    public val customId: Optional<String> = Optional.Missing(),
    public val url: Optional<String> = Optional.Missing(),
    public val disabled: OptionalBoolean = OptionalBoolean.Missing
) : ComponentData()

@Serializable
public data class SelectMenuComponentData(
    public override val type: ComponentType,
    @SerialName("custom_id")
    public val customId: Optional<String> = Optional.Missing(),
    public val options: List<SelectOptionData>,
    public val placeholder: Optional<String> = Optional.Missing(),
    @SerialName("min_values")
    public val minValues: OptionalInt = OptionalInt.Missing,
    @SerialName("max_values")
    public val maxValues: OptionalInt = OptionalInt.Missing,
    public val disabled: OptionalBoolean = OptionalBoolean.Missing
) : ComponentData()

@Serializable
public data class TextInputComponentData(
    public override val type: ComponentType,
    @SerialName("custom_id")
    public val customId: String,
    public val style: TextInputStyle,
    public val label: String,
    @SerialName("min_length")
    public val minLength: OptionalInt = OptionalInt.Missing,
    @SerialName("max_length")
    public val maxLength: OptionalInt = OptionalInt.Missing,
    public val required: OptionalBoolean = OptionalBoolean.Missing,
    public val value: Optional<String> = Optional.Missing(),
    public val placeholder: Optional<String> = Optional.Missing()
) : ComponentData()

@Serializable
public data class ModalResponseTextInputComponentData(
    public override val type: ComponentType,
    @SerialName("custom_id")
    public val customId: String,
    public val value: String
) : ComponentData()