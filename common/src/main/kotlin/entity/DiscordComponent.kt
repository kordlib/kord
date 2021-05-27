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

@KordPreview
@Serializable(with = ComponentType.Serializer::class)
sealed class ComponentType(val value: Int) {

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

@KordPreview
@Serializable(with = ButtonStyle.Serializer::class)
sealed class ButtonStyle(val value: Int) {

    class Unknown(value: Int) : ButtonStyle(value)
    object Primary : ButtonStyle(1)
    object Secondary : ButtonStyle(2)
    object Success : ButtonStyle(3)
    object Danger : ButtonStyle(4)
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
