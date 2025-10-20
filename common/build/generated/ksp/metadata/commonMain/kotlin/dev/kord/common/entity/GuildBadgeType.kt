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
 * See [GuildBadgeType]s in the [Discord Developer Documentation](https://docs.discord.food/resources/discovery#guild-badge-type).
 */
@Serializable(with = GuildBadgeType.Serializer::class)
public sealed class GuildBadgeType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is GuildBadgeType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "GuildBadgeType.Unknown(value=$value)" else "GuildBadgeType.${this::class.simpleName}"

    /**
     * An unknown [GuildBadgeType].
     *
     * This is used as a fallback for [GuildBadgeType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : GuildBadgeType(value)

    public object Sword : GuildBadgeType(0)

    public object WaterDrop : GuildBadgeType(1)

    public object Skull : GuildBadgeType(2)

    public object Toadstool : GuildBadgeType(3)

    public object Moon : GuildBadgeType(4)

    public object Lightning : GuildBadgeType(5)

    public object Leaf : GuildBadgeType(6)

    public object Heart : GuildBadgeType(7)

    public object Fire : GuildBadgeType(8)

    public object Compass : GuildBadgeType(9)

    public object Crosshairs : GuildBadgeType(10)

    public object Flower : GuildBadgeType(11)

    public object Force : GuildBadgeType(12)

    public object Gem : GuildBadgeType(13)

    public object Lava : GuildBadgeType(14)

    public object Psychic : GuildBadgeType(15)

    public object Smoke : GuildBadgeType(16)

    public object Snow : GuildBadgeType(17)

    public object Sound : GuildBadgeType(17)

    public object Sun : GuildBadgeType(19)

    public object Wind : GuildBadgeType(20)

    public object Bunny : GuildBadgeType(21)

    public object Dog : GuildBadgeType(22)

    public object Frog : GuildBadgeType(23)

    public object Goat : GuildBadgeType(24)

    public object Cat : GuildBadgeType(25)

    public object Diamond : GuildBadgeType(26)

    public object Crown : GuildBadgeType(27)

    public object Trophy : GuildBadgeType(28)

    public object MoneyBag : GuildBadgeType(29)

    public object DollarSign : GuildBadgeType(30)

    internal object Serializer : KSerializer<GuildBadgeType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildBadgeType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: GuildBadgeType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): GuildBadgeType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [GuildBadgeType]s.
         */
        public val entries: List<GuildBadgeType> by lazy(mode = PUBLICATION) {
            listOf(
                Sword,
                WaterDrop,
                Skull,
                Toadstool,
                Moon,
                Lightning,
                Leaf,
                Heart,
                Fire,
                Compass,
                Crosshairs,
                Flower,
                Force,
                Gem,
                Lava,
                Psychic,
                Smoke,
                Snow,
                Sun,
                Wind,
                Bunny,
                Dog,
                Frog,
                Goat,
                Cat,
                Diamond,
                Crown,
                Trophy,
                MoneyBag,
                DollarSign,
            )
        }

        /**
         * Returns an instance of [GuildBadgeType] with [GuildBadgeType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): GuildBadgeType = when (value) {
            0 -> Sword
            1 -> WaterDrop
            2 -> Skull
            3 -> Toadstool
            4 -> Moon
            5 -> Lightning
            6 -> Leaf
            7 -> Heart
            8 -> Fire
            9 -> Compass
            10 -> Crosshairs
            11 -> Flower
            12 -> Force
            13 -> Gem
            14 -> Lava
            15 -> Psychic
            16 -> Smoke
            17 -> Snow
            19 -> Sun
            20 -> Wind
            21 -> Bunny
            22 -> Dog
            23 -> Frog
            24 -> Goat
            25 -> Cat
            26 -> Diamond
            27 -> Crown
            28 -> Trophy
            29 -> MoneyBag
            30 -> DollarSign
            else -> Unknown(value)
        }
    }
}
