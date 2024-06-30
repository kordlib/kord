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
 * See [ChannelFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#channel-object-channel-flags).
 */
public sealed class ChannelFlag(
    /**
     * The position of the bit that is set in this [ChannelFlag]. This is always in 0..30.
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
     * Returns an instance of [ChannelFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: ChannelFlag): ChannelFlags = ChannelFlags(this.code or flag.code)

    /**
     * Returns an instance of [ChannelFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: ChannelFlags): ChannelFlags =
            ChannelFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ChannelFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown) "ChannelFlag.Unknown(shift=$shift)"
            else "ChannelFlag.${this::class.simpleName}"

    /**
     * An unknown [ChannelFlag].
     *
     * This is used as a fallback for [ChannelFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : ChannelFlag(shift)

    /**
     * This thread is pinned to the top of its parent [GuildForum][ChannelType.GuildForum] or
     * [GuildMedia][ChannelType.GuildMedia] channel.
     */
    public object Pinned : ChannelFlag(1)

    /**
     * Whether a tag is required to be specified when creating a thread in a
     * [GuildForum][ChannelType.GuildForum] or [GuildMedia][ChannelType.GuildMedia] channel.
     */
    public object RequireTag : ChannelFlag(4)

    /**
     * When set hides the embedded media download options. Available only for
     * [GuildMedia][ChannelType.GuildMedia] channels.
     */
    public object HideMediaDownloadOptions : ChannelFlag(15)

    public companion object {
        /**
         * A [List] of all known [ChannelFlag]s.
         */
        public val entries: List<ChannelFlag> by lazy(mode = PUBLICATION) {
            listOf(
                Pinned,
                RequireTag,
                HideMediaDownloadOptions,
            )
        }

        /**
         * Returns an instance of [ChannelFlag] with [ChannelFlag.shift] equal to the specified
         * [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): ChannelFlag = when (shift) {
            1 -> Pinned
            4 -> RequireTag
            15 -> HideMediaDownloadOptions
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [ChannelFlag]s.
 *
 * ## Creating an instance of [ChannelFlags]
 *
 * You can create an instance of [ChannelFlags] using the following methods:
 * ```kotlin
 * // from individual ChannelFlags
 * val channelFlags1 = ChannelFlags(ChannelFlag.Pinned, ChannelFlag.RequireTag)
 *
 * // from an Iterable
 * val iterable: Iterable<ChannelFlag> = TODO()
 * val channelFlags2 = ChannelFlags(iterable)
 *
 * // using a builder
 * val channelFlags3 = ChannelFlags {
 *     +channelFlags2
 *     +ChannelFlag.Pinned
 *     -ChannelFlag.RequireTag
 * }
 * ```
 *
 * ## Modifying an existing instance of [ChannelFlags]
 *
 * You can create a modified copy of an existing instance of [ChannelFlags] using the [copy] method:
 * ```kotlin
 * channelFlags.copy {
 *     +ChannelFlag.Pinned
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [ChannelFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val channelFlags1 = channelFlags + ChannelFlag.Pinned
 * val channelFlags2 = channelFlags - ChannelFlag.RequireTag
 * val channelFlags3 = channelFlags1 + channelFlags2
 * ```
 *
 * ## Checking for [ChannelFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [ChannelFlags] contains
 * specific [ChannelFlag]s:
 * ```kotlin
 * val hasChannelFlag = ChannelFlag.Pinned in channelFlags
 * val hasChannelFlags = ChannelFlags(ChannelFlag.Pinned, ChannelFlag.RequireTag) in channelFlags
 * ```
 *
 * ## Unknown [ChannelFlag]s
 *
 * Whenever [ChannelFlag]s haven't been added to Kord yet, they will be deserialized as instances of
 * [ChannelFlag.Unknown].
 *
 * You can also use [ChannelFlag.fromShift] to check for [unknown][ChannelFlag.Unknown]
 * [ChannelFlag]s.
 * ```kotlin
 * val hasUnknownChannelFlag = ChannelFlag.fromShift(23) in channelFlags
 * ```
 *
 * @see ChannelFlag
 * @see ChannelFlags.Builder
 */
@JvmInline
@Serializable
public value class ChannelFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    /**
     * A [Set] of all [ChannelFlag]s contained in this instance of [ChannelFlags].
     */
    public val values: Set<ChannelFlag>
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(ChannelFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [ChannelFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: ChannelFlag): Boolean = this.code and flag.code == flag.code

    /**
     * Checks if this instance of [ChannelFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: ChannelFlags): Boolean =
            this.code and flags.code == flags.code

    /**
     * Returns an instance of [ChannelFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: ChannelFlag): ChannelFlags = ChannelFlags(this.code or flag.code)

    /**
     * Returns an instance of [ChannelFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: ChannelFlags): ChannelFlags =
            ChannelFlags(this.code or flags.code)

    /**
     * Returns an instance of [ChannelFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flag].
     */
    public operator fun minus(flag: ChannelFlag): ChannelFlags =
            ChannelFlags(this.code and flag.code.inv())

    /**
     * Returns an instance of [ChannelFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flags].
     */
    public operator fun minus(flags: ChannelFlags): ChannelFlags =
            ChannelFlags(this.code and flags.code.inv())

    /**
     * Returns a copy of this instance of [ChannelFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): ChannelFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }

    override fun toString(): String = "ChannelFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [ChannelFlag].
         */
        public operator fun ChannelFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Sets all bits in the [Builder] that are set in this [ChannelFlags].
         */
        public operator fun ChannelFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [ChannelFlag].
         */
        public operator fun ChannelFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [ChannelFlags].
         */
        public operator fun ChannelFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Returns an instance of [ChannelFlags] that has all bits set that are currently set in
         * this [Builder].
         */
        public fun build(): ChannelFlags = ChannelFlags(code)
    }
}

/**
 * Returns an instance of [ChannelFlags] built with [ChannelFlags.Builder].
 */
public inline fun ChannelFlags(builder: ChannelFlags.Builder.() -> Unit = {}): ChannelFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return ChannelFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [ChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun ChannelFlags(vararg flags: ChannelFlag): ChannelFlags = ChannelFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [ChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun ChannelFlags(flags: Iterable<ChannelFlag>): ChannelFlags = ChannelFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [ChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("ChannelFlags0")
public fun ChannelFlags(flags: Iterable<ChannelFlags>): ChannelFlags = ChannelFlags {
    flags.forEach { +it }
}
