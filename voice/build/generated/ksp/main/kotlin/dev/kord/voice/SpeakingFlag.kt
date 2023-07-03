// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection",
                "RedundantUnitReturnType"))

package dev.kord.voice

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
 * Convenience container of multiple [SpeakingFlags][SpeakingFlag] which can be combined into one.
 *
 * ## Creating a collection of message flags
 * You can create an [SpeakingFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = SpeakingFlags(SpeakingFlag.Microphone, SpeakingFlag.Soundshare)
 * // From an iterable
 * val flags2 = SpeakingFlags(listOf(SpeakingFlag.Microphone, SpeakingFlag.Soundshare))
 * // Using a builder
 * val flags3 = SpeakingFlags {
 *  +SpeakingFlag.Microphone
 *  -SpeakingFlag.Soundshare
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [SpeakingFlags] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +SpeakingFlag.Microphone
 * }
 * ```
 *
 * ## Mathematical operators
 * All [SpeakingFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = SpeakingFlags(SpeakingFlag.Microphone)
 * val flags2 = flags + SpeakingFlag.Soundshare
 * val otherFlags = flags - SpeakingFlag.Soundshare
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = SpeakingFlag.Microphone in obj.flags
 * val hasFlags = SpeakingFlag(SpeakingFlag.Soundshare, SpeakingFlag.Soundshare) in obj.flags
 * ```
 *
 * ## Unknown flag
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [SpeakingFlag.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = SpeakingFlag.Unknown(1 shl 69) in obj.flags
 * ```
 * @see SpeakingFlag
 * @see SpeakingFlags.Builder
 * @property code numeric value of all [SpeakingFlags]s
 */
@Serializable(with = SpeakingFlags.Serializer::class)
public class SpeakingFlags(
    public val code: Int = 0,
) {
    public val values: Set<SpeakingFlag>
        get() = SpeakingFlag.entries.filter { it in this }.toSet()

    public operator fun contains(flag: SpeakingFlag): Boolean = this.code and flag.code == flag.code

    public operator fun contains(flags: SpeakingFlags): Boolean =
            this.code and flags.code == flags.code

    public operator fun plus(flag: SpeakingFlag): SpeakingFlags =
            SpeakingFlags(this.code or flag.code)

    public operator fun plus(flags: SpeakingFlags): SpeakingFlags =
            SpeakingFlags(this.code or flags.code)

    public operator fun minus(flag: SpeakingFlag): SpeakingFlags =
            SpeakingFlags(this.code and flag.code.inv())

    public operator fun minus(flags: SpeakingFlags): SpeakingFlags =
            SpeakingFlags(this.code and flags.code.inv())

    public override fun equals(other: Any?): Boolean = this === other ||
            (other is SpeakingFlags && this.code == other.code)

    public override fun hashCode(): Int = code.hashCode()

    public override fun toString(): String = "SpeakingFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun SpeakingFlag.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun SpeakingFlags.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun SpeakingFlag.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun SpeakingFlags.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): SpeakingFlags = SpeakingFlags(code)
    }

    internal object Serializer : KSerializer<SpeakingFlags> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.voice.SpeakingFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        public override fun serialize(encoder: Encoder, `value`: SpeakingFlags) =
                encoder.encodeSerializableValue(delegate, value.code)

        public override fun deserialize(decoder: Decoder) =
                SpeakingFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun SpeakingFlags(builder: SpeakingFlags.Builder.() -> Unit): SpeakingFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return SpeakingFlags.Builder().apply(builder).flags()
}

public fun SpeakingFlags(vararg flags: SpeakingFlag): SpeakingFlags = SpeakingFlags {
        flags.forEach { +it } }

public fun SpeakingFlags(vararg flags: SpeakingFlags): SpeakingFlags = SpeakingFlags {
        flags.forEach { +it } }

public fun SpeakingFlags(flags: Iterable<SpeakingFlag>): SpeakingFlags = SpeakingFlags {
        flags.forEach { +it } }

@JvmName("SpeakingFlags0")
public fun SpeakingFlags(flags: Iterable<SpeakingFlags>): SpeakingFlags = SpeakingFlags {
        flags.forEach { +it } }

public inline fun SpeakingFlags.copy(block: SpeakingFlags.Builder.() -> Unit): SpeakingFlags {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return SpeakingFlags.Builder(code).apply(block).flags()
}

/**
 * See [SpeakingFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/voice-connections#speaking).
 */
public sealed class SpeakingFlag(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is SpeakingFlag && this.code == other.code)

    public final override fun hashCode(): Int = code.hashCode()

    public final override fun toString(): String =
            "SpeakingFlag.${this::class.simpleName}(code=$code)"

    /**
     * An unknown [SpeakingFlag].
     *
     * This is used as a fallback for [SpeakingFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        code: Int,
    ) : SpeakingFlag(code)

    public object Microphone : SpeakingFlag(1)

    public object Soundshare : SpeakingFlag(2)

    public object Priority : SpeakingFlag(4)

    public companion object {
        /**
         * A [List] of all known [SpeakingFlag]s.
         */
        public val entries: List<SpeakingFlag> by lazy(mode = PUBLICATION) {
            listOf(
                Microphone,
                Soundshare,
                Priority,
            )
        }

    }
}
