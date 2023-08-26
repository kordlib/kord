// TODO uncomment start and remove rest of the file after deprecation cycle

/*
@file:Generate(
    INT_FLAGS, name = "SpeakingFlag", valueName = "code",
    docUrl = "https://discord.com/developers/docs/topics/voice-connections#speaking",
    entries = [
        Entry("Microphone", shift = 0, kDoc = "Normal transmission of voice audio."),
        Entry("Soundshare", shift = 1, kDoc = "Transmission of context audio for video, no speaking indicator."),
        Entry("Priority", shift = 2, kDoc = "Priority speaker, lowering audio of other speakers."),
    ]
)

package dev.kord.voice

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_FLAGS
import dev.kord.ksp.Generate.Entry
*/

@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
    "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.voice

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.enums.EnumEntries
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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

/**
 * A collection of multiple [SpeakingFlag]s.
 *
 * ## Creating an instance of [SpeakingFlags]
 *
 * You can create an instance of [SpeakingFlags] using the following methods:
 * ```kotlin
 * // from individual SpeakingFlags
 * val speakingFlags1 = SpeakingFlags(SpeakingFlag.Microphone, SpeakingFlag.Soundshare)
 *
 * // from an Iterable
 * val iterable: Iterable<SpeakingFlag> = TODO()
 * val speakingFlags2 = SpeakingFlags(iterable)
 *
 * // using a builder
 * val speakingFlags3 = SpeakingFlags {
 *     +speakingFlags2
 *     +SpeakingFlag.Microphone
 *     -SpeakingFlag.Soundshare
 * }
 * ```
 *
 * ## Modifying an existing instance of [SpeakingFlags]
 *
 * You can create a modified copy of an existing instance of [SpeakingFlags] using the [copy]
 * method:
 * ```kotlin
 * speakingFlags.copy {
 *     +SpeakingFlag.Microphone
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [SpeakingFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val speakingFlags1 = speakingFlags + SpeakingFlag.Microphone
 * val speakingFlags2 = speakingFlags - SpeakingFlag.Soundshare
 * val speakingFlags3 = speakingFlags1 + speakingFlags2
 * ```
 *
 * ## Checking for [SpeakingFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [SpeakingFlags] contains
 * specific [SpeakingFlag]s:
 * ```kotlin
 * val hasSpeakingFlag = SpeakingFlag.Microphone in speakingFlags
 * val hasSpeakingFlags = SpeakingFlags(SpeakingFlag.Microphone,
 * SpeakingFlag.Soundshare) in speakingFlags
 * ```
 *
 * ## Unknown [SpeakingFlag]s
 *
 * Whenever [SpeakingFlag]s haven't been added to Kord yet, they will be deserialized as instances
 * of [SpeakingFlag.Unknown].
 *
 * You can also use [SpeakingFlag.fromShift] to check for [unknown][SpeakingFlag.Unknown]
 * [SpeakingFlag]s.
 * ```kotlin
 * val hasUnknownSpeakingFlag = SpeakingFlag.fromShift(23) in speakingFlags
 * ```
 *
 * @see SpeakingFlag
 * @see SpeakingFlags.Builder
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

    /**
     * Returns a copy of this instance of [SpeakingFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): SpeakingFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }

    override fun equals(other: Any?): Boolean = this === other ||
        (other is SpeakingFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "SpeakingFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [SpeakingFlag].
         */
        public operator fun SpeakingFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Sets all bits in the [Builder] that are set in this [SpeakingFlags].
         */
        public operator fun SpeakingFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SpeakingFlag].
         */
        public operator fun SpeakingFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SpeakingFlags].
         */
        public operator fun SpeakingFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Returns an instance of [SpeakingFlags] that has all bits set that are currently set in
         * this [Builder].
         */
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

@Suppress("FunctionName")
@Deprecated("Binary compatibility, keep for some releases.", level = DeprecationLevel.HIDDEN)
public fun SpeakingFlagsWithIterable(flags: Iterable<SpeakingFlags>): SpeakingFlags = SpeakingFlags(flags)
