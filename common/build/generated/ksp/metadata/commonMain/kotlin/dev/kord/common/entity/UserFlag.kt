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
 * See [UserFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/user#user-object-user-flags).
 */
public sealed class UserFlag(
    /**
     * The position of the bit that is set in this [UserFlag]. This is always in 0..30.
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
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: UserFlag): UserFlags = UserFlags(this.code or flag.code)

    /**
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: UserFlags): UserFlags = UserFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is UserFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown) "UserFlag.Unknown(shift=$shift)"
            else "UserFlag.${this::class.simpleName}"

    /**
     * An unknown [UserFlag].
     *
     * This is used as a fallback for [UserFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : UserFlag(shift)

    /**
     * Discord Employee
     */
    public object DiscordEmployee : UserFlag(0)

    /**
     * Partnered Server Owner
     */
    public object DiscordPartner : UserFlag(1)

    /**
     * HypeSquad Events Member
     */
    public object HypeSquad : UserFlag(2)

    /**
     * Bug Hunter Level 1
     */
    public object BugHunterLevel1 : UserFlag(3)

    /**
     * House Bravery Member
     */
    public object HouseBravery : UserFlag(6)

    /**
     * House Brilliance Member
     */
    public object HouseBrilliance : UserFlag(7)

    /**
     * House Balance Member
     */
    public object HouseBalance : UserFlag(8)

    /**
     * Early Nitro Supporter
     */
    public object EarlySupporter : UserFlag(9)

    /**
     * User is a team
     */
    public object TeamUser : UserFlag(10)

    /**
     * Bug Hunter Level 2
     */
    public object BugHunterLevel2 : UserFlag(14)

    /**
     * Verified Bot
     */
    public object VerifiedBot : UserFlag(16)

    /**
     * Early Verified Bot Developer
     */
    public object VerifiedBotDeveloper : UserFlag(17)

    /**
     * Moderator Programs Alumni
     */
    public object DiscordCertifiedModerator : UserFlag(18)

    /**
     * Bot uses only HTTP interactions and is shown in the online member list.
     */
    public object BotHttpInteractions : UserFlag(19)

    /**
     * User is an [Active Developer](https://support-dev.discord.com/hc/articles/10113997751447).
     */
    public object ActiveDeveloper : UserFlag(22)

    public companion object {
        /**
         * A [List] of all known [UserFlag]s.
         */
        public val entries: List<UserFlag> by lazy(mode = PUBLICATION) {
            listOf(
                DiscordEmployee,
                DiscordPartner,
                HypeSquad,
                BugHunterLevel1,
                HouseBravery,
                HouseBrilliance,
                HouseBalance,
                EarlySupporter,
                TeamUser,
                BugHunterLevel2,
                VerifiedBot,
                VerifiedBotDeveloper,
                DiscordCertifiedModerator,
                BotHttpInteractions,
                ActiveDeveloper,
            )
        }

        /**
         * Returns an instance of [UserFlag] with [UserFlag.shift] equal to the specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): UserFlag = when (shift) {
            0 -> DiscordEmployee
            1 -> DiscordPartner
            2 -> HypeSquad
            3 -> BugHunterLevel1
            6 -> HouseBravery
            7 -> HouseBrilliance
            8 -> HouseBalance
            9 -> EarlySupporter
            10 -> TeamUser
            14 -> BugHunterLevel2
            16 -> VerifiedBot
            17 -> VerifiedBotDeveloper
            18 -> DiscordCertifiedModerator
            19 -> BotHttpInteractions
            22 -> ActiveDeveloper
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [UserFlag]s.
 *
 * ## Creating an instance of [UserFlags]
 *
 * You can create an instance of [UserFlags] using the following methods:
 * ```kotlin
 * // from individual UserFlags
 * val userFlags1 = UserFlags(UserFlag.DiscordEmployee, UserFlag.DiscordPartner)
 *
 * // from an Iterable
 * val iterable: Iterable<UserFlag> = TODO()
 * val userFlags2 = UserFlags(iterable)
 *
 * // using a builder
 * val userFlags3 = UserFlags {
 *     +userFlags2
 *     +UserFlag.DiscordEmployee
 *     -UserFlag.DiscordPartner
 * }
 * ```
 *
 * ## Modifying an existing instance of [UserFlags]
 *
 * You can create a modified copy of an existing instance of [UserFlags] using the [copy] method:
 * ```kotlin
 * userFlags.copy {
 *     +UserFlag.DiscordEmployee
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [UserFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val userFlags1 = userFlags + UserFlag.DiscordEmployee
 * val userFlags2 = userFlags - UserFlag.DiscordPartner
 * val userFlags3 = userFlags1 + userFlags2
 * ```
 *
 * ## Checking for [UserFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [UserFlags] contains specific
 * [UserFlag]s:
 * ```kotlin
 * val hasUserFlag = UserFlag.DiscordEmployee in userFlags
 * val hasUserFlags = UserFlags(UserFlag.DiscordEmployee, UserFlag.DiscordPartner) in userFlags
 * ```
 *
 * ## Unknown [UserFlag]s
 *
 * Whenever [UserFlag]s haven't been added to Kord yet, they will be deserialized as instances of
 * [UserFlag.Unknown].
 *
 * You can also use [UserFlag.fromShift] to check for [unknown][UserFlag.Unknown] [UserFlag]s.
 * ```kotlin
 * val hasUnknownUserFlag = UserFlag.fromShift(23) in userFlags
 * ```
 *
 * @see UserFlag
 * @see UserFlags.Builder
 */
@JvmInline
@Serializable
public value class UserFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    /**
     * A [Set] of all [UserFlag]s contained in this instance of [UserFlags].
     */
    public val values: Set<UserFlag>
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(UserFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [UserFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: UserFlag): Boolean = this.code and flag.code == flag.code

    /**
     * Checks if this instance of [UserFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: UserFlags): Boolean = this.code and flags.code == flags.code

    /**
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: UserFlag): UserFlags = UserFlags(this.code or flag.code)

    /**
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: UserFlags): UserFlags = UserFlags(this.code or flags.code)

    /**
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flag].
     */
    public operator fun minus(flag: UserFlag): UserFlags = UserFlags(this.code and flag.code.inv())

    /**
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flags].
     */
    public operator fun minus(flags: UserFlags): UserFlags =
            UserFlags(this.code and flags.code.inv())

    /**
     * Returns a copy of this instance of [UserFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): UserFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }

    @Deprecated(
        level = DeprecationLevel.HIDDEN,
        message = "Binary compatibility, keep for some releases.",
    )
    public inline fun copy0(builder: Builder.() -> Unit): UserFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return copy(builder)
    }

    override fun toString(): String = "UserFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [UserFlag].
         */
        public operator fun UserFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Sets all bits in the [Builder] that are set in this [UserFlags].
         */
        public operator fun UserFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [UserFlag].
         */
        public operator fun UserFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [UserFlags].
         */
        public operator fun UserFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Returns an instance of [UserFlags] that has all bits set that are currently set in this
         * [Builder].
         */
        public fun build(): UserFlags = UserFlags(code)
    }
}

/**
 * Returns an instance of [UserFlags] built with [UserFlags.Builder].
 */
public inline fun UserFlags(builder: UserFlags.Builder.() -> Unit = {}): UserFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return UserFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [UserFlags] that has all bits set that are set in any element of [flags].
 */
public fun UserFlags(vararg flags: UserFlag): UserFlags = UserFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [UserFlags] that has all bits set that are set in any element of [flags].
 */
public fun UserFlags(flags: Iterable<UserFlag>): UserFlags = UserFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [UserFlags] that has all bits set that are set in any element of [flags].
 */
@JvmName("UserFlags0")
public fun UserFlags(flags: Iterable<UserFlags>): UserFlags = UserFlags {
    flags.forEach { +it }
}
