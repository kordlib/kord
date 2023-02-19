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
 * Convenience container of multiple [ActivityFlags][ActivityFlag] which can be combined into one.
 *
 * ## Creating a collection of message flags
 * You can create an [ActivityFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = ActivityFlags(ActivityFlag.Instance, ActivityFlag.Join)
 * // From an iterable
 * val flags2 = ActivityFlags(listOf(ActivityFlag.Instance, ActivityFlag.Join))
 * // Using a builder
 * val flags3 = ActivityFlags {
 *  +ActivityFlag.Instance
 *  -ActivityFlag.Join
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [ActivityFlags] instance using the
 * [dev.kord.common.entity.flags.copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +ActivityFlag.Instance
 * }
 * ```
 *
 * ## Mathematical operators
 * All [ActivityFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = ActivityFlags(ActivityFlag.Instance)
 * val flags2 = flags + ActivityFlag.Join
 * val otherFlags = flags - ActivityFlag.Spectate
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = ActivityFlag.Instance in obj.flags
 * val hasFlags = ActivityFlags(ActivityFlag.JoinRequest, ActivityFlag.Sync) in obj.flags
 * ```
 *
 * ## Unknown flag
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [ActivityFlag.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = ActivityFlag.Unknown(1 shl 69) in obj.flags
 * ```
 * @see ActivityFlag
 * @see ActivityFlags.Builder
 * @property code numeric value of all [ActivityFlag]s
 */
@Serializable(with = ActivityFlags.Serializer::class)
public class ActivityFlags(
    code: Int = 0,
) : IntBitFlags<ActivityFlag, ActivityFlags, ActivityFlags.Builder>(ActivityFlag.entries, code) {
    protected override val name: String = "ActivityFlags"

    public override fun equals(other: Any?): Boolean = this === other ||
            (other is ActivityFlags && this.code == other.code)

    public override fun hashCode(): Int = code.hashCode()

    public override fun buildUpon(): Builder = Builder(code)

    protected override fun Implementation(flags: Int): ActivityFlags = ActivityFlags(flags)

    public class Builder(
        code: Int = 0,
    ) : IntBitFlags.Builder<ActivityFlag, ActivityFlags>(code) {
        public override fun flags(): ActivityFlags = ActivityFlags(code)
    }

    public class Serializer :
            BitFlags.Serializer<Int, ActivityFlag, ActivityFlags>(PrimitiveKind.INT, "code",
            Int.serializer()) {
        public override fun Implementation(code: Int): ActivityFlags = ActivityFlags(code)
    }

    public companion object : BitFlags.Companion<Int, ActivityFlag, ActivityFlags, Builder>() {
        public override fun Builder(): Builder = ActivityFlags.Builder()
    }
}

/**
 * See [ActivityFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-flags).
 */
public sealed class ActivityFlag(
    /**
     * The raw code used by Discord.
     */
    public override val code: Int,
) : IntBitFlag {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is ActivityFlag && this.code == other.code)

    public final override fun hashCode(): Int = code.hashCode()

    public final override fun toString(): String =
            "ActivityFlag.${this::class.simpleName}(code=$code)"

    /**
     * An unknown [ActivityFlag].
     *
     * This is used as a fallback for [ActivityFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        code: Int,
    ) : ActivityFlag(code)

    public object Instance : ActivityFlag(1)

    public object Join : ActivityFlag(2)

    public object Spectate : ActivityFlag(4)

    public object JoinRequest : ActivityFlag(8)

    public object Sync : ActivityFlag(16)

    public object Play : ActivityFlag(32)

    public object PartyPrivacyFriends : ActivityFlag(64)

    public object PartyPrivacVoiceChannel : ActivityFlag(128)

    public object Embed : ActivityFlag(256)

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
                PartyPrivacVoiceChannel,
                Embed,
            )
        }

    }
}
