@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import dev.kord.common.Class
import dev.kord.common.java
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.enums.EnumEntries
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
    public operator fun plus(flag: UserFlag): UserFlags = UserFlags(this.code or flag.code, null)

    /**
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: UserFlags): UserFlags = UserFlags(this.code or flags.code, null)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is UserFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown) "UserFlag.Unknown(shift=$shift)"
            else "UserFlag.${this::class.simpleName}"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message = "UserFlag is no longer an enum class. Deprecated without a replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith", "DEPRECATION"))
    @Deprecated(message = "UserFlag is no longer an enum class. Deprecated without a replacement.")
    public fun ordinal(): Int = when (this) {
        DiscordEmployee -> 0
        DiscordPartner -> 1
        HypeSquad -> 2
        BugHunterLevel1 -> 3
        HouseBravery -> 4
        HouseBrilliance -> 5
        HouseBalance -> 6
        EarlySupporter -> 7
        TeamUser -> 8
        System -> 9
        BugHunterLevel2 -> 10
        VerifiedBot -> 11
        VerifiedBotDeveloper -> 12
        DiscordCertifiedModerator -> 13
        BotHttpInteractions -> 14
        ActiveDeveloper -> 15
        is Unknown -> Int.MAX_VALUE
    }

    /**
     * @suppress
     */
    @Deprecated(
        message = "UserFlag is no longer an enum class.",
        replaceWith = ReplaceWith(expression = "UserFlag::class.java", imports =
                    arrayOf("dev.kord.common.entity.UserFlag")),
    )
    public fun getDeclaringClass(): Class<UserFlag> = UserFlag::class.java

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

    @Deprecated(
        "'UserFlag.System' is no longer documented. You can still use it with 'UserFlag.fromShift(12)'.",
        ReplaceWith("UserFlag.fromShift(12)", imports = ["dev.kord.common.entity.UserFlag"]),
        DeprecationLevel.WARNING,
    )
    public object System : UserFlag(12)

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
                @Suppress("DEPRECATION") System,
                BugHunterLevel2,
                VerifiedBot,
                VerifiedBotDeveloper,
                DiscordCertifiedModerator,
                BotHttpInteractions,
                ActiveDeveloper,
            )
        }


        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val DiscordEmployee: UserFlag = DiscordEmployee

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val DiscordPartner: UserFlag = DiscordPartner

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val HypeSquad: UserFlag = HypeSquad

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val BugHunterLevel1: UserFlag = BugHunterLevel1

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val HouseBravery: UserFlag = HouseBravery

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val HouseBrilliance: UserFlag = HouseBrilliance

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val HouseBalance: UserFlag = HouseBalance

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val EarlySupporter: UserFlag = EarlySupporter

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val TeamUser: UserFlag = TeamUser

        @Suppress(names = arrayOf("DEPRECATION"))
        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val System: UserFlag = System

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val BugHunterLevel2: UserFlag = BugHunterLevel2

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val VerifiedBot: UserFlag = VerifiedBot

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val VerifiedBotDeveloper: UserFlag = VerifiedBotDeveloper

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val DiscordCertifiedModerator: UserFlag = DiscordCertifiedModerator

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val BotHttpInteractions: UserFlag = BotHttpInteractions

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val ActiveDeveloper: UserFlag = ActiveDeveloper

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
            12 -> @Suppress("DEPRECATION") System
            14 -> BugHunterLevel2
            16 -> VerifiedBot
            17 -> VerifiedBotDeveloper
            18 -> DiscordCertifiedModerator
            19 -> BotHttpInteractions
            22 -> ActiveDeveloper
            else -> Unknown(shift)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "UserFlag is no longer an enum class. Deprecated without a replacement.")
        @JvmStatic
        public open fun valueOf(name: String): UserFlag = when (name) {
            "DiscordEmployee" -> DiscordEmployee
            "DiscordPartner" -> DiscordPartner
            "HypeSquad" -> HypeSquad
            "BugHunterLevel1" -> BugHunterLevel1
            "HouseBravery" -> HouseBravery
            "HouseBrilliance" -> HouseBrilliance
            "HouseBalance" -> HouseBalance
            "EarlySupporter" -> EarlySupporter
            "TeamUser" -> TeamUser
            "System" -> @Suppress("DEPRECATION") System
            "BugHunterLevel2" -> BugHunterLevel2
            "VerifiedBot" -> VerifiedBot
            "VerifiedBotDeveloper" -> VerifiedBotDeveloper
            "DiscordCertifiedModerator" -> DiscordCertifiedModerator
            "BotHttpInteractions" -> BotHttpInteractions
            "ActiveDeveloper" -> ActiveDeveloper
            else -> throw IllegalArgumentException(name)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT"))
        @Deprecated(
            message = "UserFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "UserFlag.entries.toTypedArray()", imports =
                        arrayOf("dev.kord.common.entity.UserFlag")),
        )
        @JvmStatic
        public open fun values(): Array<UserFlag> = entries.toTypedArray()

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "UPPER_BOUND_VIOLATED"))
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "UserFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "UserFlag.entries", imports =
                        arrayOf("dev.kord.common.entity.UserFlag")),
        )
        @JvmStatic
        public open fun getEntries(): EnumEntries<UserFlag> = EnumEntriesList

        @Suppress(names = arrayOf("SEALED_INHERITOR_IN_DIFFERENT_MODULE",
                        "SEALED_INHERITOR_IN_DIFFERENT_PACKAGE", "UPPER_BOUND_VIOLATED"))
        private object EnumEntriesList : EnumEntries<UserFlag>, List<UserFlag> by entries {
            override fun equals(other: Any?): Boolean = entries == other

            override fun hashCode(): Int = entries.hashCode()

            override fun toString(): String = entries.toString()
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
@Serializable(with = UserFlags.Serializer::class)
public class UserFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
    @Suppress("UNUSED_PARAMETER") unused: Nothing?,
) {
    // TODO uncomment annotation in DiscordUser.kt and delete this file when this constructor is removed after
    //  deprecation cycle
    @Deprecated(
        "Don't construct an instance of 'UserFlags' from a raw code. Use the factory functions described in the " +
            "documentation instead.",
        ReplaceWith("UserFlags.Builder(code).build()", "dev.kord.common.entity.UserFlags"),
        DeprecationLevel.WARNING,
    )
    public constructor(code: Int) : this(code, null)
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
     * @suppress
     */
    @Deprecated(
        message = "Renamed to 'values'.",
        replaceWith = ReplaceWith(expression = "this.values", imports = arrayOf()),
    )
    public val flags: List<UserFlag>
        get() = values.toList()

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
    public operator fun plus(flag: UserFlag): UserFlags = UserFlags(this.code or flag.code, null)

    /**
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: UserFlags): UserFlags = UserFlags(this.code or flags.code, null)

    /**
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flag].
     */
    public operator fun minus(flag: UserFlag): UserFlags = UserFlags(this.code and flag.code.inv(), null)

    /**
     * Returns an instance of [UserFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flags].
     */
    public operator fun minus(flags: UserFlags): UserFlags =
            UserFlags(this.code and flags.code.inv(), null)

    /**
     * Returns a copy of this instance of [UserFlags] modified with [builder].
     */
    @JvmName("copy0") // TODO other name when deprecated overload is removed
    public inline fun copy(builder: Builder.() -> Unit): UserFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }

    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "DEPRECATION")
    @Deprecated(
        "'UserFlags.UserFlagsBuilder' is deprecated, use 'UserFlags.Builder' instead.",
        level = DeprecationLevel.WARNING,
    )
    @kotlin.internal.LowPriorityInOverloadResolution
    public inline fun copy(block: UserFlagsBuilder.() -> Unit): UserFlags {
        contract { callsInPlace(block, EXACTLY_ONCE) }
        return UserFlagsBuilder(code).apply(block).flags()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is UserFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "UserFlags(values=$values)"

    /**
     * @suppress
     */
    @Deprecated(
        message = "UserFlags is no longer a data class.",
        replaceWith = ReplaceWith(expression = "this.code", imports = arrayOf()),
    )
    public operator fun component1(): Int = code

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message = "UserFlags is no longer a data class. Deprecated without a replacement.")
    public fun copy(code: Int = this.code): UserFlags = UserFlags(code, null)

    @Deprecated(
        "Renamed to 'Builder'.",
        ReplaceWith("UserFlags.Builder", imports = ["dev.kord.common.entity.UserFlags"]),
        DeprecationLevel.WARNING,
    )
    public class UserFlagsBuilder(code: Int = 0) {
        private val delegate = Builder(code)
        public operator fun UserFlag.unaryPlus(): Unit = with(delegate) { unaryPlus() }
        public operator fun UserFlag.unaryMinus(): Unit = with(delegate) { unaryMinus() }
        public fun flags(): UserFlags = delegate.build()
    }

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
        public fun build(): UserFlags = UserFlags(code, null)
    }

    internal object Serializer : KSerializer<UserFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.UserFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: UserFlags) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): UserFlags =
                UserFlags(decoder.decodeSerializableValue(delegate), null)
    }

    public companion object {
        @Suppress("DEPRECATION")
        @Deprecated(
            "Renamed to 'Companion', which no longer implements 'KSerializer<UserFlags>'.",
            ReplaceWith("UserFlags.serializer()", imports = ["dev.kord.common.entity.UserFlags"]),
            DeprecationLevel.WARNING,
        )
        @JvmField
        public val UserFlagsSerializer: UserFlagsSerializer = UserFlagsSerializer()
    }

    @Deprecated(
        "Renamed to 'Companion', which no longer implements 'KSerializer<UserFlags>'.",
        ReplaceWith("UserFlags.serializer()", imports = ["dev.kord.common.entity.UserFlags"]),
        DeprecationLevel.WARNING,
    )
    public class UserFlagsSerializer internal constructor() : KSerializer<UserFlags> by Serializer {
        public fun serializer(): KSerializer<UserFlags> = this
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
public fun UserFlags(vararg flags: UserFlags): UserFlags = UserFlags {
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
