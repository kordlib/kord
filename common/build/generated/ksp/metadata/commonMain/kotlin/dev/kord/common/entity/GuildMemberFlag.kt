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
 * See [GuildMemberFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-member-object-guild-member-flags).
 */
public sealed class GuildMemberFlag(
    /**
     * The position of the bit that is set in this [GuildMemberFlag]. This is always in 0..30.
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
     * Returns an instance of [GuildMemberFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: GuildMemberFlag): GuildMemberFlags =
            GuildMemberFlags(this.code or flag.code)

    /**
     * Returns an instance of [GuildMemberFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: GuildMemberFlags): GuildMemberFlags =
            GuildMemberFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildMemberFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "GuildMemberFlag.Unknown(shift=$shift)"
            else "GuildMemberFlag.${this::class.simpleName}"

    /**
     * An unknown [GuildMemberFlag].
     *
     * This is used as a fallback for [GuildMemberFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : GuildMemberFlag(shift)

    /**
     * Member has left and rejoined the guild.
     */
    public object DidRejoin : GuildMemberFlag(0)

    /**
     * Member has completed onboarding.
     */
    public object CompletedOnboarding : GuildMemberFlag(1)

    /**
     * Member is exempt from guild verification requirements.
     */
    public object BypassesVerification : GuildMemberFlag(2)

    /**
     * Member has started onboarding.
     */
    public object StartedOnboarding : GuildMemberFlag(3)

    public companion object {
        /**
         * A [List] of all known [GuildMemberFlag]s.
         */
        public val entries: List<GuildMemberFlag> by lazy(mode = PUBLICATION) {
            listOf(
                DidRejoin,
                CompletedOnboarding,
                BypassesVerification,
                StartedOnboarding,
            )
        }


        /**
         * Returns an instance of [GuildMemberFlag] with [GuildMemberFlag.shift] equal to the
         * specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): GuildMemberFlag = when (shift) {
            0 -> DidRejoin
            1 -> CompletedOnboarding
            2 -> BypassesVerification
            3 -> StartedOnboarding
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [GuildMemberFlag]s.
 *
 * ## Creating an instance of [GuildMemberFlags]
 *
 * You can create an instance of [GuildMemberFlags] using the following methods:
 * ```kotlin
 * // from individual GuildMemberFlags
 * val guildMemberFlags1 = GuildMemberFlags(GuildMemberFlag.DidRejoin, GuildMemberFlag.CompletedOnboarding)
 *
 * // from an Iterable
 * val iterable: Iterable<GuildMemberFlag> = TODO()
 * val guildMemberFlags2 = GuildMemberFlags(iterable)
 *
 * // using a builder
 * val guildMemberFlags3 = GuildMemberFlags {
 *     +guildMemberFlags2
 *     +GuildMemberFlag.DidRejoin
 *     -GuildMemberFlag.CompletedOnboarding
 * }
 * ```
 *
 * ## Modifying an existing instance of [GuildMemberFlags]
 *
 * You can create a modified copy of an existing instance of [GuildMemberFlags] using the [copy]
 * method:
 * ```kotlin
 * guildMemberFlags.copy {
 *     +GuildMemberFlag.DidRejoin
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [GuildMemberFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val guildMemberFlags1 = guildMemberFlags + GuildMemberFlag.DidRejoin
 * val guildMemberFlags2 = guildMemberFlags - GuildMemberFlag.CompletedOnboarding
 * val guildMemberFlags3 = guildMemberFlags1 + guildMemberFlags2
 * ```
 *
 * ## Checking for [GuildMemberFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [GuildMemberFlags] contains
 * specific [GuildMemberFlag]s:
 * ```kotlin
 * val hasGuildMemberFlag = GuildMemberFlag.DidRejoin in guildMemberFlags
 * val hasGuildMemberFlags = GuildMemberFlags(GuildMemberFlag.DidRejoin, GuildMemberFlag.CompletedOnboarding) in guildMemberFlags
 * ```
 *
 * ## Unknown [GuildMemberFlag]s
 *
 * Whenever [GuildMemberFlag]s haven't been added to Kord yet, they will be deserialized as
 * instances of [GuildMemberFlag.Unknown].
 *
 * You can also use [GuildMemberFlag.fromShift] to check for [unknown][GuildMemberFlag.Unknown]
 * [GuildMemberFlag]s.
 * ```kotlin
 * val hasUnknownGuildMemberFlag = GuildMemberFlag.fromShift(23) in guildMemberFlags
 * ```
 *
 * @see GuildMemberFlag
 * @see GuildMemberFlags.Builder
 */
@JvmInline
@Serializable
public value class GuildMemberFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    /**
     * A [Set] of all [GuildMemberFlag]s contained in this instance of [GuildMemberFlags].
     */
    public val values: Set<GuildMemberFlag>
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(GuildMemberFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [GuildMemberFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: GuildMemberFlag): Boolean =
            this.code and flag.code == flag.code

    /**
     * Checks if this instance of [GuildMemberFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: GuildMemberFlags): Boolean =
            this.code and flags.code == flags.code

    /**
     * Returns an instance of [GuildMemberFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: GuildMemberFlag): GuildMemberFlags =
            GuildMemberFlags(this.code or flag.code)

    /**
     * Returns an instance of [GuildMemberFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: GuildMemberFlags): GuildMemberFlags =
            GuildMemberFlags(this.code or flags.code)

    /**
     * Returns an instance of [GuildMemberFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flag].
     */
    public operator fun minus(flag: GuildMemberFlag): GuildMemberFlags =
            GuildMemberFlags(this.code and flag.code.inv())

    /**
     * Returns an instance of [GuildMemberFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flags].
     */
    public operator fun minus(flags: GuildMemberFlags): GuildMemberFlags =
            GuildMemberFlags(this.code and flags.code.inv())

    /**
     * Returns a copy of this instance of [GuildMemberFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): GuildMemberFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }

    override fun toString(): String = "GuildMemberFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [GuildMemberFlag].
         */
        public operator fun GuildMemberFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Sets all bits in the [Builder] that are set in this [GuildMemberFlags].
         */
        public operator fun GuildMemberFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [GuildMemberFlag].
         */
        public operator fun GuildMemberFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [GuildMemberFlags].
         */
        public operator fun GuildMemberFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Returns an instance of [GuildMemberFlags] that has all bits set that are currently set in
         * this [Builder].
         */
        public fun build(): GuildMemberFlags = GuildMemberFlags(code)
    }
}

/**
 * Returns an instance of [GuildMemberFlags] built with [GuildMemberFlags.Builder].
 */
public inline fun GuildMemberFlags(builder: GuildMemberFlags.Builder.() -> Unit = {}):
        GuildMemberFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return GuildMemberFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [GuildMemberFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun GuildMemberFlags(vararg flags: GuildMemberFlag): GuildMemberFlags = GuildMemberFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [GuildMemberFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun GuildMemberFlags(flags: Iterable<GuildMemberFlag>): GuildMemberFlags = GuildMemberFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [GuildMemberFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("GuildMemberFlags0")
public fun GuildMemberFlags(flags: Iterable<GuildMemberFlags>): GuildMemberFlags =
        GuildMemberFlags {
    flags.forEach { +it }
}
