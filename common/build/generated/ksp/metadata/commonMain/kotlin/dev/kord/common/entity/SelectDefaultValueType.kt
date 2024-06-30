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
 * See [SelectDefaultValueType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-default-value-structure).
 */
@Serializable(with = SelectDefaultValueType.Serializer::class)
public sealed class SelectDefaultValueType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SelectDefaultValueType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "SelectDefaultValueType.Unknown(value=$value)"
            else "SelectDefaultValueType.${this::class.simpleName}"

    /**
     * An unknown [SelectDefaultValueType].
     *
     * This is used as a fallback for [SelectDefaultValueType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : SelectDefaultValueType(value)

    public object User : SelectDefaultValueType("user")

    public object Role : SelectDefaultValueType("role")

    public object Channel : SelectDefaultValueType("channel")

    internal object Serializer : KSerializer<SelectDefaultValueType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.SelectDefaultValueType",
                PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: SelectDefaultValueType) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): SelectDefaultValueType =
                from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [SelectDefaultValueType]s.
         */
        public val entries: List<SelectDefaultValueType> by lazy(mode = PUBLICATION) {
            listOf(
                User,
                Role,
                Channel,
            )
        }

        /**
         * Returns an instance of [SelectDefaultValueType] with [SelectDefaultValueType.value] equal
         * to the specified [value].
         */
        public fun from(`value`: String): SelectDefaultValueType = when (value) {
            "user" -> User
            "role" -> Role
            "channel" -> Channel
            else -> Unknown(value)
        }
    }
}
