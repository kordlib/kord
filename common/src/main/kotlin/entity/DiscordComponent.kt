package dev.kord.common.entity

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represent a [intractable component within a message sent in Discord](https://discord.com/developers/docs/interactions/message-components#what-are-components).
 *
 * @property type the [ComponentType] of the component
 * @property style the [ButtonStyle] of the component (if it is a button)
 * @property style the text that appears on the button (if the component is a button)
 * @property emoji an [DiscordPartialEmoji] that appears on the button (if the component is a button)
 * @property customId a developer-defined identifier for the button, max 100 characters
 * @property url a url for link-style buttons
 * @property disabled whether the button is disabled, default `false`
 * @property components a list of child components (for action rows)
 */
@KordPreview
@Serializable
data class DiscordComponent(
    val type: ComponentType,
    val style: Optional<ButtonStyle> = Optional.Missing(),
    val label: Optional<String> = Optional.Missing(),
    val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
    val customId: Optional<String> = Optional.Missing(),
    val url: Optional<String> = Optional.Missing(),
    val disabled: OptionalBoolean = OptionalBoolean.Missing,
    val components: Optional<List<DiscordComponent>> = Optional.Missing()
)

/**
 * Representation of different [DiscordComponent] types.
 *
 * @property value the raw type value used by the Discord API
 */
@KordPreview
@Serializable(with = ComponentType.Serializer::class)
sealed class ComponentType(val value: Int) {

    /**
     * Fallback type used for types that haven't been added to Kord yet.
     */
    class Unknown(value: Int) : ComponentType(value)

    /**
     * A container for other components.
     */
    object ActionRow : ComponentType(1)

    /**
     * A clickable button.
     */
    object Button : ComponentType(2)

    companion object Serializer : KSerializer<ComponentType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ComponentType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ComponentType =
            when (val value = decoder.decodeInt()) {
                1 -> ActionRow
                2 -> Button
                else -> Unknown(value)
            }

        override fun serialize(encoder: Encoder, value: ComponentType) = encoder.encodeInt(value.value)
    }
}

/**
 * Representation of different ButtonStyles.
 *
 * A cheat sheet on how the styles look like can be found [here](https://discord.com/assets/7bb017ce52cfd6575e21c058feb3883b.png)
 *
 * @see ComponentType.Button
 */
@KordPreview
@Serializable(with = ButtonStyle.Serializer::class)
sealed class ButtonStyle(val value: Int) {

    /**
     * A fallback style used for styles that haven't been added to Kord yet.
     */
    class Unknown(value: Int) : ButtonStyle(value)

    /**
     * Blurple.
     * Requires: [DiscordComponent.customId]
     */
    object Primary : ButtonStyle(1)

    /**
     * Grey.
     * Requires: [DiscordComponent.customId]
     */
    object Secondary : ButtonStyle(2)

    /**
     * Green
     * Requires: [DiscordComponent.customId]
     */
    object Success : ButtonStyle(3)

    /**
     * Red.
     * Requires: [DiscordComponent.customId]
     */
    object Danger : ButtonStyle(4)

    /**
     * Grey, navigates to an URL.
     * Requires: [DiscordComponent.url]
     */
    object Link : ButtonStyle(5)

    companion object Serializer : KSerializer<ButtonStyle> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ButtonStyle", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ButtonStyle =
            when (val value = decoder.decodeInt()) {
                1 -> Primary
                2 -> Secondary
                3 -> Success
                4 -> Danger
                5 -> Link
                else -> Unknown(value)
            }

        override fun serialize(encoder: Encoder, value: ButtonStyle) = encoder.encodeInt(value.value)
    }
}
