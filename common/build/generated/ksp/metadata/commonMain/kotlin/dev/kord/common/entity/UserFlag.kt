// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import dev.kord.common.Class
import dev.kord.common.`annotation`.KordUnsafe
import dev.kord.common.java
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
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
 * Convenience container of multiple [UserFlags][UserFlag] which can be combined into one.
 *
 * ## Creating a collection of message flags
 * You can create an [UserFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = UserFlags(UserFlag.DiscordEmployee, UserFlag.DiscordPartner)
 * // From an iterable
 * val flags2 = UserFlags(listOf(UserFlag.DiscordEmployee, UserFlag.DiscordPartner))
 * // Using a builder
 * val flags3 = UserFlags {
 *  +UserFlag.DiscordEmployee
 *  -UserFlag.DiscordPartner
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [UserFlags] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +UserFlag.DiscordEmployee
 * }
 * ```
 *
 * ## Mathematical operators
 * All [UserFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = UserFlags(UserFlag.DiscordEmployee)
 * val flags2 = flags + UserFlag.DiscordPartner
 * val otherFlags = flags - UserFlag.DiscordPartner
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = UserFlag.DiscordEmployee in obj.flags
 * val hasFlags = UserFlag(UserFlag.DiscordPartner, UserFlag.DiscordPartner) in obj.flags
 * ```
 *
 * ## Unknown flag
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [UserFlag.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = UserFlag.Unknown(1 shl 69) in obj.flags
 * ```
 * @see UserFlag
 * @see UserFlags.Builder
 * @property code numeric value of all [UserFlags]s
 */
@Serializable(with = UserFlags.Serializer::class)
public class UserFlags(
    public val code: Int = 0,
) {
    public val values: Set<UserFlag>
        get() = UserFlag.entries.filter { it in this }.toSet()

    public operator fun contains(flag: UserFlag): Boolean = this.code and flag.code == flag.code

    public operator fun contains(flags: UserFlags): Boolean = this.code and flags.code == flags.code

    public operator fun plus(flag: UserFlag): UserFlags = UserFlags(this.code or flag.code)

    public operator fun plus(flags: UserFlags): UserFlags = UserFlags(this.code or flags.code)

    public operator fun minus(flag: UserFlag): UserFlags = UserFlags(this.code and flag.code.inv())

    public operator fun minus(flags: UserFlags): UserFlags =
            UserFlags(this.code and flags.code.inv())

    override fun equals(other: Any?): Boolean = this === other ||
            (other is UserFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "UserFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun UserFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun UserFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun UserFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun UserFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): UserFlags = UserFlags(code)
    }

    internal object Serializer : KSerializer<UserFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.UserFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: UserFlags) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): UserFlags =
                UserFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun UserFlags(builder: UserFlags.Builder.() -> Unit): UserFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return UserFlags.Builder().apply(builder).flags()
}

public fun UserFlags(vararg flags: UserFlag): UserFlags = UserFlags { flags.forEach { +it } }

public fun UserFlags(vararg flags: UserFlags): UserFlags = UserFlags { flags.forEach { +it } }

public fun UserFlags(flags: Iterable<UserFlag>): UserFlags = UserFlags { flags.forEach { +it } }

@JvmName("UserFlags0")
public fun UserFlags(flags: Iterable<UserFlags>): UserFlags = UserFlags { flags.forEach { +it } }

public inline fun UserFlags.copy(block: UserFlags.Builder.() -> Unit): UserFlags {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return UserFlags.Builder(code).apply(block).flags()
}

/**
 * See [UserFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/user#user-object-user-flags).
 */
public sealed class UserFlag(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is UserFlag && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()

    final override fun toString(): String = "UserFlag.${this::class.simpleName}(code=$code)"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message = "UserFlag is no longer an enum class. Deprecated without replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message = "UserFlag is no longer an enum class. Deprecated without replacement.")
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
        BugHunterLevel2 -> 9
        VerifiedBot -> 10
        VerifiedBotDeveloper -> 11
        DiscordCertifiedModerator -> 12
        BotHttpInteractions -> 13
        ActiveDeveloper -> 14
        System -> 15
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
    public fun getDeclaringClass(): Class<UserFlag>? = UserFlag::class.java

    /**
     * An unknown [UserFlag].
     *
     * This is used as a fallback for [UserFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        code: Int,
    ) : UserFlag(code)

    /**
     * Discord Employee
     */
    public object DiscordEmployee : UserFlag(1)

    /**
     * Partnered Server Owner
     */
    public object DiscordPartner : UserFlag(2)

    /**
     * HypeSquad Events Member
     */
    public object HypeSquad : UserFlag(4)

    /**
     * Bug Hunter Level 1
     */
    public object BugHunterLevel1 : UserFlag(8)

    /**
     * House Bravery Member
     */
    public object HouseBravery : UserFlag(64)

    /**
     * House Brilliance Member
     */
    public object HouseBrilliance : UserFlag(128)

    /**
     * House Balance Member
     */
    public object HouseBalance : UserFlag(256)

    /**
     * Early Nitro Supporter
     */
    public object EarlySupporter : UserFlag(512)

    /**
     * User is a team
     */
    public object TeamUser : UserFlag(1_024)

    /**
     * Bug Hunter Level 2
     */
    public object BugHunterLevel2 : UserFlag(16_384)

    /**
     * Verified Bot
     */
    public object VerifiedBot : UserFlag(65_536)

    /**
     * Early Verified Bot Developer
     */
    public object VerifiedBotDeveloper : UserFlag(131_072)

    /**
     * Moderator Programs Alumni
     */
    public object DiscordCertifiedModerator : UserFlag(262_144)

    /**
     * Bot uses only HTTP interactions and is shown in the online member list
     */
    public object BotHttpInteractions : UserFlag(524_288)

    /**
     * User is an Active Developer
     */
    public object ActiveDeveloper : UserFlag(4_194_304)

    public object System : UserFlag(4_096)

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
                System,
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

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val System: UserFlag = System

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "UserFlag is no longer an enum class. Deprecated without replacement.")
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
            "BugHunterLevel2" -> BugHunterLevel2
            "VerifiedBot" -> VerifiedBot
            "VerifiedBotDeveloper" -> VerifiedBotDeveloper
            "DiscordCertifiedModerator" -> DiscordCertifiedModerator
            "BotHttpInteractions" -> BotHttpInteractions
            "ActiveDeveloper" -> ActiveDeveloper
            "System" -> System
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
    }
}
