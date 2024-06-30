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
 * See [InteractionResponseType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-interaction-callback-type).
 */
@Serializable(with = InteractionResponseType.Serializer::class)
public sealed class InteractionResponseType(
    /**
     * The raw type used by Discord.
     */
    public val type: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is InteractionResponseType && this.type == other.type)

    final override fun hashCode(): Int = type.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "InteractionResponseType.Unknown(type=$type)"
            else "InteractionResponseType.${this::class.simpleName}"

    /**
     * An unknown [InteractionResponseType].
     *
     * This is used as a fallback for [InteractionResponseType]s that haven't been added to Kord
     * yet.
     */
    public class Unknown internal constructor(
        type: Int,
    ) : InteractionResponseType(type)

    /**
     * ACK a [Ping][dev.kord.common.entity.InteractionType.Ping].
     */
    public object Pong : InteractionResponseType(1)

    /**
     * Respond to an interaction with a message.
     */
    public object ChannelMessageWithSource : InteractionResponseType(4)

    /**
     * ACK an interaction and edit a response later, the user sees a loading state.
     */
    public object DeferredChannelMessageWithSource : InteractionResponseType(5)

    /**
     * For components, ACK an interaction and edit the original message later; the user does not see
     * a loading state.
     */
    public object DeferredUpdateMessage : InteractionResponseType(6)

    /**
     * For components, edit the message the component was attached to.
     */
    public object UpdateMessage : InteractionResponseType(7)

    /**
     * Respond to an autocomplete interaction with suggested choices.
     */
    public object ApplicationCommandAutoCompleteResult : InteractionResponseType(8)

    /**
     * Respond to an interaction with a popup modal.
     */
    public object Modal : InteractionResponseType(9)

    /**
     * Respond to an interaction with an upgrade button, only available for apps with monetization
     * enabled.
     */
    public object PremiumRequired : InteractionResponseType(10)

    internal object Serializer : KSerializer<InteractionResponseType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.InteractionResponseType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: InteractionResponseType) {
            encoder.encodeInt(value.type)
        }

        override fun deserialize(decoder: Decoder): InteractionResponseType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [InteractionResponseType]s.
         */
        public val entries: List<InteractionResponseType> by lazy(mode = PUBLICATION) {
            listOf(
                Pong,
                ChannelMessageWithSource,
                DeferredChannelMessageWithSource,
                DeferredUpdateMessage,
                UpdateMessage,
                ApplicationCommandAutoCompleteResult,
                Modal,
                PremiumRequired,
            )
        }

        /**
         * Returns an instance of [InteractionResponseType] with [InteractionResponseType.type]
         * equal to the specified [type].
         */
        public fun from(type: Int): InteractionResponseType = when (type) {
            1 -> Pong
            4 -> ChannelMessageWithSource
            5 -> DeferredChannelMessageWithSource
            6 -> DeferredUpdateMessage
            7 -> UpdateMessage
            8 -> ApplicationCommandAutoCompleteResult
            9 -> Modal
            10 -> PremiumRequired
            else -> Unknown(type)
        }
    }
}
