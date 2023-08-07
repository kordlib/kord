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
 * Convenience container of multiple [ChannelFlags][ChannelFlag] which can be combined into one.
 *
 * ## Creating a collection of message flags
 * You can create an [ChannelFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = ChannelFlags(ChannelFlag.Pinned, ChannelFlag.RequireTag)
 * // From an iterable
 * val flags2 = ChannelFlags(listOf(ChannelFlag.Pinned, ChannelFlag.RequireTag))
 * // Using a builder
 * val flags3 = ChannelFlags {
 *  +ChannelFlag.Pinned
 *  -ChannelFlag.RequireTag
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [ChannelFlags] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +ChannelFlag.Pinned
 * }
 * ```
 *
 * ## Mathematical operators
 * All [ChannelFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = ChannelFlags(ChannelFlag.Pinned)
 * val flags2 = flags + ChannelFlag.RequireTag
 * val otherFlags = flags - ChannelFlag.RequireTag
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = ChannelFlag.Pinned in obj.flags
 * val hasFlags = ChannelFlag(ChannelFlag.RequireTag, ChannelFlag.RequireTag) in obj.flags
 * ```
 *
 * ## Unknown flag
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [ChannelFlag.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = ChannelFlag.Unknown(1 shl 69) in obj.flags
 * ```
 * @see ChannelFlag
 * @see ChannelFlags.Builder
 * @property code numeric value of all [ChannelFlags]s
 */
@Serializable(with = ChannelFlags.Serializer::class)
public class ChannelFlags(
    public val code: Int = 0,
) {
    public val values: Set<ChannelFlag>
        get() = ChannelFlag.entries.filter { it in this }.toSet()

    @Deprecated(
        message = "Renamed to 'values'.",
        replaceWith = ReplaceWith(expression = "this.values", imports = arrayOf()),
    )
    public val flags: List<ChannelFlag>
        get() = values.toList()

    public operator fun contains(flag: ChannelFlag): Boolean = this.code and flag.code == flag.code

    public operator fun contains(flags: ChannelFlags): Boolean =
            this.code and flags.code == flags.code

    public operator fun plus(flag: ChannelFlag): ChannelFlags = ChannelFlags(this.code or flag.code)

    public operator fun plus(flags: ChannelFlags): ChannelFlags =
            ChannelFlags(this.code or flags.code)

    public operator fun minus(flag: ChannelFlag): ChannelFlags =
            ChannelFlags(this.code and flag.code.inv())

    public operator fun minus(flags: ChannelFlags): ChannelFlags =
            ChannelFlags(this.code and flags.code.inv())

    public inline fun copy(block: Builder.() -> Unit): ChannelFlags {
        contract { callsInPlace(block, EXACTLY_ONCE) }
        return Builder(code).apply(block).flags()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is ChannelFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "ChannelFlags(values=$values)"

    /**
     * @suppress
     */
    @Deprecated(
        message = "ChannelFlags is no longer a data class.",
        replaceWith = ReplaceWith(expression = "this.code", imports = arrayOf()),
    )
    public operator fun component1(): Int = code

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ChannelFlags is no longer a data class. Deprecated without a replacement.")
    public fun copy(code: Int = this.code): ChannelFlags = ChannelFlags(code)

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun ChannelFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ChannelFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ChannelFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun ChannelFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): ChannelFlags = ChannelFlags(code)
    }

    internal object Serializer : KSerializer<ChannelFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ChannelFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: ChannelFlags) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): ChannelFlags =
                ChannelFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun ChannelFlags(builder: ChannelFlags.Builder.() -> Unit): ChannelFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return ChannelFlags.Builder().apply(builder).flags()
}

public fun ChannelFlags(vararg flags: ChannelFlag): ChannelFlags = ChannelFlags {
        flags.forEach { +it } }

public fun ChannelFlags(vararg flags: ChannelFlags): ChannelFlags = ChannelFlags {
        flags.forEach { +it } }

public fun ChannelFlags(flags: Iterable<ChannelFlag>): ChannelFlags = ChannelFlags {
        flags.forEach { +it } }

@JvmName("ChannelFlags0")
public fun ChannelFlags(flags: Iterable<ChannelFlags>): ChannelFlags = ChannelFlags {
        flags.forEach { +it } }

/**
 * See [ChannelFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#channel-object-channel-flags).
 */
public sealed class ChannelFlag(
    shift: Int,
) {
    /**
     * The raw code used by Discord.
     */
    public val code: Int = 1 shl shift

    public operator fun plus(flag: ChannelFlag): ChannelFlags = ChannelFlags(this.code or flag.code)

    public operator fun plus(flags: ChannelFlags): ChannelFlags =
            ChannelFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ChannelFlag && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()

    final override fun toString(): String = "ChannelFlag.${this::class.simpleName}(code=$code)"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ChannelFlag is no longer an enum class. Deprecated without a replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ChannelFlag is no longer an enum class. Deprecated without a replacement.")
    public fun ordinal(): Int = when (this) {
        Pinned -> 0
        RequireTag -> 1
        is Unknown -> Int.MAX_VALUE
    }

    /**
     * @suppress
     */
    @Deprecated(
        message = "ChannelFlag is no longer an enum class.",
        replaceWith = ReplaceWith(expression = "ChannelFlag::class.java", imports =
                    arrayOf("dev.kord.common.entity.ChannelFlag")),
    )
    public fun getDeclaringClass(): Class<ChannelFlag>? = ChannelFlag::class.java

    /**
     * An unknown [ChannelFlag].
     *
     * This is used as a fallback for [ChannelFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        shift: Int,
    ) : ChannelFlag(shift)

    /**
     * This thread is pinned to the top of its parent [GuildForum][ChannelType.GuildForum] channel.
     */
    public object Pinned : ChannelFlag(1)

    /**
     * Whether a tag is required to be specified when creating a thread in a
     * [GuildForum][ChannelType.GuildForum] channel.
     */
    public object RequireTag : ChannelFlag(4)

    public companion object {
        /**
         * A [List] of all known [ChannelFlag]s.
         */
        public val entries: List<ChannelFlag> by lazy(mode = PUBLICATION) {
            listOf(
                Pinned,
                RequireTag,
            )
        }


        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Pinned: ChannelFlag = Pinned

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val RequireTag: ChannelFlag = RequireTag

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "ChannelFlag is no longer an enum class. Deprecated without a replacement.")
        @JvmStatic
        public open fun valueOf(name: String): ChannelFlag = when (name) {
            "Pinned" -> Pinned
            "RequireTag" -> RequireTag
            else -> throw IllegalArgumentException(name)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT"))
        @Deprecated(
            message = "ChannelFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "ChannelFlag.entries.toTypedArray()", imports =
                        arrayOf("dev.kord.common.entity.ChannelFlag")),
        )
        @JvmStatic
        public open fun values(): Array<ChannelFlag> = entries.toTypedArray()

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "UPPER_BOUND_VIOLATED"))
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "ChannelFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "ChannelFlag.entries", imports =
                        arrayOf("dev.kord.common.entity.ChannelFlag")),
        )
        @JvmStatic
        public open fun getEntries(): EnumEntries<ChannelFlag> = EnumEntriesList

        @Suppress(names = arrayOf("SEALED_INHERITOR_IN_DIFFERENT_MODULE",
                        "SEALED_INHERITOR_IN_DIFFERENT_PACKAGE", "UPPER_BOUND_VIOLATED"))
        private object EnumEntriesList : EnumEntries<ChannelFlag>, List<ChannelFlag> by entries {
            override fun equals(other: Any?): Boolean = entries == other

            override fun hashCode(): Int = entries.hashCode()

            override fun toString(): String = entries.toString()
        }
    }
}