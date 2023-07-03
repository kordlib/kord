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
 * Convenience container of multiple [SystemChannelFlags][SystemChannelFlag] which can be combined
 * into one.
 *
 * ## Creating a collection of message flags
 * You can create an [SystemChannelFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = SystemChannelFlags(SystemChannelFlag.SuppressJoinNotifications,
 * SystemChannelFlag.SuppressPremiumSubscriptions)
 * // From an iterable
 * val flags2 = SystemChannelFlags(listOf(SystemChannelFlag.SuppressJoinNotifications,
 * SystemChannelFlag.SuppressPremiumSubscriptions))
 * // Using a builder
 * val flags3 = SystemChannelFlags {
 *  +SystemChannelFlag.SuppressJoinNotifications
 *  -SystemChannelFlag.SuppressPremiumSubscriptions
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [SystemChannelFlags] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +SystemChannelFlag.SuppressJoinNotifications
 * }
 * ```
 *
 * ## Mathematical operators
 * All [SystemChannelFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = SystemChannelFlags(SystemChannelFlag.SuppressJoinNotifications)
 * val flags2 = flags + SystemChannelFlag.SuppressPremiumSubscriptions
 * val otherFlags = flags - SystemChannelFlag.SuppressPremiumSubscriptions
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = SystemChannelFlag.SuppressJoinNotifications in obj.flags
 * val hasFlags = SystemChannelFlag(SystemChannelFlag.SuppressPremiumSubscriptions,
 * SystemChannelFlag.SuppressPremiumSubscriptions) in obj.flags
 * ```
 *
 * ## Unknown flag
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [SystemChannelFlag.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = SystemChannelFlag.Unknown(1 shl 69) in obj.flags
 * ```
 * @see SystemChannelFlag
 * @see SystemChannelFlags.Builder
 * @property code numeric value of all [SystemChannelFlags]s
 */
@Serializable(with = SystemChannelFlags.Serializer::class)
public class SystemChannelFlags(
    public val code: Int = 0,
) {
    public val values: Set<SystemChannelFlag>
        get() = SystemChannelFlag.entries.filter { it in this }.toSet()

    public operator fun contains(flag: SystemChannelFlag): Boolean =
            this.code and flag.code == flag.code

    public operator fun contains(flags: SystemChannelFlags): Boolean =
            this.code and flags.code == flags.code

    public operator fun plus(flag: SystemChannelFlag): SystemChannelFlags =
            SystemChannelFlags(this.code or flag.code)

    public operator fun plus(flags: SystemChannelFlags): SystemChannelFlags =
            SystemChannelFlags(this.code or flags.code)

    public operator fun minus(flag: SystemChannelFlag): SystemChannelFlags =
            SystemChannelFlags(this.code and flag.code.inv())

    public operator fun minus(flags: SystemChannelFlags): SystemChannelFlags =
            SystemChannelFlags(this.code and flags.code.inv())

    public override fun equals(other: Any?): Boolean = this === other ||
            (other is SystemChannelFlags && this.code == other.code)

    public override fun hashCode(): Int = code.hashCode()

    public override fun toString(): String = "SystemChannelFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun SystemChannelFlag.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun SystemChannelFlags.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun SystemChannelFlag.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun SystemChannelFlags.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): SystemChannelFlags = SystemChannelFlags(code)
    }

    internal object Serializer : KSerializer<SystemChannelFlags> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.SystemChannelFlags",
                PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        public override fun serialize(encoder: Encoder, `value`: SystemChannelFlags) =
                encoder.encodeSerializableValue(delegate, value.code)

        public override fun deserialize(decoder: Decoder) =
                SystemChannelFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun SystemChannelFlags(builder: SystemChannelFlags.Builder.() -> Unit):
        SystemChannelFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return SystemChannelFlags.Builder().apply(builder).flags()
}

public fun SystemChannelFlags(vararg flags: SystemChannelFlag): SystemChannelFlags =
        SystemChannelFlags { flags.forEach { +it } }

public fun SystemChannelFlags(vararg flags: SystemChannelFlags): SystemChannelFlags =
        SystemChannelFlags { flags.forEach { +it } }

public fun SystemChannelFlags(flags: Iterable<SystemChannelFlag>): SystemChannelFlags =
        SystemChannelFlags { flags.forEach { +it } }

@JvmName("SystemChannelFlags0")
public fun SystemChannelFlags(flags: Iterable<SystemChannelFlags>): SystemChannelFlags =
        SystemChannelFlags { flags.forEach { +it } }

public inline fun SystemChannelFlags.copy(block: SystemChannelFlags.Builder.() -> Unit):
        SystemChannelFlags {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return SystemChannelFlags.Builder(code).apply(block).flags()
}

/**
 * See [SystemChannelFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-system-channel-flags).
 */
public sealed class SystemChannelFlag(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is SystemChannelFlag && this.code == other.code)

    public final override fun hashCode(): Int = code.hashCode()

    public final override fun toString(): String =
            "SystemChannelFlag.${this::class.simpleName}(code=$code)"

    /**
     * An unknown [SystemChannelFlag].
     *
     * This is used as a fallback for [SystemChannelFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        code: Int,
    ) : SystemChannelFlag(code)

    /**
     * Suppress member join notifications.
     */
    public object SuppressJoinNotifications : SystemChannelFlag(1)

    /**
     * Suppress server boost notifications.
     */
    public object SuppressPremiumSubscriptions : SystemChannelFlag(2)

    /**
     * Hide server setup tips.
     */
    public object SuppressGuildReminderNotifications : SystemChannelFlag(4)

    /**
     * Hide member join sticker reply buttons.
     */
    public object SuppressJoinNotificationReplies : SystemChannelFlag(8)

    /**
     * Suppress role subscription purchase and renewal notifications.
     */
    public object SuppressRoleSubscriptionPurchaseNotifications : SystemChannelFlag(16)

    /**
     * Hide role subscription sticker reply buttons.
     */
    public object SuppressRoleSubscriptionPurchaseNotificationReplies : SystemChannelFlag(32)

    public companion object {
        /**
         * A [List] of all known [SystemChannelFlag]s.
         */
        public val entries: List<SystemChannelFlag> by lazy(mode = PUBLICATION) {
            listOf(
                SuppressJoinNotifications,
                SuppressPremiumSubscriptions,
                SuppressGuildReminderNotifications,
                SuppressJoinNotificationReplies,
                SuppressRoleSubscriptionPurchaseNotifications,
                SuppressRoleSubscriptionPurchaseNotificationReplies,
            )
        }

    }
}
