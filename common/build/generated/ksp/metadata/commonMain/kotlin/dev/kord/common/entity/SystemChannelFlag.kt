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

    public inline fun copy(block: Builder.() -> Unit): SystemChannelFlags {
        contract { callsInPlace(block, EXACTLY_ONCE) }
        return Builder(code).apply(block).flags()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is SystemChannelFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "SystemChannelFlags(values=$values)"

    /**
     * @suppress
     */
    @Deprecated(
        message = "SystemChannelFlags is no longer a data class.",
        replaceWith = ReplaceWith(expression = "this.code", imports = arrayOf()),
    )
    public operator fun component1(): Int = code

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SystemChannelFlags is no longer a data class. Deprecated without a replacement.")
    public fun copy(code: Int = this.code): SystemChannelFlags = SystemChannelFlags(code)

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun SystemChannelFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun SystemChannelFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun SystemChannelFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun SystemChannelFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): SystemChannelFlags = SystemChannelFlags(code)
    }

    internal object Serializer : KSerializer<SystemChannelFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.SystemChannelFlags",
                PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: SystemChannelFlags) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): SystemChannelFlags =
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

/**
 * See [SystemChannelFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-system-channel-flags).
 */
public sealed class SystemChannelFlag(
    shift: Int,
) {
    /**
     * The raw code used by Discord.
     */
    public val code: Int = 1 shl shift

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SystemChannelFlag && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()

    final override fun toString(): String =
            "SystemChannelFlag.${this::class.simpleName}(code=$code)"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SystemChannelFlag is no longer an enum class. Deprecated without a replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SystemChannelFlag is no longer an enum class. Deprecated without a replacement.")
    public fun ordinal(): Int = when (this) {
        SuppressJoinNotifications -> 0
        SuppressPremiumSubscriptions -> 1
        SuppressGuildReminderNotifications -> 2
        SuppressJoinNotificationReplies -> 3
        SuppressRoleSubscriptionPurchaseNotifications -> 4
        SuppressRoleSubscriptionPurchaseNotificationReplies -> 5
        is Unknown -> Int.MAX_VALUE
    }

    /**
     * @suppress
     */
    @Deprecated(
        message = "SystemChannelFlag is no longer an enum class.",
        replaceWith = ReplaceWith(expression = "SystemChannelFlag::class.java", imports =
                    arrayOf("dev.kord.common.entity.SystemChannelFlag")),
    )
    public fun getDeclaringClass(): Class<SystemChannelFlag>? = SystemChannelFlag::class.java

    /**
     * An unknown [SystemChannelFlag].
     *
     * This is used as a fallback for [SystemChannelFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        shift: Int,
    ) : SystemChannelFlag(shift)

    /**
     * Suppress member join notifications.
     */
    public object SuppressJoinNotifications : SystemChannelFlag(0)

    /**
     * Suppress server boost notifications.
     */
    public object SuppressPremiumSubscriptions : SystemChannelFlag(1)

    /**
     * Hide server setup tips.
     */
    public object SuppressGuildReminderNotifications : SystemChannelFlag(2)

    /**
     * Hide member join sticker reply buttons.
     */
    public object SuppressJoinNotificationReplies : SystemChannelFlag(3)

    /**
     * Suppress role subscription purchase and renewal notifications.
     */
    public object SuppressRoleSubscriptionPurchaseNotifications : SystemChannelFlag(4)

    /**
     * Hide role subscription sticker reply buttons.
     */
    public object SuppressRoleSubscriptionPurchaseNotificationReplies : SystemChannelFlag(5)

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


        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val SuppressJoinNotifications: SystemChannelFlag = SuppressJoinNotifications

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val SuppressPremiumSubscriptions: SystemChannelFlag = SuppressPremiumSubscriptions

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val SuppressGuildReminderNotifications: SystemChannelFlag =
                SuppressGuildReminderNotifications

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val SuppressJoinNotificationReplies: SystemChannelFlag =
                SuppressJoinNotificationReplies

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val SuppressRoleSubscriptionPurchaseNotifications: SystemChannelFlag =
                SuppressRoleSubscriptionPurchaseNotifications

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val SuppressRoleSubscriptionPurchaseNotificationReplies: SystemChannelFlag =
                SuppressRoleSubscriptionPurchaseNotificationReplies

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "SystemChannelFlag is no longer an enum class. Deprecated without a replacement.")
        @JvmStatic
        public open fun valueOf(name: String): SystemChannelFlag = when (name) {
            "SuppressJoinNotifications" -> SuppressJoinNotifications
            "SuppressPremiumSubscriptions" -> SuppressPremiumSubscriptions
            "SuppressGuildReminderNotifications" -> SuppressGuildReminderNotifications
            "SuppressJoinNotificationReplies" -> SuppressJoinNotificationReplies
            "SuppressRoleSubscriptionPurchaseNotifications" -> SuppressRoleSubscriptionPurchaseNotifications
            "SuppressRoleSubscriptionPurchaseNotificationReplies" -> SuppressRoleSubscriptionPurchaseNotificationReplies
            else -> throw IllegalArgumentException(name)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT"))
        @Deprecated(
            message = "SystemChannelFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "SystemChannelFlag.entries.toTypedArray()",
                        imports = arrayOf("dev.kord.common.entity.SystemChannelFlag")),
        )
        @JvmStatic
        public open fun values(): Array<SystemChannelFlag> = entries.toTypedArray()

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "UPPER_BOUND_VIOLATED"))
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "SystemChannelFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "SystemChannelFlag.entries", imports =
                        arrayOf("dev.kord.common.entity.SystemChannelFlag")),
        )
        @JvmStatic
        public open fun getEntries(): EnumEntries<SystemChannelFlag> = EnumEntriesList

        @Suppress(names = arrayOf("SEALED_INHERITOR_IN_DIFFERENT_MODULE",
                        "SEALED_INHERITOR_IN_DIFFERENT_PACKAGE", "UPPER_BOUND_VIOLATED"))
        private object EnumEntriesList : EnumEntries<SystemChannelFlag>, List<SystemChannelFlag> by
                entries {
            override fun equals(other: Any?): Boolean = entries == other

            override fun hashCode(): Int = entries.hashCode()

            override fun toString(): String = entries.toString()
        }
    }
}
