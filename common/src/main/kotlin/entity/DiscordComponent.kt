@file:GenerateKordEnum(
    name = "ComponentType", valueType = INT,
    deprecatedSerializerName = "Serializer",
    entries = [
        Entry("ActionRow", intValue = 1, kDoc = "A container for other components."),
        Entry("Button", intValue = 2, kDoc = "A button object."),
        Entry("SelectMenu", intValue = 3, kDoc = "A select menu for picking from choices."),
        Entry("TextInput", intValue = 4, kDoc = "A text input object."),
    ],
)

@file:GenerateKordEnum(
    name = "ButtonStyle", valueType = INT,
    deprecatedSerializerName = "Serializer",
    kDoc = "Style of a [button][dev.kord.common.entity.ComponentType.Button].\n\nA preview of the different styles " +
            "can be found " +
            "[here](https://discord.com/developers/docs/interactions/message-components#button-object-button-styles).",
    entries = [
        Entry("Primary", intValue = 1, kDoc = "Blurple."),
        Entry("Secondary", intValue = 2, kDoc = "Grey."),
        Entry("Success", intValue = 3, kDoc = "Green."),
        Entry("Danger", intValue = 4, kDoc = "Red."),
        Entry("Link", intValue = 5, kDoc = "Grey, navigates to a URL."),
    ],
)

@file:GenerateKordEnum(
    name = "TextInputStyle", valueType = INT,
    kDoc = "Style of a [text·input][dev.kord.common.entity.ComponentType.TextInput]",
    entries = [
        Entry("Short", intValue = 1, kDoc = "A single-line input."),
        Entry("Paragraph", intValue = 2, kDoc = "A multi-line input."),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Represent a [interactable component within a message sent in Discord](https://discord.com/developers/docs/interactions/message-components#what-are-components).
 *
 * @property type the [ComponentType] of the component
 * @property style the [ButtonStyle] of the component (if it is a button)
 * @property emoji an [DiscordPartialEmoji] that appears on the button (if the component is a button)
 * @property customId a developer-defined identifier for the button, max 100 characters
 * @property url a url for link-style buttons
 * @property disabled whether the button is disabled, default `false`
 * @property components a list of child components (for action rows)
 * @property options the select menu options
 * @property placeholder the placeholder text for the select menu
 * @property minValues the minimum amount of [options] allowed
 * @property maxValues the maximum amount of [options] allowed
 * @property minLength the minimum input length for a text input, min 0, max 4000.
 * @property maxLength the maximum input length for a text input, min 1, max 4000.
 * @property required whether this component is required to be filled, default false.
 * @property value a pre-filled value for this component, max 4000 characters.
 */
@Serializable(with = DiscordComponent.Serializer::class)
public sealed class DiscordComponent {
    public abstract val type: ComponentType
    public abstract val label: Optional<String>
    public abstract val emoji: Optional<DiscordPartialEmoji>
    @SerialName("custom_id")
    public abstract val customId: Optional<String>
    public abstract val url: Optional<String>
    public abstract val disabled: OptionalBoolean
    public abstract val components: Optional<List<DiscordComponent>>
    public abstract val options: Optional<List<DiscordSelectOption>>
    public abstract val placeholder: Optional<String>
    @SerialName("min_values")
    public abstract val minValues: OptionalInt
    @SerialName("max_values")
    public abstract val maxValues: OptionalInt
    @SerialName("min_length")
    public abstract val minLength: OptionalInt
    @SerialName("max_length")
    public abstract val maxLength: OptionalInt
    public abstract val required: OptionalBoolean
    public abstract val value: Optional<String>

    internal object Serializer : JsonContentPolymorphicSerializer<DiscordComponent>(DiscordComponent::class) {
        override fun selectDeserializer(element: JsonElement): KSerializer<out DiscordComponent> {
            val componentType = element.jsonObject["type"]?.jsonPrimitive?.intOrNull ?: error("Missing component type ID!")

            return when (componentType) {
                ComponentType.TextInput.value -> DiscordTextInputComponent.serializer()
                else -> DiscordChatComponent.serializer()
            }
        }
    }
}

@Serializable
public data class DiscordChatComponent(
     override val type: ComponentType,
     val style: Optional<ButtonStyle> = Optional.Missing(),
     override val label: Optional<String> = Optional.Missing(),
     override val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
     override val customId: Optional<String> = Optional.Missing(),
     override val url: Optional<String> = Optional.Missing(),
     override val disabled: OptionalBoolean = OptionalBoolean.Missing,
     override val components: Optional<List<DiscordComponent>> = Optional.Missing(),
     override val options: Optional<List<DiscordSelectOption>> = Optional.Missing(),
     override val placeholder: Optional<String> = Optional.Missing(),
    @SerialName("min_values")
     override val minValues: OptionalInt = OptionalInt.Missing,
    @SerialName("max_values")
     override val maxValues: OptionalInt = OptionalInt.Missing,
    @SerialName("min_length")
     override val minLength: OptionalInt = OptionalInt.Missing,
    @SerialName("max_length")
     override val maxLength: OptionalInt = OptionalInt.Missing,
     override val required: OptionalBoolean = OptionalBoolean.Missing,
     override val value: Optional<String> = Optional.Missing()
) : DiscordComponent()

@Serializable
public data class DiscordTextInputComponent(
     override val type: ComponentType,
    public val style: Optional<TextInputStyle> = Optional.Missing(),
     override val label: Optional<String> = Optional.Missing(),
     override val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
     override val customId: Optional<String> = Optional.Missing(),
     override val url: Optional<String> = Optional.Missing(),
     override val disabled: OptionalBoolean = OptionalBoolean.Missing,
     override val components: Optional<List<DiscordComponent>> = Optional.Missing(),
     override val options: Optional<List<DiscordSelectOption>> = Optional.Missing(),
     override val placeholder: Optional<String> = Optional.Missing(),
    @SerialName("min_values")
     override val minValues: OptionalInt = OptionalInt.Missing,
    @SerialName("max_values")
     override val maxValues: OptionalInt = OptionalInt.Missing,
    @SerialName("min_length")
     override val minLength: OptionalInt = OptionalInt.Missing,
    @SerialName("max_length")
     override val maxLength: OptionalInt = OptionalInt.Missing,
     override val required: OptionalBoolean = OptionalBoolean.Missing,
     override val value: Optional<String> = Optional.Missing()
) : DiscordComponent()
