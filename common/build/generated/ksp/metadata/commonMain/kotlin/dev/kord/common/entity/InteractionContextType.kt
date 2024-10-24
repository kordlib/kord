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
 * Context in Discord where an interaction can be used, or where it was triggered from.
 *
 * See [InteractionContextType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-context-types).
 */
@Serializable(with = InteractionContextType.Serializer::class)
public sealed class InteractionContextType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is InteractionContextType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "InteractionContextType.Unknown(value=$value)"
            else "InteractionContextType.${this::class.simpleName}"

    /**
     * An unknown [InteractionContextType].
     *
     * This is used as a fallback for [InteractionContextType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : InteractionContextType(value)

    /**
     * Interaction can be used within servers
     */
    public object Guild : InteractionContextType(0)

    /**
     * Interaction can be used within DMs with the app's bot user
     */
    public object BotDM : InteractionContextType(1)

    /**
     * Interaction can be used within Group DMs and DMs other than the app's bot user
     */
    public object PrivateChannel : InteractionContextType(2)

    internal object Serializer : KSerializer<InteractionContextType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.InteractionContextType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: InteractionContextType) {
            encoder.encodeInt(value.value)
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
                BotDM,
                PrivateChannel,
            )
        }

        /**
         * Returns an instance of [InteractionContextType] with [InteractionContextType.value] equal
         * to the specified [value].
         */
        public fun from(`value`: Int): InteractionContextType = when (value) {
            0 -> Guild
            1 -> BotDM
            2 -> PrivateChannel
            else -> Unknown(value)
        }
    }
}
