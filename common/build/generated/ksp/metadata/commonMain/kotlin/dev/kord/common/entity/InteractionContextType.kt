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
 * See [InteractionContextType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-context-types).
 */
@Serializable(with = InteractionContextType.Serializer::class)
public sealed class InteractionContextType(
    /**
     * The raw type used by Discord.
     */
    public val type: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is InteractionContextType && this.type == other.type)

    final override fun hashCode(): Int = type.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "InteractionContextType.Unknown(type=$type)"
            else "InteractionContextType.${this::class.simpleName}"

    /**
     * An unknown [InteractionContextType].
     *
     * This is used as a fallback for [InteractionContextType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        type: Int,
    ) : InteractionContextType(type)

    public object Guild : InteractionContextType(0)

    public object BotDm : InteractionContextType(1)

    public object PrivateChannel : InteractionContextType(2)

    internal object Serializer : KSerializer<InteractionContextType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.InteractionContextType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: InteractionContextType) {
            encoder.encodeInt(value.type)
        }

        override fun deserialize(decoder: Decoder): InteractionContextType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [InteractionContextType]s.
         */
        public val entries: List<InteractionContextType> by lazy(mode = PUBLICATION) {
            listOf(
                Guild,
                BotDm,
                PrivateChannel,
            )
        }

        /**
         * Returns an instance of [InteractionContextType] with [InteractionContextType.type] equal
         * to the specified [type].
         */
        public fun from(type: Int): InteractionContextType = when (type) {
            0 -> Guild
            1 -> BotDm
            2 -> PrivateChannel
            else -> Unknown(type)
        }
    }
}
