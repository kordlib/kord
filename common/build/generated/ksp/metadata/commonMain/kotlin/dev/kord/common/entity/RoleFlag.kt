// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlinx.serialization.Serializable

/**
 * See [RoleFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/permissions#role-object-role-flags).
 */
public sealed class RoleFlag(
    /**
     * The position of the bit that is set in this [RoleFlag]. This is always in 0..30.
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
     * Returns an instance of [RoleFlags] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: RoleFlag): RoleFlags = RoleFlags(this.value or flag.value)

    /**
     * Returns an instance of [RoleFlags] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: RoleFlags): RoleFlags = RoleFlags(this.value or flags.value)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is RoleFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown) "RoleFlag.Unknown(shift=$shift)"
            else "RoleFlag.${this::class.simpleName}"

    /**
     * An unknown [RoleFlag].
     *
     * This is used as a fallback for [RoleFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : RoleFlag(shift)

    /**
     * Role can be selected by members in an onboarding prompt.
     */
    public object InPrompt : RoleFlag(0)

    public companion object {
        /**
         * A [List] of all known [RoleFlag]s.
         */
        public val entries: List<RoleFlag> by lazy(mode = PUBLICATION) {
            listOf(
                InPrompt,
            )
        }


        /**
         * Returns an instance of [RoleFlag] with [RoleFlag.shift] equal to the specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): RoleFlag = when (shift) {
            0 -> InPrompt
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [RoleFlag]s.
 *
 * ## Creating an instance of [RoleFlags]
 *
 * You can create an instance of [RoleFlags] using the following methods:
 * ```kotlin
 * // from individual RoleFlags
 * val roleFlags1 = RoleFlags(RoleFlag.InPrompt, RoleFlag.fromShift(22))
 *
 * // from an Iterable
 * val iterable: Iterable<RoleFlag> = TODO()
 * val roleFlags2 = RoleFlags(iterable)
 *
 * // using a builder
 * val roleFlags3 = RoleFlags {
 *     +roleFlags2
 *     +RoleFlag.InPrompt
 *     -RoleFlag.fromShift(22)
 * }
 * ```
 *
 * ## Modifying an existing instance of [RoleFlags]
 *
 * You can create a modified copy of an existing instance of [RoleFlags] using the [copy] method:
 * ```kotlin
 * roleFlags.copy {
 *     +RoleFlag.InPrompt
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [RoleFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val roleFlags1 = roleFlags + RoleFlag.InPrompt
 * val roleFlags2 = roleFlags - RoleFlag.fromShift(22)
 * val roleFlags3 = roleFlags1 + roleFlags2
 * ```
 *
 * ## Checking for [RoleFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [RoleFlags] contains specific
 * [RoleFlag]s:
 * ```kotlin
 * val hasRoleFlag = RoleFlag.InPrompt in roleFlags
 * val hasRoleFlags = RoleFlags(RoleFlag.InPrompt, RoleFlag.fromShift(22)) in roleFlags
 * ```
 *
 * ## Unknown [RoleFlag]s
 *
 * Whenever [RoleFlag]s haven't been added to Kord yet, they will be deserialized as instances of
 * [RoleFlag.Unknown].
 *
 * You can also use [RoleFlag.fromShift] to check for [unknown][RoleFlag.Unknown] [RoleFlag]s.
 * ```kotlin
 * val hasUnknownRoleFlag = RoleFlag.fromShift(23) in roleFlags
 * ```
 *
 * @see RoleFlag
 * @see RoleFlags.Builder
 */
@JvmInline
@Serializable
public value class RoleFlags internal constructor(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    /**
     * A [Set] of all [RoleFlag]s contained in this instance of [RoleFlags].
     */
    public val values: Set<RoleFlag>
        get() = buildSet {
            var remaining = value
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(RoleFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [RoleFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: RoleFlag): Boolean = this.value and flag.value == flag.value

    /**
     * Checks if this instance of [RoleFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: RoleFlags): Boolean =
            this.value and flags.value == flags.value

    /**
     * Returns an instance of [RoleFlags] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: RoleFlag): RoleFlags = RoleFlags(this.value or flag.value)

    /**
     * Returns an instance of [RoleFlags] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: RoleFlags): RoleFlags = RoleFlags(this.value or flags.value)

    /**
     * Returns an instance of [RoleFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flag].
     */
    public operator fun minus(flag: RoleFlag): RoleFlags =
            RoleFlags(this.value and flag.value.inv())

    /**
     * Returns an instance of [RoleFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flags].
     */
    public operator fun minus(flags: RoleFlags): RoleFlags =
            RoleFlags(this.value and flags.value.inv())

    /**
     * Returns a copy of this instance of [RoleFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): RoleFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(value).apply(builder).build()
    }

    override fun toString(): String = "RoleFlags(values=$values)"

    public class Builder(
        private var `value`: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [RoleFlag].
         */
        public operator fun RoleFlag.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        /**
         * Sets all bits in the [Builder] that are set in this [RoleFlags].
         */
        public operator fun RoleFlags.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [RoleFlag].
         */
        public operator fun RoleFlag.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [RoleFlags].
         */
        public operator fun RoleFlags.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        /**
         * Returns an instance of [RoleFlags] that has all bits set that are currently set in this
         * [Builder].
         */
        public fun build(): RoleFlags = RoleFlags(value)
    }
}

/**
 * Returns an instance of [RoleFlags] built with [RoleFlags.Builder].
 */
public inline fun RoleFlags(builder: RoleFlags.Builder.() -> Unit = {}): RoleFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return RoleFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [RoleFlags] that has all bits set that are set in any element of [flags].
 */
public fun RoleFlags(vararg flags: RoleFlag): RoleFlags = RoleFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [RoleFlags] that has all bits set that are set in any element of [flags].
 */
public fun RoleFlags(flags: Iterable<RoleFlag>): RoleFlags = RoleFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [RoleFlags] that has all bits set that are set in any element of [flags].
 */
@JvmName("RoleFlags0")
public fun RoleFlags(flags: Iterable<RoleFlags>): RoleFlags = RoleFlags {
    flags.forEach { +it }
}
