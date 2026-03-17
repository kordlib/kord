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
 * See [BaseClientThemeType]s in the [Discord Developer Documentation](https://docs.discord.com/developers/resources/message#shared-client-theme-object).
 */
@Serializable(with = BaseClientThemeType.Serializer::class)
public sealed class BaseClientThemeType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is BaseClientThemeType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "BaseClientThemeType.Unknown(value=$value)" else "BaseClientThemeType.${this::class.simpleName}"

    /**
     * An unknown [BaseClientThemeType].
     *
     * This is used as a fallback for [BaseClientThemeType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : BaseClientThemeType(value)

    public object Dark : BaseClientThemeType(1)

    public object Light : BaseClientThemeType(2)

    public object Darker : BaseClientThemeType(3)

    public object Midnight : BaseClientThemeType(4)

    internal object Serializer : KSerializer<BaseClientThemeType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.BaseClientThemeType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: BaseClientThemeType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): BaseClientThemeType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [BaseClientThemeType]s.
         */
        public val entries: List<BaseClientThemeType> by lazy(mode = PUBLICATION) {
            listOf(
                Dark,
                Light,
                Darker,
                Midnight,
            )
        }

        /**
         * Returns an instance of [BaseClientThemeType] with [BaseClientThemeType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): BaseClientThemeType = when (value) {
            1 -> Dark
            2 -> Light
            3 -> Darker
            4 -> Midnight
            else -> Unknown(value)
        }
    }
}
