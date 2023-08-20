// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

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
 * See [ActivityFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-flags).
 */
public sealed class ActivityFlag(
    /**
     * The position of the bit that is set in this [ActivityFlag]. This is always in 0..30.
     */
    public val shift: Int,
) {
    init {
        require(shift in 0..30) { """shift has to be in 0..30 but was $shift""" }
    }

    /**
     * The raw value used by Discord.
     */
    public val `value`: Int
        get() = 1 shl shift

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: ActivityFlag): ActivityFlags =
            ActivityFlags(this.value or flag.value)

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: ActivityFlags): ActivityFlags =
            ActivityFlags(this.value or flags.value)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ActivityFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown)
            "ActivityFlag.Unknown(shift=$shift)" else "ActivityFlag.${this::class.simpleName}"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ActivityFlag is no longer an enum class. Deprecated without a replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ActivityFlag is no longer an enum class. Deprecated without a replacement.")
    public fun ordinal(): Int = when (this) {
        Instance -> 0
        Join -> 1
        Spectate -> 2
        JoinRequest -> 3
        Sync -> 4
        Play -> 5
        PartyPrivacyFriends -> 6
        PartyPrivacyVoiceChannel -> 7
        Embedded -> 8
        is Unknown -> Int.MAX_VALUE
    }

    /**
     * @suppress
     */
    @Deprecated(
        message = "ActivityFlag is no longer an enum class.",
        replaceWith = ReplaceWith(expression = "ActivityFlag::class.java", imports =
                    arrayOf("dev.kord.common.entity.ActivityFlag")),
    )
    public fun getDeclaringClass(): Class<ActivityFlag> = ActivityFlag::class.java

    /**
     * An unknown [ActivityFlag].
     *
     * This is used as a fallback for [ActivityFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : ActivityFlag(shift)

    public object Instance : ActivityFlag(0)

    public object Join : ActivityFlag(1)

    public object Spectate : ActivityFlag(2)

    public object JoinRequest : ActivityFlag(3)

    public object Sync : ActivityFlag(4)

    public object Play : ActivityFlag(5)

    public object PartyPrivacyFriends : ActivityFlag(6)

    public object PartyPrivacyVoiceChannel : ActivityFlag(7)

    public object Embedded : ActivityFlag(8)

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
                PartyPrivacyVoiceChannel,
                Embedded,
            )
        }


        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Instance: ActivityFlag = Instance

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Join: ActivityFlag = Join

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Spectate: ActivityFlag = Spectate

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val JoinRequest: ActivityFlag = JoinRequest

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Sync: ActivityFlag = Sync

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Play: ActivityFlag = Play

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val PartyPrivacyFriends: ActivityFlag = PartyPrivacyFriends

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val PartyPrivacyVoiceChannel: ActivityFlag = PartyPrivacyVoiceChannel

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Embedded: ActivityFlag = Embedded

        /**
         * Returns an instance of [ActivityFlag] with [ActivityFlag.shift] equal to the specified
         * [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): ActivityFlag = when (shift) {
            0 -> Instance
            1 -> Join
            2 -> Spectate
            3 -> JoinRequest
            4 -> Sync
            5 -> Play
            6 -> PartyPrivacyFriends
            7 -> PartyPrivacyVoiceChannel
            8 -> Embedded
            else -> Unknown(shift)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "ActivityFlag is no longer an enum class. Deprecated without a replacement.")
        @JvmStatic
        public open fun valueOf(name: String): ActivityFlag = when (name) {
            "Instance" -> Instance
            "Join" -> Join
            "Spectate" -> Spectate
            "JoinRequest" -> JoinRequest
            "Sync" -> Sync
            "Play" -> Play
            "PartyPrivacyFriends" -> PartyPrivacyFriends
            "PartyPrivacyVoiceChannel" -> PartyPrivacyVoiceChannel
            "Embedded" -> Embedded
            else -> throw IllegalArgumentException(name)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT"))
        @Deprecated(
            message = "ActivityFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "ActivityFlag.entries.toTypedArray()", imports =
                        arrayOf("dev.kord.common.entity.ActivityFlag")),
        )
        @JvmStatic
        public open fun values(): Array<ActivityFlag> = entries.toTypedArray()

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "UPPER_BOUND_VIOLATED"))
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "ActivityFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "ActivityFlag.entries", imports =
                        arrayOf("dev.kord.common.entity.ActivityFlag")),
        )
        @JvmStatic
        public open fun getEntries(): EnumEntries<ActivityFlag> = EnumEntriesList

        @Suppress(names = arrayOf("SEALED_INHERITOR_IN_DIFFERENT_MODULE",
                        "SEALED_INHERITOR_IN_DIFFERENT_PACKAGE", "UPPER_BOUND_VIOLATED"))
        private object EnumEntriesList : EnumEntries<ActivityFlag>, List<ActivityFlag> by entries {
            override fun equals(other: Any?): Boolean = entries == other

            override fun hashCode(): Int = entries.hashCode()

            override fun toString(): String = entries.toString()
        }
    }
}

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
 * You can crate a modified copy of a [ActivityFlags] instance using the [copy] method
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
 * val otherFlags = flags - ActivityFlag.Join
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = ActivityFlag.Instance in obj.flags
 * val hasFlags = ActivityFlag(ActivityFlag.Join, ActivityFlag.Join) in obj.flags
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
 * @property code numeric value of all [ActivityFlags]s
 */
@Serializable(with = ActivityFlags.Serializer::class)
public class ActivityFlags internal constructor(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    /**
     * A [Set] of all [ActivityFlag]s contained in this instance of [ActivityFlags].
     */
    public val values: Set<ActivityFlag>
        get() = buildSet {
            var remaining = value
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(ActivityFlag.fromShift(shift))
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
    public val flags: Set<ActivityFlag>
        get() = values

    /**
     * Checks if this instance of [ActivityFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: ActivityFlag): Boolean =
            this.value and flag.value == flag.value

    /**
     * Checks if this instance of [ActivityFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: ActivityFlags): Boolean =
            this.value and flags.value == flags.value

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: ActivityFlag): ActivityFlags =
            ActivityFlags(this.value or flag.value)

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: ActivityFlags): ActivityFlags =
            ActivityFlags(this.value or flags.value)

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flag].
     */
    public operator fun minus(flag: ActivityFlag): ActivityFlags =
            ActivityFlags(this.value and flag.value.inv())

    /**
     * Returns an instance of [ActivityFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flags].
     */
    public operator fun minus(flags: ActivityFlags): ActivityFlags =
            ActivityFlags(this.value and flags.value.inv())

    /**
     * Returns a copy of this instance of [ActivityFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): ActivityFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(value).apply(builder).build()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is ActivityFlags && this.value == other.value)

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "ActivityFlags(values=$values)"

    public class Builder(
        private var `value`: Int = 0,
    ) {
        public operator fun ActivityFlag.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        public operator fun ActivityFlags.unaryPlus() {
            this@Builder.value = this@Builder.value or this.value
        }

        public operator fun ActivityFlag.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        public operator fun ActivityFlags.unaryMinus() {
            this@Builder.value = this@Builder.value and this.value.inv()
        }

        public fun build(): ActivityFlags = ActivityFlags(value)

        /**
         * @suppress
         */
        @Deprecated(
            message = "Renamed to 'build'",
            replaceWith = ReplaceWith(expression = "this.build()", imports = arrayOf()),
        )
        public fun flags(): ActivityFlags = build()
    }

    internal object Serializer : KSerializer<ActivityFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ActivityFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: ActivityFlags) {
            encoder.encodeSerializableValue(delegate, value.value)
        }

        override fun deserialize(decoder: Decoder): ActivityFlags =
                ActivityFlags(decoder.decodeSerializableValue(delegate))
    }
}

/**
 * Returns an instance of [ActivityFlags] built with [ActivityFlags.Builder].
 */
public inline fun ActivityFlags(builder: ActivityFlags.Builder.() -> Unit = {}): ActivityFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return ActivityFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [ActivityFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun ActivityFlags(vararg flags: ActivityFlag): ActivityFlags = ActivityFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [ActivityFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun ActivityFlags(vararg flags: ActivityFlags): ActivityFlags = ActivityFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [ActivityFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun ActivityFlags(flags: Iterable<ActivityFlag>): ActivityFlags = ActivityFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [ActivityFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("ActivityFlags0")
public fun ActivityFlags(flags: Iterable<ActivityFlags>): ActivityFlags = ActivityFlags {
    flags.forEach { +it }
}
