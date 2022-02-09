package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.value
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Represent a [interactable component within a message sent in Discord](https://discord.com/developers/docs/interactions/message-components#what-are-components).
 *
 * @property type the [ComponentType] of the component
 * @property style the [ButtonStyle] of the component (if it is a button)
 * @property style the text that appears on the button (if the component is a button)
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

    internal object Serializer : JsonContentPolymorphicSerializer<DiscordComponent>(DiscordComponent::class) {
        override fun selectDeserializer(element: JsonElement): KSerializer<out DiscordComponent> {
            val componentType = element.jsonObject["type"]?.jsonPrimitive?.intOrNull ?: error("Missing component type ID!")

            return when {
                componentType == ComponentType.ActionRow.value -> ActionRowComponent.serializer()
                componentType == ComponentType.Button.value -> ButtonComponent.serializer()
                componentType == ComponentType.SelectMenu.value -> SelectMenuComponent.serializer()
                componentType == ComponentType.TextInput.value && element.jsonObject.containsKey("style") -> TextInputComponent.serializer()
                componentType == ComponentType.TextInput.value -> ModalResponseTextInputComponent.serializer()
                else -> UnknownComponent.serializer()
            }
        }
    }
}

@Serializable
public data class UnknownComponent(
    public override val type: ComponentType
) : DiscordComponent()

@Serializable
public data class ActionRowComponent(
    public override val type: ComponentType,
    public val components: Optional<List<DiscordComponent>> = Optional.Missing(),
) : DiscordComponent()

@Serializable
public data class ButtonComponent(
    public override val type: ComponentType,
    public val style: ButtonStyle,
    public val label: Optional<String> = Optional.Missing(),
    public val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
    public val customId: Optional<String> = Optional.Missing(),
    public val url: Optional<String> = Optional.Missing(),
    public val disabled: OptionalBoolean = OptionalBoolean.Missing
) : DiscordComponent()

@Serializable
public data class SelectMenuComponent(
    public override val type: ComponentType,
    @SerialName("custom_id")
    public val customId: Optional<String> = Optional.Missing(),
    public val options: List<DiscordSelectOption>,
    public val placeholder: Optional<String> = Optional.Missing(),
    @SerialName("min_values")
    public val minValues: OptionalInt = OptionalInt.Missing,
    @SerialName("max_values")
    public val maxValues: OptionalInt = OptionalInt.Missing,
    public val disabled: OptionalBoolean = OptionalBoolean.Missing
) : DiscordComponent()

@Serializable
public data class TextInputComponent(
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
) : DiscordComponent()

@Serializable
public data class ModalResponseTextInputComponent(
    public override val type: ComponentType,
    @SerialName("custom_id")
    public val customId: String,
    public val value: String
) : DiscordComponent()

/**
 * Representation of different [DiscordComponent] types.
 *
 * @property value the raw type value used by the Discord API
 */
@Serializable(with = ComponentType.Serializer::class)
public sealed class ComponentType(public val value: Int) {
    /**
     * Fallback type used for types that haven't been added to Kord yet.
     */
    public class Unknown(value: Int) : ComponentType(value)

    /**
     * A container for other components.
     */
    public object ActionRow : ComponentType(1)

    /**
     * A clickable button.
     */
    public object Button : ComponentType(2)

    /**
     * A select menu for picking from choices.
     */
    public object SelectMenu : ComponentType(3)

    /**
     * 	A text input object.
     */
    public object TextInput : ComponentType(4)

    public companion object Serializer : KSerializer<ComponentType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ComponentType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ComponentType =
            when (val value = decoder.decodeInt()) {
                1 -> ActionRow
                2 -> Button
                3 -> SelectMenu
                else -> Unknown(value)
            }

        override fun serialize(encoder: Encoder, value: ComponentType): Unit = encoder.encodeInt(value.value)
    }
}

/**
 * Representation of different ButtonStyles.
 *
 * A cheat sheet on how the styles look like can be found [here](https://discord.com/assets/7bb017ce52cfd6575e21c058feb3883b.png)
 *
 * @see ComponentType.Button
 */
@Serializable(with = ButtonStyle.Serializer::class)
public sealed class ButtonStyle(public val value: Int) {

    /**
     * A fallback style used for styles that haven't been added to Kord yet.
     */
    public class Unknown(value: Int) : ButtonStyle(value)

    /**
     * Blurple.
     * Requires: [DiscordComponent.customId]
     */
    public object Primary : ButtonStyle(1)

    /**
     * Grey.
     * Requires: [DiscordComponent.customId]
     */
    public object Secondary : ButtonStyle(2)

    /**
     * Green
     * Requires: [DiscordComponent.customId]
     */
    public object Success : ButtonStyle(3)

    /**
     * Red.
     * Requires: [DiscordComponent.customId]
     */
    public object Danger : ButtonStyle(4)

    /**
     * Grey, navigates to an URL.
     * Requires: [DiscordComponent.url]
     */
    public object Link : ButtonStyle(5)

    public companion object Serializer : KSerializer<ButtonStyle> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Button", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ButtonStyle =
            when (val value = decoder.decodeInt()) {
                1 -> Primary
                2 -> Secondary
                3 -> Success
                4 -> Danger
                5 -> Link
                else -> Unknown(value)
            }

        override fun serialize(encoder: Encoder, value: ButtonStyle): Unit = encoder.encodeInt(value.value)
    }
}

/**
 * Representation of different TextInputStyles.
 *
 * @see ComponentType.TextInput
 */
@Serializable(with = TextInputStyle.Serializer::class)
public sealed class TextInputStyle(public val value: Int) {
    /**
     * A fallback style used for styles that haven't been added to Kord yet.
     */
    public class Unknown(value: Int) : TextInputStyle(value)

    /**
     * A single-line input.
     */
    public object Short : TextInputStyle(1)

    /**
     * A multi-line input.
     */
    public object Paragraph : TextInputStyle(2)

    public companion object Serializer : KSerializer<TextInputStyle> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TextInput", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TextInputStyle =
            when (val value = decoder.decodeInt()) {
                1 -> Short
                2 -> Paragraph
                else -> Unknown(value)
            }

        override fun serialize(encoder: Encoder, value: TextInputStyle): Unit = encoder.encodeInt(value.value)
    }
}