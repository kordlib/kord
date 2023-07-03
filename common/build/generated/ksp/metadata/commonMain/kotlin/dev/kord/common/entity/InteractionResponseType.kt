// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection",
                "RedundantUnitReturnType"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordUnsafe
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
@OptIn(KordUnsafe::class)
public sealed class InteractionResponseType(
    /**
     * The raw type used by Discord.
     */
    public val type: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is InteractionResponseType && this.type == other.type)

    public final override fun hashCode(): Int = type.hashCode()

    public final override fun toString(): String =
            "InteractionResponseType.${this::class.simpleName}(type=$type)"

    /**
     * An unknown [InteractionResponseType].
     *
     * This is used as a fallback for [InteractionResponseType]s that haven't been added to Kord
     * yet.
     */
    public class Unknown @KordUnsafe constructor(
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

    internal object Serializer : KSerializer<InteractionResponseType> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.InteractionResponseType",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: InteractionResponseType) =
                encoder.encodeInt(value.type)

        public override fun deserialize(decoder: Decoder) = when (val type = decoder.decodeInt()) {
            1 -> Pong
            4 -> ChannelMessageWithSource
            5 -> DeferredChannelMessageWithSource
            6 -> DeferredUpdateMessage
            7 -> UpdateMessage
            8 -> ApplicationCommandAutoCompleteResult
            9 -> Modal
            else -> Unknown(type)
        }
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
            )
        }

    }
}
