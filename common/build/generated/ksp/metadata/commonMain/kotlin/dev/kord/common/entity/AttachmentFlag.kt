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
 * See [AttachmentFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#attachment-object-attachment-flags).
 */
public sealed class AttachmentFlag(
    /**
     * The position of the bit that is set in this [AttachmentFlag]. This is always in 0..30.
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
     * Returns an instance of [AttachmentFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: AttachmentFlag): AttachmentFlags =
            AttachmentFlags(this.value or flag.value)

    /**
     * Returns an instance of [AttachmentFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: AttachmentFlags): AttachmentFlags =
            AttachmentFlags(this.value or flags.value)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is AttachmentFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "AttachmentFlag.Unknown(shift=$shift)"
            else "AttachmentFlag.${this::class.simpleName}"

    /**
     * An unknown [AttachmentFlag].
     *
     * This is used as a fallback for [AttachmentFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : AttachmentFlag(shift)

    /**
     * This attachment has been edited using the remix feature on mobile.
     */
    public object IsRemix : AttachmentFlag(2)

    public companion object {
        /**
         * A [List] of all known [AttachmentFlag]s.
         */
        public val entries: List<AttachmentFlag> by lazy(mode = PUBLICATION) {
            listOf(
                IsRemix,
            )
        }


        /**
         * Returns an instance of [AttachmentFlag] with [AttachmentFlag.shift] equal to the
         * specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): AttachmentFlag = when (shift) {
            2 -> IsRemix
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [AttachmentFlag]s.
 *
 * ## Creating an instance of [AttachmentFlags]
 *
 * You can create an instance of [AttachmentFlags] using the following methods:
 * ```kotlin
 * // from individual AttachmentFlags
 * val attachmentFlags1 = AttachmentFlags(AttachmentFlag.IsRemix, AttachmentFlag.fromShift(22))
 *
 * // from an Iterable
 * val iterable: Iterable<AttachmentFlag> = TODO()
 * val attachmentFlags2 = AttachmentFlags(iterable)
 *
 * // using a builder
 * val attachmentFlags3 = AttachmentFlags {
 *     +attachmentFlags2
 *     +AttachmentFlag.IsRemix
 *     -AttachmentFlag.fromShift(22)
 * }
 * ```
 *
 * ## Modifying an existing instance of [AttachmentFlags]
 *
 * You can create a modified copy of an existing instance of [AttachmentFlags] using the [copy]
 * method:
 * ```kotlin
 * attachmentFlags.copy {
 *     +AttachmentFlag.IsRemix
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [AttachmentFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val attachmentFlags1 = attachmentFlags + AttachmentFlag.IsRemix
 * val attachmentFlags2 = attachmentFlags - AttachmentFlag.fromShift(22)
 * val attachmentFlags3 = attachmentFlags1 + attachmentFlags2
 * ```
 *
 * ## Checking for [AttachmentFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [AttachmentFlags] contains
 * specific [AttachmentFlag]s:
 * ```kotlin
 * val hasAttachmentFlag = AttachmentFlag.IsRemix in attachmentFlags
 * val hasAttachmentFlags = AttachmentFlags(AttachmentFlag.IsRemix, AttachmentFlag.fromShift(22)) in attachmentFlags
 * ```
 *
 * ## Unknown [AttachmentFlag]s
 *
 * Whenever [AttachmentFlag]s haven't been added to Kord yet, they will be deserialized as instances
 * of [AttachmentFlag.Unknown].
 *
 * You can also use [AttachmentFlag.fromShift] to check for [unknown][AttachmentFlag.Unknown]
 * [AttachmentFlag]s.
 * ```kotlin
 * val hasUnknownAttachmentFlag = AttachmentFlag.fromShift(23) in attachmentFlags
 * ```
 *
 * @see AttachmentFlag
 * @see AttachmentFlags.Builder
 */
@JvmInline
@Serializable
public value class AttachmentFlags internal constructor(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    /**
     * A [Set] of all [AttachmentFlag]s contained in this instance of [AttachmentFlags].
     */
    public val values: Set<AttachmentFlag>
        get() = buildSet {
            var remaining = value
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(AttachmentFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [AttachmentFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: AttachmentFlag): Boolean =
            this.value and flag.value == flag.value

    /**
     * Checks if this instance of [AttachmentFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: AttachmentFlags): Boolean =
            this.value and flags.value == flags.value

    /**
     * Returns an instance of [AttachmentFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: AttachmentFlag): AttachmentFlags =
            AttachmentFlags(this.value or flag.value)

    /**
     * Returns an instance of [AttachmentFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: AttachmentFlags): AttachmentFlags =
            AttachmentFlags(this.value or flags.value)

    /**
     * Returns an instance of [AttachmentFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flag].
     */
    public operator fun minus(flag: AttachmentFlag): AttachmentFlags =
            AttachmentFlags(this.value and flag.value.inv())

    /**
     * Returns an instance of [AttachmentFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flags].
     */
    public operator fun minus(flags: AttachmentFlags): AttachmentFlags =
            AttachmentFlags(this.value and flags.value.inv())

    /**
     * Returns a copy of this instance of [AttachmentFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): AttachmentFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(value).apply(builder).build()
    }

    override fun toString(): String = "AttachmentFlags(values=$values)"

    public class Builder(
        private var `value`: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [AttachmentFlag].
         */
        public operator fun AttachmentFlag.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        /**
         * Sets all bits in the [Builder] that are set in this [AttachmentFlags].
         */
        public operator fun AttachmentFlags.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [AttachmentFlag].
         */
        public operator fun AttachmentFlag.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [AttachmentFlags].
         */
        public operator fun AttachmentFlags.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        /**
         * Returns an instance of [AttachmentFlags] that has all bits set that are currently set in
         * this [Builder].
         */
        public fun build(): AttachmentFlags = AttachmentFlags(value)
    }
}

/**
 * Returns an instance of [AttachmentFlags] built with [AttachmentFlags.Builder].
 */
public inline fun AttachmentFlags(builder: AttachmentFlags.Builder.() -> Unit = {}):
        AttachmentFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return AttachmentFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [AttachmentFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun AttachmentFlags(vararg flags: AttachmentFlag): AttachmentFlags = AttachmentFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [AttachmentFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun AttachmentFlags(flags: Iterable<AttachmentFlag>): AttachmentFlags = AttachmentFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [AttachmentFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("AttachmentFlags0")
public fun AttachmentFlags(flags: Iterable<AttachmentFlags>): AttachmentFlags = AttachmentFlags {
    flags.forEach { +it }
}
