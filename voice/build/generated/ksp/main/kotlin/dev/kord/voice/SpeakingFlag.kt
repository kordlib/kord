// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.voice

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlinx.serialization.Serializable

/**
 * See [SpeakingFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/voice-connections#speaking).
 */
public sealed class SpeakingFlag(
    /**
     * The position of the bit that is set in this [SpeakingFlag]. This is always in 0..30.
     */
    public val shift: Int,
) {
    init {
        require(shift in 0..30) { """shift has to be in 0..30 but was $shift""" }
    }

    /**
     * The raw code used by Discord.
     */
    public val code: Int
        get() = 1 shl shift

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: SpeakingFlag): SpeakingFlags =
            SpeakingFlags(this.code or flag.code)

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: SpeakingFlags): SpeakingFlags =
            SpeakingFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SpeakingFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "SpeakingFlag.Unknown(shift=$shift)"
            else "SpeakingFlag.${this::class.simpleName}"

    /**
     * An unknown [SpeakingFlag].
     *
     * This is used as a fallback for [SpeakingFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : SpeakingFlag(shift)

    /**
     * Normal transmission of voice audio.
     */
    public object Microphone : SpeakingFlag(0)

    /**
     * Transmission of context audio for video, no speaking indicator.
     */
    public object Soundshare : SpeakingFlag(1)

    /**
     * Priority speaker, lowering audio of other speakers.
     */
    public object Priority : SpeakingFlag(2)

    public companion object {
        /**
         * A [List] of all known [SpeakingFlag]s.
         */
        public val entries: List<SpeakingFlag> by lazy(mode = PUBLICATION) {
            listOf(
                Microphone,
                Soundshare,
                Priority,
            )
        }

        /**
         * Returns an instance of [SpeakingFlag] with [SpeakingFlag.shift] equal to the specified
         * [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): SpeakingFlag = when (shift) {
            0 -> Microphone
            1 -> Soundshare
            2 -> Priority
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [SpeakingFlag]s.
 *
 * ## Creating an instance of [SpeakingFlags]
 *
 * You can create an instance of [SpeakingFlags] using the following methods:
 * ```kotlin
 * // from individual SpeakingFlags
 * val speakingFlags1 = SpeakingFlags(SpeakingFlag.Microphone, SpeakingFlag.Soundshare)
 *
 * // from an Iterable
 * val iterable: Iterable<SpeakingFlag> = TODO()
 * val speakingFlags2 = SpeakingFlags(iterable)
 *
 * // using a builder
 * val speakingFlags3 = SpeakingFlags {
 *     +speakingFlags2
 *     +SpeakingFlag.Microphone
 *     -SpeakingFlag.Soundshare
 * }
 * ```
 *
 * ## Modifying an existing instance of [SpeakingFlags]
 *
 * You can create a modified copy of an existing instance of [SpeakingFlags] using the [copy]
 * method:
 * ```kotlin
 * speakingFlags.copy {
 *     +SpeakingFlag.Microphone
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [SpeakingFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val speakingFlags1 = speakingFlags + SpeakingFlag.Microphone
 * val speakingFlags2 = speakingFlags - SpeakingFlag.Soundshare
 * val speakingFlags3 = speakingFlags1 + speakingFlags2
 * ```
 *
 * ## Checking for [SpeakingFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [SpeakingFlags] contains
 * specific [SpeakingFlag]s:
 * ```kotlin
 * val hasSpeakingFlag = SpeakingFlag.Microphone in speakingFlags
 * val hasSpeakingFlags = SpeakingFlags(SpeakingFlag.Microphone, SpeakingFlag.Soundshare) in speakingFlags
 * ```
 *
 * ## Unknown [SpeakingFlag]s
 *
 * Whenever [SpeakingFlag]s haven't been added to Kord yet, they will be deserialized as instances
 * of [SpeakingFlag.Unknown].
 *
 * You can also use [SpeakingFlag.fromShift] to check for [unknown][SpeakingFlag.Unknown]
 * [SpeakingFlag]s.
 * ```kotlin
 * val hasUnknownSpeakingFlag = SpeakingFlag.fromShift(23) in speakingFlags
 * ```
 *
 * @see SpeakingFlag
 * @see SpeakingFlags.Builder
 */
@JvmInline
@Serializable
public value class SpeakingFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    /**
     * A [Set] of all [SpeakingFlag]s contained in this instance of [SpeakingFlags].
     */
    public val values: Set<SpeakingFlag>
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(SpeakingFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [SpeakingFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: SpeakingFlag): Boolean = this.code and flag.code == flag.code

    /**
     * Checks if this instance of [SpeakingFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: SpeakingFlags): Boolean =
            this.code and flags.code == flags.code

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: SpeakingFlag): SpeakingFlags =
            SpeakingFlags(this.code or flag.code)

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: SpeakingFlags): SpeakingFlags =
            SpeakingFlags(this.code or flags.code)

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flag].
     */
    public operator fun minus(flag: SpeakingFlag): SpeakingFlags =
            SpeakingFlags(this.code and flag.code.inv())

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flags].
     */
    public operator fun minus(flags: SpeakingFlags): SpeakingFlags =
            SpeakingFlags(this.code and flags.code.inv())

    /**
     * Returns a copy of this instance of [SpeakingFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): SpeakingFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }

    override fun toString(): String = "SpeakingFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [SpeakingFlag].
         */
        public operator fun SpeakingFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Sets all bits in the [Builder] that are set in this [SpeakingFlags].
         */
        public operator fun SpeakingFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SpeakingFlag].
         */
        public operator fun SpeakingFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SpeakingFlags].
         */
        public operator fun SpeakingFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Returns an instance of [SpeakingFlags] that has all bits set that are currently set in
         * this [Builder].
         */
        public fun build(): SpeakingFlags = SpeakingFlags(code)
    }
}

/**
 * Returns an instance of [SpeakingFlags] built with [SpeakingFlags.Builder].
 */
public inline fun SpeakingFlags(builder: SpeakingFlags.Builder.() -> Unit = {}): SpeakingFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return SpeakingFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [SpeakingFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SpeakingFlags(vararg flags: SpeakingFlag): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SpeakingFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SpeakingFlags(flags: Iterable<SpeakingFlag>): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SpeakingFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("SpeakingFlags0")
public fun SpeakingFlags(flags: Iterable<SpeakingFlags>): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}
