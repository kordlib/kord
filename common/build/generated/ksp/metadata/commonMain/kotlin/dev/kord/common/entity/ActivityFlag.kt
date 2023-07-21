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
public class ActivityFlags(
    public val code: Int = 0,
) {
    public val values: Set<ActivityFlag>
        get() = ActivityFlag.entries.filter { it in this }.toSet()

    public operator fun contains(flag: ActivityFlag): Boolean = this.code and flag.code == flag.code

    public operator fun contains(flags: ActivityFlags): Boolean =
            this.code and flags.code == flags.code

    public operator fun plus(flag: ActivityFlag): ActivityFlags =
            ActivityFlags(this.code or flag.code)

    public operator fun plus(flags: ActivityFlags): ActivityFlags =
            ActivityFlags(this.code or flags.code)

    public operator fun minus(flag: ActivityFlag): ActivityFlags =
            ActivityFlags(this.code and flag.code.inv())

    public operator fun minus(flags: ActivityFlags): ActivityFlags =
            ActivityFlags(this.code and flags.code.inv())

    override fun equals(other: Any?): Boolean = this === other ||
            (other is ActivityFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "ActivityFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun ActivityFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ActivityFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ActivityFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun ActivityFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): ActivityFlags = ActivityFlags(code)
    }

    internal object Serializer : KSerializer<ActivityFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ActivityFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: ActivityFlags) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): ActivityFlags =
                ActivityFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun ActivityFlags(builder: ActivityFlags.Builder.() -> Unit): ActivityFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return ActivityFlags.Builder().apply(builder).flags()
}

public fun ActivityFlags(vararg flags: ActivityFlag): ActivityFlags = ActivityFlags {
        flags.forEach { +it } }

public fun ActivityFlags(vararg flags: ActivityFlags): ActivityFlags = ActivityFlags {
        flags.forEach { +it } }

public fun ActivityFlags(flags: Iterable<ActivityFlag>): ActivityFlags = ActivityFlags {
        flags.forEach { +it } }

@JvmName("ActivityFlags0")
public fun ActivityFlags(flags: Iterable<ActivityFlags>): ActivityFlags = ActivityFlags {
        flags.forEach { +it } }

public inline fun ActivityFlags.copy(block: ActivityFlags.Builder.() -> Unit): ActivityFlags {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return ActivityFlags.Builder(code).apply(block).flags()
}

/**
 * See [ActivityFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-flags).
 */
public sealed class ActivityFlag(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ActivityFlag && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()

    final override fun toString(): String = "ActivityFlag.${this::class.simpleName}(code=$code)"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ActivityFlag is no longer an enum class. Deprecated without replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ActivityFlag is no longer an enum class. Deprecated without replacement.")
    public fun ordinal(): Int = when (this) {
        Instance -> 0
        Join -> 1
        Spectate -> 2
        JoinRequest -> 3
        Sync -> 4
        Play -> 5
        PartyPrivacyFriends -> 6
        PartyPrivacVoiceChannel -> 7
        Embed -> 8
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
    public fun getDeclaringClass(): Class<ActivityFlag>? = ActivityFlag::class.java

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
        public val PartyPrivacVoiceChannel: ActivityFlag = PartyPrivacVoiceChannel

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Embed: ActivityFlag = Embed

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "ActivityFlag is no longer an enum class. Deprecated without replacement.")
        @JvmStatic
        public open fun valueOf(name: String): ActivityFlag = when (name) {
            "Instance" -> Instance
            "Join" -> Join
            "Spectate" -> Spectate
            "JoinRequest" -> JoinRequest
            "Sync" -> Sync
            "Play" -> Play
            "PartyPrivacyFriends" -> PartyPrivacyFriends
            "PartyPrivacVoiceChannel" -> PartyPrivacVoiceChannel
            "Embed" -> Embed
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
    }
}
