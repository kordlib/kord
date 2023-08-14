// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

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
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(GuildMemberFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

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

    public inline fun copy(block: Builder.() -> Unit): GuildMemberFlags {
        contract { callsInPlace(block, EXACTLY_ONCE) }
        return Builder(code).apply(block).build()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildMemberFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "GuildMemberFlags(values=$values)"

    /**
     * @suppress
     */
    @Deprecated(
        message = "GuildMemberFlags is no longer a data class.",
        replaceWith = ReplaceWith(expression = "this.code", imports = arrayOf()),
    )
    public operator fun component1(): Int = code

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "GuildMemberFlags is no longer a data class. Deprecated without a replacement.")
    public fun copy(code: Int = this.code): GuildMemberFlags = GuildMemberFlags(code)

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun GuildMemberFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun GuildMemberFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun GuildMemberFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun GuildMemberFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun build(): GuildMemberFlags = GuildMemberFlags(code)

        /**
         * @suppress
         */
        @Deprecated(
            message = "Renamed to 'build'",
            replaceWith = ReplaceWith(expression = "this.build()", imports = arrayOf()),
        )
        public fun flags(): GuildMemberFlags = build()
    }

    internal object Serializer : KSerializer<GuildMemberFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildMemberFlags",
                PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: GuildMemberFlags) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): GuildMemberFlags =
                GuildMemberFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun GuildMemberFlags(builder: GuildMemberFlags.Builder.() -> Unit): GuildMemberFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return GuildMemberFlags.Builder().apply(builder).build()
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

/**
 * See [GuildMemberFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-member-object-guild-member-flags).
 */
public sealed class GuildMemberFlag(
    /**
     * The position of the bit that is set in this [GuildMemberFlag]. This is always in 0..30.
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

    public operator fun plus(flag: GuildMemberFlag): GuildMemberFlags =
            GuildMemberFlags(this.code or flag.code)

    public operator fun plus(flags: GuildMemberFlags): GuildMemberFlags =
            GuildMemberFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildMemberFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown)
            "GuildMemberFlag.Unknown(shift=$shift)" else "GuildMemberFlag.${this::class.simpleName}"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "GuildMemberFlag is no longer an enum class. Deprecated without a replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "GuildMemberFlag is no longer an enum class. Deprecated without a replacement.")
    public fun ordinal(): Int = when (this) {
        DidRejoin -> 0
        CompletedOnboarding -> 1
        BypassesVerification -> 2
        StartedOnboarding -> 3
        is Unknown -> Int.MAX_VALUE
    }

    /**
     * @suppress
     */
    @Deprecated(
        message = "GuildMemberFlag is no longer an enum class.",
        replaceWith = ReplaceWith(expression = "GuildMemberFlag::class.java", imports =
                    arrayOf("dev.kord.common.entity.GuildMemberFlag")),
    )
    public fun getDeclaringClass(): Class<GuildMemberFlag> = GuildMemberFlag::class.java

    /**
     * An unknown [GuildMemberFlag].
     *
     * This is used as a fallback for [GuildMemberFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : GuildMemberFlag(shift)

    /**
     * Member has left and rejoined the guild
     */
    public object DidRejoin : GuildMemberFlag(0)

    /**
     * Member has completed onboarding
     */
    public object CompletedOnboarding : GuildMemberFlag(1)

    /**
     * Member is exempt from guild verification requirements
     */
    public object BypassesVerification : GuildMemberFlag(2)

    /**
     * Member has started onboarding
     */
    public object StartedOnboarding : GuildMemberFlag(3)

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


        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val DidRejoin: GuildMemberFlag = DidRejoin

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val CompletedOnboarding: GuildMemberFlag = CompletedOnboarding

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val BypassesVerification: GuildMemberFlag = BypassesVerification

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val StartedOnboarding: GuildMemberFlag = StartedOnboarding

        /**
         * Returns an instance of [GuildMemberFlag] with [GuildMemberFlag.shift] equal to the
         * specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): GuildMemberFlag = when (shift) {
            0 -> DidRejoin
            1 -> CompletedOnboarding
            2 -> BypassesVerification
            3 -> StartedOnboarding
            else -> Unknown(shift)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "GuildMemberFlag is no longer an enum class. Deprecated without a replacement.")
        @JvmStatic
        public open fun valueOf(name: String): GuildMemberFlag = when (name) {
            "DidRejoin" -> DidRejoin
            "CompletedOnboarding" -> CompletedOnboarding
            "BypassesVerification" -> BypassesVerification
            "StartedOnboarding" -> StartedOnboarding
            else -> throw IllegalArgumentException(name)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT"))
        @Deprecated(
            message = "GuildMemberFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "GuildMemberFlag.entries.toTypedArray()", imports
                        = arrayOf("dev.kord.common.entity.GuildMemberFlag")),
        )
        @JvmStatic
        public open fun values(): Array<GuildMemberFlag> = entries.toTypedArray()

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "UPPER_BOUND_VIOLATED"))
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "GuildMemberFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "GuildMemberFlag.entries", imports =
                        arrayOf("dev.kord.common.entity.GuildMemberFlag")),
        )
        @JvmStatic
        public open fun getEntries(): EnumEntries<GuildMemberFlag> = EnumEntriesList

        @Suppress(names = arrayOf("SEALED_INHERITOR_IN_DIFFERENT_MODULE",
                        "SEALED_INHERITOR_IN_DIFFERENT_PACKAGE", "UPPER_BOUND_VIOLATED"))
        private object EnumEntriesList : EnumEntries<GuildMemberFlag>, List<GuildMemberFlag> by
                entries {
            override fun equals(other: Any?): Boolean = entries == other

            override fun hashCode(): Int = entries.hashCode()

            override fun toString(): String = entries.toString()
        }
    }
}
