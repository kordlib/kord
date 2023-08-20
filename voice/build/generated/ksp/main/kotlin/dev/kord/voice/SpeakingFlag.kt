// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.voice

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
public class SpeakingFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    /**
     * A [Set] of all [SpeakingFlag]s contained in this instance of [SpeakingFlags].
     */
    public val values: Set<SpeakingFlag>
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(SpeakingFlag.fromShift(shift))
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
    public val flags: List<SpeakingFlag>
        get() = values.toList()

    /**
     * Checks if this instance of [SpeakingFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: SpeakingFlag): Boolean = this.code and flag.code == flag.code

    /**
     * Checks if this instance of [SpeakingFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: SpeakingFlags): Boolean =
            this.code and flags.code == flags.code

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: SpeakingFlag): SpeakingFlags =
            SpeakingFlags(this.code or flag.code)

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: SpeakingFlags): SpeakingFlags =
            SpeakingFlags(this.code or flags.code)

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flag].
     */
    public operator fun minus(flag: SpeakingFlag): SpeakingFlags =
            SpeakingFlags(this.code and flag.code.inv())

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flags].
     */
    public operator fun minus(flags: SpeakingFlags): SpeakingFlags =
            SpeakingFlags(this.code and flags.code.inv())

    public inline fun copy(block: Builder.() -> Unit): SpeakingFlags {
        contract { callsInPlace(block, EXACTLY_ONCE) }
        return Builder(code).apply(block).build()
    }

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

        public fun build(): SpeakingFlags = SpeakingFlags(code)

        /**
         * @suppress
         */
        @Deprecated(
            message = "Renamed to 'build'",
            replaceWith = ReplaceWith(expression = "this.build()", imports = arrayOf()),
        )
        public fun flags(): SpeakingFlags = build()
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

/**
 * Returns an instance of [SpeakingFlags] built with [SpeakingFlags.Builder].
 */
public inline fun SpeakingFlags(builder: SpeakingFlags.Builder.() -> Unit = {}): SpeakingFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return SpeakingFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [SpeakingFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SpeakingFlags(vararg flags: SpeakingFlag): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SpeakingFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SpeakingFlags(vararg flags: SpeakingFlags): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SpeakingFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SpeakingFlags(flags: Iterable<SpeakingFlag>): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SpeakingFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("SpeakingFlags0")
public fun SpeakingFlags(flags: Iterable<SpeakingFlags>): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}

/**
 * See [SpeakingFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/voice-connections#speaking).
 */
public sealed class SpeakingFlag(
    /**
     * The position of the bit that is set in this [SpeakingFlag]. This is always in 0..30.
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
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: SpeakingFlag): SpeakingFlags =
            SpeakingFlags(this.code or flag.code)

    /**
     * Returns an instance of [SpeakingFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: SpeakingFlags): SpeakingFlags =
            SpeakingFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SpeakingFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown)
            "SpeakingFlag.Unknown(shift=$shift)" else "SpeakingFlag.${this::class.simpleName}"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SpeakingFlag is no longer an enum class. Deprecated without a replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SpeakingFlag is no longer an enum class. Deprecated without a replacement.")
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
    public fun getDeclaringClass(): Class<SpeakingFlag> = SpeakingFlag::class.java

    /**
     * An unknown [SpeakingFlag].
     *
     * This is used as a fallback for [SpeakingFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : SpeakingFlag(shift)

    /**
     * Normal transmission of voice audio.
     */
    public object Microphone : SpeakingFlag(0)

    /**
     * Transmission of context audio for video, no speaking indicator.
     */
    public object Soundshare : SpeakingFlag(1)

    /**
     * Priority speaker, lowering audio of other speakers.
     */
    public object Priority : SpeakingFlag(2)

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
         * Returns an instance of [SpeakingFlag] with [SpeakingFlag.shift] equal to the specified
         * [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): SpeakingFlag = when (shift) {
            0 -> Microphone
            1 -> Soundshare
            2 -> Priority
            else -> Unknown(shift)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "SpeakingFlag is no longer an enum class. Deprecated without a replacement.")
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

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "UPPER_BOUND_VIOLATED"))
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "SpeakingFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "SpeakingFlag.entries", imports =
                        arrayOf("dev.kord.voice.SpeakingFlag")),
        )
        @JvmStatic
        public open fun getEntries(): EnumEntries<SpeakingFlag> = EnumEntriesList

        @Suppress(names = arrayOf("SEALED_INHERITOR_IN_DIFFERENT_MODULE",
                        "SEALED_INHERITOR_IN_DIFFERENT_PACKAGE", "UPPER_BOUND_VIOLATED"))
        private object EnumEntriesList : EnumEntries<SpeakingFlag>, List<SpeakingFlag> by entries {
            override fun equals(other: Any?): Boolean = entries == other

            override fun hashCode(): Int = entries.hashCode()

            override fun toString(): String = entries.toString()
        }
    }
}
