package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.optionalInt
import dev.kord.common.entity.optional.value
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.serializer

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
public data class DiscordComponent(
    val type: ComponentType,
    val style: Optional<ComponentStyle> = Optional.Missing(),
    val label: Optional<String> = Optional.Missing(),
    val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
    val customId: Optional<String> = Optional.Missing(),
    val url: Optional<String> = Optional.Missing(),
    val disabled: OptionalBoolean = OptionalBoolean.Missing,
    val components: Optional<List<DiscordComponent>> = Optional.Missing(),
    val options: Optional<List<DiscordSelectOption>> = Optional.Missing(),
    val placeholder: Optional<String> = Optional.Missing(),
    @SerialName("min_values")
    val minValues: OptionalInt = OptionalInt.Missing,
    @SerialName("max_values")
    val maxValues: OptionalInt = OptionalInt.Missing,
    @SerialName("min_length")
    val minLength: OptionalInt = OptionalInt.Missing,
    @SerialName("max_length")
    val maxLength: OptionalInt = OptionalInt.Missing,
    val required: OptionalBoolean = OptionalBoolean.Missing,
    val value: Optional<String> = Optional.Missing()
) {
    internal object Serializer : KSerializer<DiscordComponent> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Kord.DiscordComponent") {
            element("type", ComponentType.serializer().descriptor, isOptional = false)
            element("style", OptionalInt.serializer().descriptor, isOptional = true)
            element("label", Optional.serializer(String.serializer()).descriptor, isOptional = true)
            element("emoji", Optional.serializer(DiscordPartialEmoji.serializer()).descriptor, isOptional = true)
            element("custom_id", Optional.serializer(String.serializer()).descriptor, isOptional = true)
            element("url", Optional.serializer(String.serializer()).descriptor, isOptional = true)
            element("disabled", OptionalBoolean.serializer().descriptor, isOptional = true)
            element(
                "components",
                serializer<Optional<JsonArray>>().descriptor, // Can't use DiscordComponent here because kotlinx.serialization doesn't like it! (probably because it is a recursive reference)
                isOptional = true
            )
            element(
                "options",
                serializer<Optional<JsonArray>>().descriptor,
                isOptional = true
            )
            element("placeholder", Optional.serializer(String.serializer()).descriptor, isOptional = true)
            element("min_values", OptionalInt.serializer().descriptor, isOptional = true)
            element("max_values", OptionalInt.serializer().descriptor, isOptional = true)
            element("min_length", OptionalInt.serializer().descriptor, isOptional = true)
            element("max_length", OptionalInt.serializer().descriptor, isOptional = true)
            element("required", OptionalBoolean.serializer().descriptor, isOptional = true)
            element("value", Optional.serializer(String.serializer()).descriptor, isOptional = true)
        }

        override fun deserialize(decoder: Decoder): DiscordComponent {
            decoder as? JsonDecoder ?: error("DiscordComponent can only be deserialized with a JsonDecoder")
            var type: ComponentType? = null
            var style: Optional<ComponentStyle>? = null
            var label: Optional<String>? = null
            var discordPartialEmoji: Optional<DiscordPartialEmoji>? = null
            var customId: Optional<String>? = null
            var url: Optional<String>? = null
            var disabled: OptionalBoolean? = null
            var components: Optional<List<DiscordComponent>>? = null
            var options: Optional<List<DiscordSelectOption>>? = null
            var placeholder: Optional<String>? = null
            var minValues: OptionalInt? = null
            var maxValues: OptionalInt? = null
            var minLength: OptionalInt? = null
            var maxLength: OptionalInt? = null
            var required: OptionalBoolean? = null
            var value: Optional<String>? = null

            decoder.decodeStructure(descriptor) {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> type = decodeSerializableElement(descriptor, index, ComponentType.serializer())
                        1 -> {
                            val styleId = decodeSerializableElement(descriptor, index, OptionalInt.serializer()).value
                            if (styleId == null)
                                style = Optional.Missing()
                            else {
                                when (type) {
                                    ComponentType.Button -> when (styleId) {
                                        1 -> ComponentStyle.Button.Primary
                                        2 -> ComponentStyle.Button.Secondary
                                        3 -> ComponentStyle.Button.Success
                                        4 -> ComponentStyle.Button.Danger
                                        5 -> ComponentStyle.Button.Link
                                        else -> ComponentStyle.Button.Unknown(styleId)
                                    }
                                    ComponentType.TextInput -> when (styleId) {
                                        1 -> ComponentStyle.TextInput.Short
                                        2 -> ComponentStyle.TextInput.Paragraph
                                        else -> ComponentStyle.TextInput.Unknown(styleId)
                                    }
                                    else -> ComponentStyle.Unknown(styleId)
                                }
                            }
                        }
                        2 -> label =
                            decodeSerializableElement(descriptor, index, Optional.serializer(String.serializer()))
                        3 -> discordPartialEmoji = decodeSerializableElement(
                            descriptor,
                            index,
                            Optional.serializer(DiscordPartialEmoji.serializer())
                        )
                        4 -> customId =
                            decodeSerializableElement(descriptor, index, Optional.serializer(String.serializer()))
                        5 -> url =
                            decodeSerializableElement(descriptor, index, Optional.serializer(String.serializer()))
                        6 -> disabled = decodeSerializableElement(descriptor, index, OptionalBoolean.serializer())
                        7 -> components = decodeSerializableElement(
                            descriptor,
                            index,
                            Optional.serializer(ListSerializer(DiscordComponent.serializer()))
                        )
                        8 -> options = decodeSerializableElement(
                            descriptor,
                            index,
                            Optional.serializer(ListSerializer(DiscordSelectOption.serializer()))
                        )
                        9 -> placeholder =
                            decodeSerializableElement(descriptor, index, Optional.serializer(String.serializer()))
                        10 -> minValues = decodeSerializableElement(descriptor, index, OptionalInt.serializer())
                        11 -> maxValues = decodeSerializableElement(descriptor, index, OptionalInt.serializer())
                        12 -> minLength = decodeSerializableElement(descriptor, index, OptionalInt.serializer())
                        13 -> maxLength = decodeSerializableElement(descriptor, index, OptionalInt.serializer())
                        14 -> required = decodeSerializableElement(descriptor, index, OptionalBoolean.serializer())
                        15 -> value =
                            decodeSerializableElement(descriptor, index, Optional.serializer(String.serializer()))
                        CompositeDecoder.DECODE_DONE -> return@decodeStructure
                        else -> throw SerializationException("unknown index: $index")
                    }
                }
            }

            return DiscordComponent(
                type!!,
                style ?: Optional.Missing(),
                label ?: Optional.Missing(),
                discordPartialEmoji ?: Optional.Missing(),
                customId ?: Optional.Missing(),
                url ?: Optional.Missing(),
                disabled ?: OptionalBoolean.Missing,
                components ?: Optional.Missing(),
                options ?: Optional.Missing(),
                placeholder ?: Optional.Missing(),
                minValues ?: OptionalInt.Missing,
                maxValues ?: OptionalInt.Missing,
                minLength ?: OptionalInt.Missing,
                maxLength ?: OptionalInt.Missing,
                required ?: OptionalBoolean.Missing,
                value ?: Optional.Missing()
            )
        }

        @OptIn(InternalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: DiscordComponent) {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, ComponentType.serializer(), value.type)
                encodeSerializeElementIfNotMissing(
                    descriptor,
                    1,
                    value.style.value?.value?.optionalInt() ?: OptionalInt.Missing
                )
                encodeSerializeElementIfNotMissing(descriptor, 2, value.label)
                encodeSerializeElementIfNotMissing(
                    descriptor,
                    3,
                    value.emoji
                )
                encodeSerializeElementIfNotMissing(descriptor, 4, value.customId)
                encodeSerializeElementIfNotMissing(descriptor, 5, value.url)
                encodeSerializeElementIfNotMissing(descriptor, 6, value.disabled)
                encodeSerializeElementIfNotMissing(
                    descriptor,
                    7,
                    value.components
                )
                encodeSerializeElementIfNotMissing(
                    descriptor,
                    8,
                    value.options
                )
                encodeSerializeElementIfNotMissing(descriptor, 9, value.placeholder)
                encodeSerializeElementIfNotMissing(descriptor, 10, value.minValues)
                encodeSerializeElementIfNotMissing(descriptor, 11, value.maxValues)
                encodeSerializeElementIfNotMissing(descriptor, 12, value.minLength)
                encodeSerializeElementIfNotMissing(descriptor, 13, value.maxLength)
                encodeSerializeElementIfNotMissing(descriptor, 14, value.required)
                encodeSerializeElementIfNotMissing(descriptor, 15, value.value)
            }
        }

        private fun CompositeEncoder.encodeSerializeElementIfNotMissing(
            descriptor: SerialDescriptor,
            index: Int,
            value: OptionalInt
        ) {
            if (value != OptionalInt.Missing)
                encodeSerializableElement(
                    descriptor,
                    index,
                    OptionalInt.serializer(),
                    value
                )
        }

        private fun CompositeEncoder.encodeSerializeElementIfNotMissing(
            descriptor: SerialDescriptor,
            index: Int,
            value: OptionalBoolean
        ) {
            if (value != OptionalBoolean.Missing)
                encodeSerializableElement(
                    descriptor,
                    index,
                    OptionalBoolean.serializer(),
                    value
                )
        }

        private inline fun <reified T> CompositeEncoder.encodeSerializeElementIfNotMissing(
            descriptor: SerialDescriptor,
            index: Int,
            value: Optional<T>,
            serializer: KSerializer<T> = serializer<T>()
        ) {
            if (value !is Optional.Missing<T>)
                encodeSerializableElement(
                    descriptor,
                    index,
                    Optional.serializer(serializer),
                    value
                )
        }
    }
}

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
 * Representation of different ComponentStyles.
 */
@Serializable(with = ComponentStyle.Serializer::class)
public sealed class ComponentStyle(public val value: Int) {
    /**
     * A fallback style used for styles that haven't been added to Kord yet.
     */
    public class Unknown(value: Int) : Button(value)

    public companion object Serializer : KSerializer<ComponentStyle> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Button", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Nothing = error("Can't deserialize a ComponentStyle without knowing the component type!")

        override fun serialize(encoder: Encoder, value: ComponentStyle): Unit = encoder.encodeInt(value.value)
    }

    /**
     * Representation of different ButtonStyles.
     *
     * A cheat sheet on how the styles look like can be found [here](https://discord.com/assets/7bb017ce52cfd6575e21c058feb3883b.png)
     *
     * @see ComponentType.Button
     */
    public sealed class Button(value: Int) : ComponentStyle(value) {

        /**
         * A fallback style used for styles that haven't been added to Kord yet.
         */
        public class Unknown(value: Int) : Button(value)

        /**
         * Blurple.
         * Requires: [DiscordComponent.customId]
         */
        public object Primary : Button(1)

        /**
         * Grey.
         * Requires: [DiscordComponent.customId]
         */
        public object Secondary : Button(2)

        /**
         * Green
         * Requires: [DiscordComponent.customId]
         */
        public object Success : Button(3)

        /**
         * Red.
         * Requires: [DiscordComponent.customId]
         */
        public object Danger : Button(4)

        /**
         * Grey, navigates to an URL.
         * Requires: [DiscordComponent.url]
         */
        public object Link : Button(5)

        public companion object Serializer : KSerializer<Button> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Button", PrimitiveKind.INT)

            override fun deserialize(decoder: Decoder): Button =
                when (val value = decoder.decodeInt()) {
                    1 -> Primary
                    2 -> Secondary
                    3 -> Success
                    4 -> Danger
                    5 -> Link
                    else -> Unknown(value)
                }

            override fun serialize(encoder: Encoder, value: Button): Unit = encoder.encodeInt(value.value)
        }
    }

    /**
     * Representation of different TextInputStyles.
     *
     * @see ComponentType.TextInput
     */
    @Serializable(with = TextInput.Serializer::class)
    public sealed class TextInput(value: Int) : ComponentStyle(value) {
        /**
         * A fallback style used for styles that haven't been added to Kord yet.
         */
        public class Unknown(value: Int) : TextInput(value)

        /**
         * A single-line input.
         */
        public object Short : TextInput(1)

        /**
         * A multi-line input.
         */
        public object Paragraph : TextInput(2)

        public companion object Serializer : KSerializer<TextInput> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TextInput", PrimitiveKind.INT)

            override fun deserialize(decoder: Decoder): TextInput =
                when (val value = decoder.decodeInt()) {
                    1 -> Short
                    2 -> Paragraph
                    else -> Unknown(value)
                }

            override fun serialize(encoder: Encoder, value: TextInput): Unit = encoder.encodeInt(value.value)
        }
    }
}
