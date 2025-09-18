// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

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
 *
 *
 * See [PrimaryEntryPointCommandHandlerType]s in the [Discord Developer Documentation](https://discord.com/developers/docs/interactions/application-commands#application-command-object-entry-point-command-handler-types).
 */
@Serializable(with = PrimaryEntryPointCommandHandlerType.Serializer::class)
public sealed class PrimaryEntryPointCommandHandlerType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is PrimaryEntryPointCommandHandlerType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "PrimaryEntryPointCommandHandlerType.Unknown(value=$value)" else "PrimaryEntryPointCommandHandlerType.${this::class.simpleName}"

    /**
     * An unknown [PrimaryEntryPointCommandHandlerType].
     *
     * This is used as a fallback for [PrimaryEntryPointCommandHandlerType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : PrimaryEntryPointCommandHandlerType(value)

    /**
     * The app handles the interaction using an interaction token
     */
    public object AppHandler : PrimaryEntryPointCommandHandlerType(1)

    /**
     * Discord handles the interaction by launching an Activity and sending a follow-up message without coordinating with the app
     */
    public object DiscordLaunchActivity : PrimaryEntryPointCommandHandlerType(2)

    internal object Serializer : KSerializer<PrimaryEntryPointCommandHandlerType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.PrimaryEntryPointCommandHandlerType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: PrimaryEntryPointCommandHandlerType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): PrimaryEntryPointCommandHandlerType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [PrimaryEntryPointCommandHandlerType]s.
         */
        public val entries: List<PrimaryEntryPointCommandHandlerType> by lazy(mode = PUBLICATION) {
            listOf(
                AppHandler,
                DiscordLaunchActivity,
            )
        }

        /**
         * Returns an instance of [PrimaryEntryPointCommandHandlerType] with [PrimaryEntryPointCommandHandlerType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): PrimaryEntryPointCommandHandlerType = when (value) {
            1 -> AppHandler
            2 -> DiscordLaunchActivity
            else -> Unknown(value)
        }
    }
}
