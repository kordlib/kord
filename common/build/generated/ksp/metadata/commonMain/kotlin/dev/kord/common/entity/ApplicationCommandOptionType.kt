// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection", "MemberVisibilityCanBePrivate"))

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
 * See [ApplicationCommandOptionType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-type).
 */
@Serializable(with = ApplicationCommandOptionType.Serializer::class)
public sealed class ApplicationCommandOptionType(
    /**
     * The raw type used by Discord.
     */
    public val type: Int,
) {
    final override fun equals(other: Any?): kotlin.Boolean = this === other ||
            (other is ApplicationCommandOptionType && this.type == other.type)

    final override fun hashCode(): Int = type.hashCode()

    final override fun toString(): kotlin.String =
            if (this is Unknown) "ApplicationCommandOptionType.Unknown(type=$type)"
            else "ApplicationCommandOptionType.${this::class.simpleName}"

    /**
     * An unknown [ApplicationCommandOptionType].
     *
     * This is used as a fallback for [ApplicationCommandOptionType]s that haven't been added to
     * Kord yet.
     */
    public class Unknown internal constructor(
        type: Int,
    ) : ApplicationCommandOptionType(type)

    public object SubCommand : ApplicationCommandOptionType(1)

    public object SubCommandGroup : ApplicationCommandOptionType(2)

    public object String : ApplicationCommandOptionType(3)

    /**
     * Any integer between `-2^53` and `2^53`.
     */
    public object Integer : ApplicationCommandOptionType(4)

    public object Boolean : ApplicationCommandOptionType(5)

    public object User : ApplicationCommandOptionType(6)

    /**
     * Includes all channel types + categories.
     */
    public object Channel : ApplicationCommandOptionType(7)

    public object Role : ApplicationCommandOptionType(8)

    /**
     * Includes users and roles.
     */
    public object Mentionable : ApplicationCommandOptionType(9)

    /**
     * Any double between `-2^53` and `2^53`.
     */
    public object Number : ApplicationCommandOptionType(10)

    public object Attachment : ApplicationCommandOptionType(11)

    internal object Serializer : KSerializer<ApplicationCommandOptionType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ApplicationCommandOptionType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ApplicationCommandOptionType) {
            encoder.encodeInt(value.type)
        }

        override fun deserialize(decoder: Decoder): ApplicationCommandOptionType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [ApplicationCommandOptionType]s.
         */
        public val entries: List<ApplicationCommandOptionType> by lazy(mode = PUBLICATION) {
            listOf(
                SubCommand,
                SubCommandGroup,
                String,
                Integer,
                Boolean,
                User,
                Channel,
                Role,
                Mentionable,
                Number,
                Attachment,
            )
        }


        /**
         * Returns an instance of [ApplicationCommandOptionType] with
         * [ApplicationCommandOptionType.type] equal to the specified [type].
         */
        public fun from(type: Int): ApplicationCommandOptionType = when (type) {
            1 -> SubCommand
            2 -> SubCommandGroup
            3 -> String
            4 -> Integer
            5 -> Boolean
            6 -> User
            7 -> Channel
            8 -> Role
            9 -> Mentionable
            10 -> Number
            11 -> Attachment
            else -> Unknown(type)
        }
    }
}
