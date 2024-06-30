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
 * See [ActivityFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-flags).
 */
public sealed class ActivityFlag(
    /**
     * The position of the bit that is set in this [ActivityFlag]. This is always in 0..30.
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
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: ActivityFlag): ActivityFlags =
            ActivityFlags(this.value or flag.value)

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: ActivityFlags): ActivityFlags =
            ActivityFlags(this.value or flags.value)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ActivityFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "ActivityFlag.Unknown(shift=$shift)"
            else "ActivityFlag.${this::class.simpleName}"

    /**
     * An unknown [ActivityFlag].
     *
     * This is used as a fallback for [ActivityFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : ActivityFlag(shift)

    public object Instance : ActivityFlag(0)

    public object Join : ActivityFlag(1)

    public object Spectate : ActivityFlag(2)

    public object JoinRequest : ActivityFlag(3)

    public object Sync : ActivityFlag(4)

    public object Play : ActivityFlag(5)

    public object PartyPrivacyFriends : ActivityFlag(6)

    public object PartyPrivacyVoiceChannel : ActivityFlag(7)

    public object Embedded : ActivityFlag(8)

    public companion object {
        /**
         * A [List] of all known [ActivityFlag]s.
         */
        public val entries: List<ActivityFlag> by lazy(mode = PUBLICATION) {
            listOf(
                Instance,
                Join,
                Spectate,
                JoinRequest,
                Sync,
                Play,
                PartyPrivacyFriends,
                PartyPrivacyVoiceChannel,
                Embedded,
            )
        }

        /**
         * Returns an instance of [ActivityFlag] with [ActivityFlag.shift] equal to the specified
         * [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): ActivityFlag = when (shift) {
            0 -> Instance
            1 -> Join
            2 -> Spectate
            3 -> JoinRequest
            4 -> Sync
            5 -> Play
            6 -> PartyPrivacyFriends
            7 -> PartyPrivacyVoiceChannel
            8 -> Embedded
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [ActivityFlag]s.
 *
 * ## Creating an instance of [ActivityFlags]
 *
 * You can create an instance of [ActivityFlags] using the following methods:
 * ```kotlin
 * // from individual ActivityFlags
 * val activityFlags1 = ActivityFlags(ActivityFlag.Instance, ActivityFlag.Join)
 *
 * // from an Iterable
 * val iterable: Iterable<ActivityFlag> = TODO()
 * val activityFlags2 = ActivityFlags(iterable)
 *
 * // using a builder
 * val activityFlags3 = ActivityFlags {
 *     +activityFlags2
 *     +ActivityFlag.Instance
 *     -ActivityFlag.Join
 * }
 * ```
 *
 * ## Modifying an existing instance of [ActivityFlags]
 *
 * You can create a modified copy of an existing instance of [ActivityFlags] using the [copy]
 * method:
 * ```kotlin
 * activityFlags.copy {
 *     +ActivityFlag.Instance
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [ActivityFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val activityFlags1 = activityFlags + ActivityFlag.Instance
 * val activityFlags2 = activityFlags - ActivityFlag.Join
 * val activityFlags3 = activityFlags1 + activityFlags2
 * ```
 *
 * ## Checking for [ActivityFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [ActivityFlags] contains
 * specific [ActivityFlag]s:
 * ```kotlin
 * val hasActivityFlag = ActivityFlag.Instance in activityFlags
 * val hasActivityFlags = ActivityFlags(ActivityFlag.Instance, ActivityFlag.Join) in activityFlags
 * ```
 *
 * ## Unknown [ActivityFlag]s
 *
 * Whenever [ActivityFlag]s haven't been added to Kord yet, they will be deserialized as instances
 * of [ActivityFlag.Unknown].
 *
 * You can also use [ActivityFlag.fromShift] to check for [unknown][ActivityFlag.Unknown]
 * [ActivityFlag]s.
 * ```kotlin
 * val hasUnknownActivityFlag = ActivityFlag.fromShift(23) in activityFlags
 * ```
 *
 * @see ActivityFlag
 * @see ActivityFlags.Builder
 */
@JvmInline
@Serializable
public value class ActivityFlags internal constructor(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    /**
     * A [Set] of all [ActivityFlag]s contained in this instance of [ActivityFlags].
     */
    public val values: Set<ActivityFlag>
        get() = buildSet {
            var remaining = value
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(ActivityFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [ActivityFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: ActivityFlag): Boolean =
            this.value and flag.value == flag.value

    /**
     * Checks if this instance of [ActivityFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: ActivityFlags): Boolean =
            this.value and flags.value == flags.value

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: ActivityFlag): ActivityFlags =
            ActivityFlags(this.value or flag.value)

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: ActivityFlags): ActivityFlags =
            ActivityFlags(this.value or flags.value)

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flag].
     */
    public operator fun minus(flag: ActivityFlag): ActivityFlags =
            ActivityFlags(this.value and flag.value.inv())

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flags].
     */
    public operator fun minus(flags: ActivityFlags): ActivityFlags =
            ActivityFlags(this.value and flags.value.inv())

    /**
     * Returns a copy of this instance of [ActivityFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): ActivityFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(value).apply(builder).build()
    }

    override fun toString(): String = "ActivityFlags(values=$values)"

    public class Builder(
        private var `value`: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [ActivityFlag].
         */
        public operator fun ActivityFlag.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        /**
         * Sets all bits in the [Builder] that are set in this [ActivityFlags].
         */
        public operator fun ActivityFlags.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [ActivityFlag].
         */
        public operator fun ActivityFlag.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [ActivityFlags].
         */
        public operator fun ActivityFlags.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        /**
         * Returns an instance of [ActivityFlags] that has all bits set that are currently set in
         * this [Builder].
         */
        public fun build(): ActivityFlags = ActivityFlags(value)
    }
}

/**
 * Returns an instance of [ActivityFlags] built with [ActivityFlags.Builder].
 */
public inline fun ActivityFlags(builder: ActivityFlags.Builder.() -> Unit = {}): ActivityFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return ActivityFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [ActivityFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun ActivityFlags(vararg flags: ActivityFlag): ActivityFlags = ActivityFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [ActivityFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun ActivityFlags(flags: Iterable<ActivityFlag>): ActivityFlags = ActivityFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [ActivityFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("ActivityFlags0")
public fun ActivityFlags(flags: Iterable<ActivityFlags>): ActivityFlags = ActivityFlags {
    flags.forEach { +it }
}
