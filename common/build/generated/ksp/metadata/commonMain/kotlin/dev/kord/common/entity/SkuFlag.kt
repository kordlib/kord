// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * See [SkuFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/monetization/skus#sku-object-sku-flags).
 */
public sealed class SkuFlag(
    /**
     * The position of the bit that is set in this [SkuFlag]. This is always in 0..30.
     */
    public val shift: Int,
) {
    init {
        require(shift in 0..30) { """shift has to be in 0..30 but was $shift""" }
    }

    /**
     * The raw value used by Discord.
     */
    public val `value`: Int
        get() = 1 shl shift

    /**
     * Returns an instance of [SkuFlags] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: SkuFlag): SkuFlags = SkuFlags(this.value or flag.value)

    /**
     * Returns an instance of [SkuFlags] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: SkuFlags): SkuFlags = SkuFlags(this.value or flags.value)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SkuFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown) "SkuFlag.Unknown(shift=$shift)"
            else "SkuFlag.${this::class.simpleName}"

    /**
     * An unknown [SkuFlag].
     *
     * This is used as a fallback for [SkuFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : SkuFlag(shift)

    /**
     * SKU is available for purchase.
     */
    public object Available : SkuFlag(2)

    /**
     * Recurring SKU that can be purchased by a user and applied to a single server. Grants access
     * to every user in that server.
     */
    public object GuildSubscription : SkuFlag(7)

    /**
     * Recurring SKU purchased by a user for themselves. Grants access to the purchasing user in
     * every server.
     */
    public object UserSubscription : SkuFlag(8)

    public companion object {
        /**
         * A [List] of all known [SkuFlag]s.
         */
        public val entries: List<SkuFlag> by lazy(mode = PUBLICATION) {
            listOf(
                Available,
                GuildSubscription,
                UserSubscription,
            )
        }

        /**
         * Returns an instance of [SkuFlag] with [SkuFlag.shift] equal to the specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): SkuFlag = when (shift) {
            2 -> Available
            7 -> GuildSubscription
            8 -> UserSubscription
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [SkuFlag]s.
 *
 * ## Creating an instance of [SkuFlags]
 *
 * You can create an instance of [SkuFlags] using the following methods:
 * ```kotlin
 * // from individual SkuFlags
 * val skuFlags1 = SkuFlags(SkuFlag.Available, SkuFlag.GuildSubscription)
 *
 * // from an Iterable
 * val iterable: Iterable<SkuFlag> = TODO()
 * val skuFlags2 = SkuFlags(iterable)
 *
 * // using a builder
 * val skuFlags3 = SkuFlags {
 *     +skuFlags2
 *     +SkuFlag.Available
 *     -SkuFlag.GuildSubscription
 * }
 * ```
 *
 * ## Modifying an existing instance of [SkuFlags]
 *
 * You can create a modified copy of an existing instance of [SkuFlags] using the [copy] method:
 * ```kotlin
 * skuFlags.copy {
 *     +SkuFlag.Available
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [SkuFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val skuFlags1 = skuFlags + SkuFlag.Available
 * val skuFlags2 = skuFlags - SkuFlag.GuildSubscription
 * val skuFlags3 = skuFlags1 + skuFlags2
 * ```
 *
 * ## Checking for [SkuFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [SkuFlags] contains specific
 * [SkuFlag]s:
 * ```kotlin
 * val hasSkuFlag = SkuFlag.Available in skuFlags
 * val hasSkuFlags = SkuFlags(SkuFlag.Available, SkuFlag.GuildSubscription) in skuFlags
 * ```
 *
 * ## Unknown [SkuFlag]s
 *
 * Whenever [SkuFlag]s haven't been added to Kord yet, they will be deserialized as instances of
 * [SkuFlag.Unknown].
 *
 * You can also use [SkuFlag.fromShift] to check for [unknown][SkuFlag.Unknown] [SkuFlag]s.
 * ```kotlin
 * val hasUnknownSkuFlag = SkuFlag.fromShift(23) in skuFlags
 * ```
 *
 * @see SkuFlag
 * @see SkuFlags.Builder
 */
@Serializable(with = SkuFlags.Serializer::class)
public class SkuFlags internal constructor(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    /**
     * A [Set] of all [SkuFlag]s contained in this instance of [SkuFlags].
     */
    public val values: Set<SkuFlag>
        get() = buildSet {
            var remaining = value
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(SkuFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [SkuFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: SkuFlag): Boolean = this.value and flag.value == flag.value

    /**
     * Checks if this instance of [SkuFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: SkuFlags): Boolean =
            this.value and flags.value == flags.value

    /**
     * Returns an instance of [SkuFlags] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: SkuFlag): SkuFlags = SkuFlags(this.value or flag.value)

    /**
     * Returns an instance of [SkuFlags] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: SkuFlags): SkuFlags = SkuFlags(this.value or flags.value)

    /**
     * Returns an instance of [SkuFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flag].
     */
    public operator fun minus(flag: SkuFlag): SkuFlags = SkuFlags(this.value and flag.value.inv())

    /**
     * Returns an instance of [SkuFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flags].
     */
    public operator fun minus(flags: SkuFlags): SkuFlags =
            SkuFlags(this.value and flags.value.inv())

    /**
     * Returns a copy of this instance of [SkuFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): SkuFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(value).apply(builder).build()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is SkuFlags && this.value == other.value)

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "SkuFlags(values=$values)"

    public class Builder(
        private var `value`: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [SkuFlag].
         */
        public operator fun SkuFlag.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        /**
         * Sets all bits in the [Builder] that are set in this [SkuFlags].
         */
        public operator fun SkuFlags.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SkuFlag].
         */
        public operator fun SkuFlag.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SkuFlags].
         */
        public operator fun SkuFlags.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        /**
         * Returns an instance of [SkuFlags] that has all bits set that are currently set in this
         * [Builder].
         */
        public fun build(): SkuFlags = SkuFlags(value)
    }

    internal object Serializer : KSerializer<SkuFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.SkuFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: SkuFlags) {
            encoder.encodeSerializableValue(delegate, value.value)
        }

        override fun deserialize(decoder: Decoder): SkuFlags =
                SkuFlags(decoder.decodeSerializableValue(delegate))
    }
}

/**
 * Returns an instance of [SkuFlags] built with [SkuFlags.Builder].
 */
public inline fun SkuFlags(builder: SkuFlags.Builder.() -> Unit = {}): SkuFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return SkuFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [SkuFlags] that has all bits set that are set in any element of [flags].
 */
public fun SkuFlags(vararg flags: SkuFlag): SkuFlags = SkuFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SkuFlags] that has all bits set that are set in any element of [flags].
 */
public fun SkuFlags(vararg flags: SkuFlags): SkuFlags = SkuFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SkuFlags] that has all bits set that are set in any element of [flags].
 */
public fun SkuFlags(flags: Iterable<SkuFlag>): SkuFlags = SkuFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SkuFlags] that has all bits set that are set in any element of [flags].
 */
@JvmName("SkuFlags0")
public fun SkuFlags(flags: Iterable<SkuFlags>): SkuFlags = SkuFlags {
    flags.forEach { +it }
}
