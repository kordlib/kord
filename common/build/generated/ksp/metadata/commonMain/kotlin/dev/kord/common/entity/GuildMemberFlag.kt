// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection",
                "RedundantUnitReturnType"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordUnsafe
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Convenience container of multiple [GuildMemberFlags][GuildMemberFlag] which can be combined into
 * one.
 *
 * ## Creating a collection of message flags
 * You can create an [GuildMemberFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = GuildMemberFlags(GuildMemberFlag.DidRejoin, GuildMemberFlag.CompletedOnboarding)
 * // From an iterable
 * val flags2 = GuildMemberFlags(listOf(GuildMemberFlag.DidRejoin,
 * GuildMemberFlag.CompletedOnboarding))
 * // Using a builder
 * val flags3 = GuildMemberFlags {
 *  +GuildMemberFlag.DidRejoin
 *  -GuildMemberFlag.CompletedOnboarding
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [GuildMemberFlags] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +GuildMemberFlag.DidRejoin
 * }
 * ```
 *
 * ## Mathematical operators
 * All [GuildMemberFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = GuildMemberFlags(GuildMemberFlag.DidRejoin)
 * val flags2 = flags + GuildMemberFlag.CompletedOnboarding
 * val otherFlags = flags - GuildMemberFlag.CompletedOnboarding
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = GuildMemberFlag.DidRejoin in obj.flags
 * val hasFlags = GuildMemberFlag(GuildMemberFlag.CompletedOnboarding,
 * GuildMemberFlag.CompletedOnboarding) in obj.flags
 * ```
 *
 * ## Unknown flag
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [GuildMemberFlag.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = GuildMemberFlag.Unknown(1 shl 69) in obj.flags
 * ```
 * @see GuildMemberFlag
 * @see GuildMemberFlags.Builder
 * @property code numeric value of all [GuildMemberFlags]s
 */
@Serializable(with = GuildMemberFlags.Serializer::class)
public class GuildMemberFlags(
    public val code: Int = 0,
) {
    public val values: Set<GuildMemberFlag>
        get() = GuildMemberFlag.entries.filter { it in this }.toSet()

    public operator fun contains(flag: GuildMemberFlag): Boolean =
            this.code and flag.code == flag.code

    public operator fun contains(flags: GuildMemberFlags): Boolean =
            this.code and flags.code == flags.code

    public operator fun plus(flag: GuildMemberFlag): GuildMemberFlags =
            GuildMemberFlags(this.code or flag.code)

    public operator fun plus(flags: GuildMemberFlags): GuildMemberFlags =
            GuildMemberFlags(this.code or flags.code)

    public operator fun minus(flag: GuildMemberFlag): GuildMemberFlags =
            GuildMemberFlags(this.code and flag.code.inv())

    public operator fun minus(flags: GuildMemberFlags): GuildMemberFlags =
            GuildMemberFlags(this.code and flags.code.inv())

    public override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildMemberFlags && this.code == other.code)

    public override fun hashCode(): Int = code.hashCode()

    public override fun toString(): String = "GuildMemberFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun GuildMemberFlag.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun GuildMemberFlags.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun GuildMemberFlag.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun GuildMemberFlags.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): GuildMemberFlags = GuildMemberFlags(code)
    }

    internal object Serializer : KSerializer<GuildMemberFlags> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildMemberFlags",
                PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        public override fun serialize(encoder: Encoder, `value`: GuildMemberFlags) =
                encoder.encodeSerializableValue(delegate, value.code)

        public override fun deserialize(decoder: Decoder) =
                GuildMemberFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun GuildMemberFlags(builder: GuildMemberFlags.Builder.() -> Unit): GuildMemberFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return GuildMemberFlags.Builder().apply(builder).flags()
}

public fun GuildMemberFlags(vararg flags: GuildMemberFlag): GuildMemberFlags = GuildMemberFlags {
        flags.forEach { +it } }

public fun GuildMemberFlags(vararg flags: GuildMemberFlags): GuildMemberFlags = GuildMemberFlags {
        flags.forEach { +it } }

public fun GuildMemberFlags(flags: Iterable<GuildMemberFlag>): GuildMemberFlags = GuildMemberFlags {
        flags.forEach { +it } }

@JvmName("GuildMemberFlags0")
public fun GuildMemberFlags(flags: Iterable<GuildMemberFlags>): GuildMemberFlags =
        GuildMemberFlags { flags.forEach { +it } }

public inline fun GuildMemberFlags.copy(block: GuildMemberFlags.Builder.() -> Unit):
        GuildMemberFlags {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return GuildMemberFlags.Builder(code).apply(block).flags()
}

/**
 * See [GuildMemberFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-member-object-guild-member-flags).
 */
public sealed class GuildMemberFlag(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildMemberFlag && this.code == other.code)

    public final override fun hashCode(): Int = code.hashCode()

    public final override fun toString(): String =
            "GuildMemberFlag.${this::class.simpleName}(code=$code)"

    /**
     * An unknown [GuildMemberFlag].
     *
     * This is used as a fallback for [GuildMemberFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        code: Int,
    ) : GuildMemberFlag(code)

    /**
     * Member has left and rejoined the guild
     */
    public object DidRejoin : GuildMemberFlag(1)

    /**
     * Member has completed onboarding
     */
    public object CompletedOnboarding : GuildMemberFlag(2)

    /**
     * Member is exempt from guild verification requirements
     */
    public object BypassesVerification : GuildMemberFlag(4)

    /**
     * Member has started onboarding
     */
    public object StartedOnboarding : GuildMemberFlag(8)

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

    }
}
