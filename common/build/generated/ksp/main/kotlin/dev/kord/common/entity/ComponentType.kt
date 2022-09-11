// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral"))

package dev.kord.common.entity

import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.ReplaceWith
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmField
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ComponentType.NewSerializer::class)
public sealed class ComponentType(
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is ComponentType && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    /**
     * An unknown [ComponentType].
     *
     * This is used as a fallback for [ComponentType]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : ComponentType(value)

    /**
     * A container for other components.
     */
    public object ActionRow : ComponentType(1)

    /**
     * A button object.
     */
    public object Button : ComponentType(2)

    /**
     * A select menu for picking from choices.
     */
    public object SelectMenu : ComponentType(3)

    /**
     * A text input object.
     */
    public object TextInput : ComponentType(4)

    internal object NewSerializer : KSerializer<ComponentType> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ComponentType", PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: ComponentType) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> ActionRow
            2 -> Button
            3 -> SelectMenu
            4 -> TextInput
            else -> Unknown(value)
        }
    }

    @Deprecated(
        message = "Use 'ComponentType.serializer()' instead.",
        replaceWith = ReplaceWith(expression = "ComponentType.serializer()", imports =
                    arrayOf("dev.kord.common.entity.ComponentType")),
    )
    public object Serializer : KSerializer<ComponentType> by NewSerializer {
        @Deprecated(
            message = "Use 'ComponentType.serializer()' instead.",
            replaceWith = ReplaceWith(expression = "ComponentType.serializer()", imports =
                        arrayOf("dev.kord.common.entity.ComponentType")),
        )
        public fun serializer(): KSerializer<ComponentType> = this
    }

    public companion object {
        public val entries: List<ComponentType> by lazy(mode = PUBLICATION) {
            listOf(
                ActionRow,
                Button,
                SelectMenu,
                TextInput,
            )
        }


        @Suppress(names = arrayOf("DEPRECATION"))
        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Serializer: Serializer = Serializer
    }
}
