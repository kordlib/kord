// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmField
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

/**
 * See [SystemChannelFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-system-channel-flags).
 */
public sealed class SystemChannelFlag(
    /**
     * The position of the bit that is set in this [SystemChannelFlag]. This is always in 0..30.
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
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: SystemChannelFlag): SystemChannelFlags =
            SystemChannelFlags(this.code or flag.code)

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: SystemChannelFlags): SystemChannelFlags =
            SystemChannelFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SystemChannelFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "SystemChannelFlag.Unknown(shift=$shift)"
            else "SystemChannelFlag.${this::class.simpleName}"

    /**
     * An unknown [SystemChannelFlag].
     *
     * This is used as a fallback for [SystemChannelFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : SystemChannelFlag(shift)

    /**
     * Suppress member join notifications.
     */
    public object SuppressJoinNotifications : SystemChannelFlag(0)

    /**
     * Suppress server boost notifications.
     */
    public object SuppressPremiumSubscriptions : SystemChannelFlag(1)

    /**
     * Suppress server setup tips.
     */
    public object SuppressGuildReminderNotifications : SystemChannelFlag(2)

    /**
     * Hide member join sticker reply buttons.
     */
    public object SuppressJoinNotificationReplies : SystemChannelFlag(3)

    /**
     * Suppress role subscription purchase and renewal notifications.
     */
    public object SuppressRoleSubscriptionPurchaseNotifications : SystemChannelFlag(4)

    /**
     * Hide role subscription sticker reply buttons.
     */
    public object SuppressRoleSubscriptionPurchaseNotificationReplies : SystemChannelFlag(5)

    public companion object {
        /**
         * A [List] of all known [SystemChannelFlag]s.
         */
        public val entries: List<SystemChannelFlag> by lazy(mode = PUBLICATION) {
            listOf(
                SuppressJoinNotifications,
                SuppressPremiumSubscriptions,
                SuppressGuildReminderNotifications,
                SuppressJoinNotificationReplies,
                SuppressRoleSubscriptionPurchaseNotifications,
                SuppressRoleSubscriptionPurchaseNotificationReplies,
            )
        }

        /**
         * Returns an instance of [SystemChannelFlag] with [SystemChannelFlag.shift] equal to the
         * specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): SystemChannelFlag = when (shift) {
            0 -> SuppressJoinNotifications
            1 -> SuppressPremiumSubscriptions
            2 -> SuppressGuildReminderNotifications
            3 -> SuppressJoinNotificationReplies
            4 -> SuppressRoleSubscriptionPurchaseNotifications
            5 -> SuppressRoleSubscriptionPurchaseNotificationReplies
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [SystemChannelFlag]s.
 *
 * ## Creating an instance of [SystemChannelFlags]
 *
 * You can create an instance of [SystemChannelFlags] using the following methods:
 * ```kotlin
 * // from individual SystemChannelFlags
 * val systemChannelFlags1 = SystemChannelFlags(SystemChannelFlag.SuppressJoinNotifications, SystemChannelFlag.SuppressPremiumSubscriptions)
 *
 * // from an Iterable
 * val iterable: Iterable<SystemChannelFlag> = TODO()
 * val systemChannelFlags2 = SystemChannelFlags(iterable)
 *
 * // using a builder
 * val systemChannelFlags3 = SystemChannelFlags {
 *     +systemChannelFlags2
 *     +SystemChannelFlag.SuppressJoinNotifications
 *     -SystemChannelFlag.SuppressPremiumSubscriptions
 * }
 * ```
 *
 * ## Modifying an existing instance of [SystemChannelFlags]
 *
 * You can create a modified copy of an existing instance of [SystemChannelFlags] using the [copy]
 * method:
 * ```kotlin
 * systemChannelFlags.copy {
 *     +SystemChannelFlag.SuppressJoinNotifications
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [SystemChannelFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val systemChannelFlags1 = systemChannelFlags + SystemChannelFlag.SuppressJoinNotifications
 * val systemChannelFlags2 = systemChannelFlags - SystemChannelFlag.SuppressPremiumSubscriptions
 * val systemChannelFlags3 = systemChannelFlags1 + systemChannelFlags2
 * ```
 *
 * ## Checking for [SystemChannelFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [SystemChannelFlags] contains
 * specific [SystemChannelFlag]s:
 * ```kotlin
 * val hasSystemChannelFlag = SystemChannelFlag.SuppressJoinNotifications in systemChannelFlags
 * val hasSystemChannelFlags = SystemChannelFlags(SystemChannelFlag.SuppressJoinNotifications, SystemChannelFlag.SuppressPremiumSubscriptions) in systemChannelFlags
 * ```
 *
 * ## Unknown [SystemChannelFlag]s
 *
 * Whenever [SystemChannelFlag]s haven't been added to Kord yet, they will be deserialized as
 * instances of [SystemChannelFlag.Unknown].
 *
 * You can also use [SystemChannelFlag.fromShift] to check for [unknown][SystemChannelFlag.Unknown]
 * [SystemChannelFlag]s.
 * ```kotlin
 * val hasUnknownSystemChannelFlag = SystemChannelFlag.fromShift(23) in systemChannelFlags
 * ```
 *
 * @see SystemChannelFlag
 * @see SystemChannelFlags.Builder
 */
@JvmInline
@Serializable
public value class SystemChannelFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    /**
     * A [Set] of all [SystemChannelFlag]s contained in this instance of [SystemChannelFlags].
     */
    public val values: Set<SystemChannelFlag>
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(SystemChannelFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [SystemChannelFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: SystemChannelFlag): Boolean =
            this.code and flag.code == flag.code

    /**
     * Checks if this instance of [SystemChannelFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: SystemChannelFlags): Boolean =
            this.code and flags.code == flags.code

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: SystemChannelFlag): SystemChannelFlags =
            SystemChannelFlags(this.code or flag.code)

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: SystemChannelFlags): SystemChannelFlags =
            SystemChannelFlags(this.code or flags.code)

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this`
     * except the bits that are set in [flag].
     */
    public operator fun minus(flag: SystemChannelFlag): SystemChannelFlags =
            SystemChannelFlags(this.code and flag.code.inv())

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this`
     * except the bits that are set in [flags].
     */
    public operator fun minus(flags: SystemChannelFlags): SystemChannelFlags =
            SystemChannelFlags(this.code and flags.code.inv())

    /**
     * Returns a copy of this instance of [SystemChannelFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): SystemChannelFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }

    override fun toString(): String = "SystemChannelFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [SystemChannelFlag].
         */
        public operator fun SystemChannelFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Sets all bits in the [Builder] that are set in this [SystemChannelFlags].
         */
        public operator fun SystemChannelFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SystemChannelFlag].
         */
        public operator fun SystemChannelFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SystemChannelFlags].
         */
        public operator fun SystemChannelFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Returns an instance of [SystemChannelFlags] that has all bits set that are currently set
         * in this [Builder].
         */
        public fun build(): SystemChannelFlags = SystemChannelFlags(code)
    }

    public companion object {
        @Suppress(names = arrayOf("DEPRECATION_ERROR"))
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "Renamed to 'Companion'.",
            replaceWith = ReplaceWith(expression = "SystemChannelFlags.Companion", imports =
                        arrayOf("dev.kord.common.entity.SystemChannelFlags")),
        )
        @JvmField
        public val NewCompanion: NewCompanion = NewCompanion()
    }

    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Renamed to 'Companion'.",
        replaceWith = ReplaceWith(expression = "SystemChannelFlags.Companion", imports =
                    arrayOf("dev.kord.common.entity.SystemChannelFlags")),
    )
    public class NewCompanion internal constructor() {
        public fun serializer(): KSerializer<SystemChannelFlags> = SystemChannelFlags.serializer()
    }
}

/**
 * Returns an instance of [SystemChannelFlags] built with [SystemChannelFlags.Builder].
 */
public inline fun SystemChannelFlags(builder: SystemChannelFlags.Builder.() -> Unit = {}):
        SystemChannelFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return SystemChannelFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [SystemChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SystemChannelFlags(vararg flags: SystemChannelFlag): SystemChannelFlags =
        SystemChannelFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SystemChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SystemChannelFlags(flags: Iterable<SystemChannelFlag>): SystemChannelFlags =
        SystemChannelFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SystemChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("SystemChannelFlags0")
public fun SystemChannelFlags(flags: Iterable<SystemChannelFlags>): SystemChannelFlags =
        SystemChannelFlags {
    flags.forEach { +it }
}
