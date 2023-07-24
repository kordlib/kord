// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * See [ComponentType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/message-components#component-object-component-types).
 */
@Serializable(with = ComponentType.Serializer::class)
public sealed class ComponentType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ComponentType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = "ComponentType.${this::class.simpleName}(value=$value)"

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
     * A select menu for picking from defined text options.
     */
    public object StringSelect : ComponentType(3)

    /**
     * A text input object.
     */
    public object TextInput : ComponentType(4)

    /**
     * Select menu for users.
     */
    public object UserSelect : ComponentType(5)

    /**
     * Select menu for roles.
     */
    public object RoleSelect : ComponentType(6)

    /**
     * Select menu for mentionables (users and roles).
     */
    public object MentionableSelect : ComponentType(7)

    /**
     * Select menu for channels.
     */
    public object ChannelSelect : ComponentType(8)

    internal object Serializer : KSerializer<ComponentType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ComponentType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ComponentType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ComponentType =
                when (val value = decoder.decodeInt()) {
            1 -> ActionRow
            2 -> Button
            3 -> StringSelect
            4 -> TextInput
            5 -> UserSelect
            6 -> RoleSelect
            7 -> MentionableSelect
            8 -> ChannelSelect
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [ComponentType]s.
         */
        public val entries: List<ComponentType> by lazy(mode = PUBLICATION) {
            listOf(
                ActionRow,
                Button,
                StringSelect,
                TextInput,
                UserSelect,
                RoleSelect,
                MentionableSelect,
                ChannelSelect,
            )
        }

    }
}
