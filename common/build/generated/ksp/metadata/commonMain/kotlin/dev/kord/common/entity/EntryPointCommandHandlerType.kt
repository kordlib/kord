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
 * See [EntryPointCommandHandlerType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/application-commands#application-command-object-entry-point-command-handler-types).
 */
@Serializable(with = EntryPointCommandHandlerType.Serializer::class)
public sealed class EntryPointCommandHandlerType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is EntryPointCommandHandlerType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "EntryPointCommandHandlerType.Unknown(value=$value)"
            else "EntryPointCommandHandlerType.${this::class.simpleName}"

    /**
     * An unknown [EntryPointCommandHandlerType].
     *
     * This is used as a fallback for [EntryPointCommandHandlerType]s that haven't been added to
     * Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : EntryPointCommandHandlerType(value)

    /**
     * The app handles the interaction using an interaction token
     */
    public object AppHandler : EntryPointCommandHandlerType(1)

    /**
     * Discord handles the interaction by launching an Activity and sending a follow-up message
     * without coordinating with the app
     */
    public object DiscordLaunchActivity : EntryPointCommandHandlerType(2)

    internal object Serializer : KSerializer<EntryPointCommandHandlerType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.EntryPointCommandHandlerType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: EntryPointCommandHandlerType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): EntryPointCommandHandlerType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [EntryPointCommandHandlerType]s.
         */
        public val entries: List<EntryPointCommandHandlerType> by lazy(mode = PUBLICATION) {
            listOf(
                AppHandler,
                DiscordLaunchActivity,
            )
        }

        /**
         * Returns an instance of [EntryPointCommandHandlerType] with
         * [EntryPointCommandHandlerType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): EntryPointCommandHandlerType = when (value) {
            1 -> AppHandler
            2 -> DiscordLaunchActivity
            else -> Unknown(value)
        }
    }
}
