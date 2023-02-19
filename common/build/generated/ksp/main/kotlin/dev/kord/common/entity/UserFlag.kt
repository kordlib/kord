// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordUnsafe
import dev.kord.common.entity.flags.BitFlags
import dev.kord.common.entity.flags.IntBitFlag
import dev.kord.common.entity.flags.IntBitFlags
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind

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
 * You can crate a modified copy of a [UserFlags] instance using the
 * [dev.kord.common.entity.flags.copy] method
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
 * val otherFlags = flags - UserFlag.HypeSquad
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = UserFlag.DiscordEmployee in obj.flags
 * val hasFlags = UserFlags(UserFlag.BugHunterLevel1, UserFlag.HouseBravery) in obj.flags
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
 * @property code numeric value of all [UserFlag]s
 */
@Serializable(with = UserFlags.Serializer::class)
public class UserFlags(
    code: Int = 0,
) : IntBitFlags<UserFlag, UserFlags, UserFlags.Builder>(UserFlag.entries, code) {
    protected override val name: String = "UserFlags"

    public override fun equals(other: Any?): Boolean = this === other ||
            (other is UserFlags && this.code == other.code)

    public override fun hashCode(): Int = code.hashCode()

    public override fun buildUpon(): Builder = Builder(code)

    protected override fun Implementation(flags: Int): UserFlags = UserFlags(flags)

    public class Builder(
        code: Int = 0,
    ) : IntBitFlags.Builder<UserFlag, UserFlags>(code) {
        public override fun flags(): UserFlags = UserFlags(code)
    }

    public class Serializer : BitFlags.Serializer<Int, UserFlag, UserFlags>(PrimitiveKind.INT,
            "code", Int.serializer()) {
        public override fun Implementation(code: Int): UserFlags = UserFlags(code)
    }

    public companion object : BitFlags.Companion<Int, UserFlag, UserFlags, Builder>() {
        public override fun Builder(): Builder = UserFlags.Builder()
    }
}

public sealed class UserFlag(
    /**
     * The raw code used by Discord.
     */
    public override val code: Int,
) : IntBitFlag {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is UserFlag && this.code == other.code)

    public final override fun hashCode(): Int = code.hashCode()

    public final override fun toString(): String = "UserFlag.${this::class.simpleName}(code=$code)"

    /**
     * An unknown [UserFlag].
     *
     * This is used as a fallback for [UserFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        code: Int,
    ) : UserFlag(code)

    public object DiscordEmployee : UserFlag(1)

    public object DiscordPartner : UserFlag(2)

    public object HypeSquad : UserFlag(4)

    public object BugHunterLevel1 : UserFlag(8)

    public object HouseBravery : UserFlag(64)

    public object HouseBrilliance : UserFlag(128)

    public object HouseBalance : UserFlag(256)

    public object EarlySupporter : UserFlag(512)

    public object TeamUser : UserFlag(1024)

    public object System : UserFlag(4096)

    public object BugHunterLevel2 : UserFlag(16384)

    public object VerifiedBot : UserFlag(65536)

    public object VerifiedBotDeveloper : UserFlag(131072)

    public object DiscordCertifiedModerator : UserFlag(262144)

    public object BotHttpInteractions : UserFlag(524288)

    public object ActiveDeveloper : UserFlag(4194304)

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
                System,
                BugHunterLevel2,
                VerifiedBot,
                VerifiedBotDeveloper,
                DiscordCertifiedModerator,
                BotHttpInteractions,
                ActiveDeveloper,
            )
        }

    }
}
