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
 * See [WebhookType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/webhook#webhook-object-webhook-types).
 */
@Serializable(with = WebhookType.Serializer::class)
public sealed class WebhookType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is WebhookType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = "WebhookType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [WebhookType].
     *
     * This is used as a fallback for [WebhookType]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : WebhookType(value)

    /**
     * Incoming Webhooks can post messages to channels with a generated token.
     */
    public object Incoming : WebhookType(1)

    /**
     * Channel Follower Webhooks are internal webhooks used with Channel Following to post new
     * messages into channels.
     */
    public object ChannelFollower : WebhookType(2)

    /**
     * Application webhooks are webhooks used with Interactions.
     */
    public object Application : WebhookType(3)

    internal object Serializer : KSerializer<WebhookType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.WebhookType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: WebhookType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): WebhookType =
                when (val value = decoder.decodeInt()) {
            1 -> Incoming
            2 -> ChannelFollower
            3 -> Application
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [WebhookType]s.
         */
        public val entries: List<WebhookType> by lazy(mode = PUBLICATION) {
            listOf(
                Incoming,
                ChannelFollower,
                Application,
            )
        }

    }
}
