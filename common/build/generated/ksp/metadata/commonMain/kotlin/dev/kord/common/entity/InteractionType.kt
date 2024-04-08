// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

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
 * See [InteractionType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-type).
 */
@Serializable(with = InteractionType.Serializer::class)
public sealed class InteractionType(
    /**
     * The raw type used by Discord.
     */
    public val type: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is InteractionType && this.type == other.type)

    final override fun hashCode(): Int = type.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "InteractionType.Unknown(type=$type)"
            else "InteractionType.${this::class.simpleName}"

    /**
     * An unknown [InteractionType].
     *
     * This is used as a fallback for [InteractionType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        type: Int,
    ) : InteractionType(type)

    public object Ping : InteractionType(1)

    public object ApplicationCommand : InteractionType(2)

    public object Component : InteractionType(3)

    public object AutoComplete : InteractionType(4)

    public object ModalSubmit : InteractionType(5)

    internal object Serializer : KSerializer<InteractionType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.InteractionType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: InteractionType) {
            encoder.encodeInt(value.type)
        }

        override fun deserialize(decoder: Decoder): InteractionType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [InteractionType]s.
         */
        public val entries: List<InteractionType> by lazy(mode = PUBLICATION) {
            listOf(
                Ping,
                ApplicationCommand,
                Component,
                AutoComplete,
                ModalSubmit,
            )
        }


        /**
         * Returns an instance of [InteractionType] with [InteractionType.type] equal to the
         * specified [type].
         */
        public fun from(type: Int): InteractionType = when (type) {
            1 -> Ping
            2 -> ApplicationCommand
            3 -> Component
            4 -> AutoComplete
            5 -> ModalSubmit
            else -> Unknown(type)
        }
    }
}
