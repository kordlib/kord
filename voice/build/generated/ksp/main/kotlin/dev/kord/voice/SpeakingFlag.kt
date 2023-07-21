// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.voice

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

    override fun equals(other: Any?): Boolean = this === other ||
            (other is SpeakingFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "SpeakingFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun SpeakingFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun SpeakingFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun SpeakingFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun SpeakingFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): SpeakingFlags = SpeakingFlags(code)
    }

    internal object Serializer : KSerializer<SpeakingFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.voice.SpeakingFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: SpeakingFlags) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): SpeakingFlags =
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
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SpeakingFlag && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()

    final override fun toString(): String = "SpeakingFlag.${this::class.simpleName}(code=$code)"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SpeakingFlag is no longer an enum class. Deprecated without replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SpeakingFlag is no longer an enum class. Deprecated without replacement.")
    public fun ordinal(): Int = when (this) {
        Microphone -> 0
        Soundshare -> 1
        Priority -> 2
        is Unknown -> Int.MAX_VALUE
    }

    /**
     * @suppress
     */
    @Deprecated(
        message = "SpeakingFlag is no longer an enum class.",
        replaceWith = ReplaceWith(expression = "SpeakingFlag::class.java", imports =
                    arrayOf("dev.kord.voice.SpeakingFlag")),
    )
    public fun getDeclaringClass(): Class<SpeakingFlag>? = SpeakingFlag::class.java

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


        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Microphone: SpeakingFlag = Microphone

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Soundshare: SpeakingFlag = Soundshare

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Priority: SpeakingFlag = Priority

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "SpeakingFlag is no longer an enum class. Deprecated without replacement.")
        @JvmStatic
        public open fun valueOf(name: String): SpeakingFlag = when (name) {
            "Microphone" -> Microphone
            "Soundshare" -> Soundshare
            "Priority" -> Priority
            else -> throw IllegalArgumentException(name)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT"))
        @Deprecated(
            message = "SpeakingFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "SpeakingFlag.entries.toTypedArray()", imports =
                        arrayOf("dev.kord.voice.SpeakingFlag")),
        )
        @JvmStatic
        public open fun values(): Array<SpeakingFlag> = entries.toTypedArray()
    }
}
