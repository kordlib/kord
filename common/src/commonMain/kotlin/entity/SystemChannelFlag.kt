@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import dev.kord.common.Class
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
 * See [SystemChannelFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-system-channel-flags).
 */
public sealed class SystemChannelFlag(
    /**
     * The position of the bit that is set in this [SystemChannelFlag]. This is always in 0..30.
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
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: SystemChannelFlag): SystemChannelFlags =
            SystemChannelFlags(this.code or flag.code, null)

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: SystemChannelFlags): SystemChannelFlags =
            SystemChannelFlags(this.code or flags.code, null)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SystemChannelFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown)
            "SystemChannelFlag.Unknown(shift=$shift)" else
            "SystemChannelFlag.${this::class.simpleName}"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SystemChannelFlag is no longer an enum class. Deprecated without a replacement.", level = DeprecationLevel.HIDDEN)
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SystemChannelFlag is no longer an enum class. Deprecated without a replacement.", level = DeprecationLevel.HIDDEN)
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
        DeprecationLevel.HIDDEN,
    )
    public fun getDeclaringClass(): Class<SystemChannelFlag> = SystemChannelFlag::class.java

    /**
     * An unknown [SystemChannelFlag].
     *
     * This is used as a fallback for [SystemChannelFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
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
     * Suppress server setup tips.
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
         * Returns an instance of [SystemChannelFlag] with [SystemChannelFlag.shift] equal to the
         * specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): SystemChannelFlag = when (shift) {
            0 -> SuppressJoinNotifications
            1 -> SuppressPremiumSubscriptions
            2 -> SuppressGuildReminderNotifications
            3 -> SuppressJoinNotificationReplies
            4 -> SuppressRoleSubscriptionPurchaseNotifications
            5 -> SuppressRoleSubscriptionPurchaseNotificationReplies
            else -> Unknown(shift)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "SystemChannelFlag is no longer an enum class. Deprecated without a replacement.", level = DeprecationLevel.HIDDEN)
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
            DeprecationLevel.HIDDEN,
        )
        @JvmStatic
        public open fun values(): Array<SystemChannelFlag> = entries.toTypedArray()
    }
}

/**
 * A collection of multiple [SystemChannelFlag]s.
 *
 * ## Creating an instance of [SystemChannelFlags]
 *
 * You can create an instance of [SystemChannelFlags] using the following methods:
 * ```kotlin
 * // from individual SystemChannelFlags
 * val systemChannelFlags1 = SystemChannelFlags(SystemChannelFlag.SuppressJoinNotifications,
 * SystemChannelFlag.SuppressPremiumSubscriptions)
 *
 * // from an Iterable
 * val iterable: Iterable<SystemChannelFlag> = TODO()
 * val systemChannelFlags2 = SystemChannelFlags(iterable)
 *
 * // using a builder
 * val systemChannelFlags3 = SystemChannelFlags {
 *     +systemChannelFlags2
 *     +SystemChannelFlag.SuppressJoinNotifications
 *     -SystemChannelFlag.SuppressPremiumSubscriptions
 * }
 * ```
 *
 * ## Modifying an existing instance of [SystemChannelFlags]
 *
 * You can create a modified copy of an existing instance of [SystemChannelFlags] using the [copy]
 * method:
 * ```kotlin
 * systemChannelFlags.copy {
 *     +SystemChannelFlag.SuppressJoinNotifications
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [SystemChannelFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val systemChannelFlags1 = systemChannelFlags + SystemChannelFlag.SuppressJoinNotifications
 * val systemChannelFlags2 = systemChannelFlags - SystemChannelFlag.SuppressPremiumSubscriptions
 * val systemChannelFlags3 = systemChannelFlags1 + systemChannelFlags2
 * ```
 *
 * ## Checking for [SystemChannelFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [SystemChannelFlags] contains
 * specific [SystemChannelFlag]s:
 * ```kotlin
 * val hasSystemChannelFlag = SystemChannelFlag.SuppressJoinNotifications in systemChannelFlags
 * val hasSystemChannelFlags = SystemChannelFlags(SystemChannelFlag.SuppressJoinNotifications,
 * SystemChannelFlag.SuppressPremiumSubscriptions) in systemChannelFlags
 * ```
 *
 * ## Unknown [SystemChannelFlag]s
 *
 * Whenever [SystemChannelFlag]s haven't been added to Kord yet, they will be deserialized as
 * instances of [SystemChannelFlag.Unknown].
 *
 * You can also use [SystemChannelFlag.fromShift] to check for [unknown][SystemChannelFlag.Unknown]
 * [SystemChannelFlag]s.
 * ```kotlin
 * val hasUnknownSystemChannelFlag = SystemChannelFlag.fromShift(23) in systemChannelFlags
 * ```
 *
 * @see SystemChannelFlag
 * @see SystemChannelFlags.Builder
 */
@Serializable(with = SystemChannelFlags.Serializer::class)
public class SystemChannelFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
    @Suppress("UNUSED_PARAMETER") unused: Nothing?,
) {
    // TODO uncomment annotation in DiscordGuild.kt and delete this file when this constructor is removed after
    //  deprecation cycle
    @Deprecated(
        "Don't construct an instance of 'SystemChannelFlags' from a raw code. Use the factory functions described in " +
            "the documentation instead.",
        ReplaceWith("SystemChannelFlags.Builder(code).build()", "dev.kord.common.entity.SystemChannelFlags"),
        DeprecationLevel.HIDDEN,
    )
    public constructor(code: Int) : this(code, null)

    /**
     * A [Set] of all [SystemChannelFlag]s contained in this instance of [SystemChannelFlags].
     */
    public val values: Set<SystemChannelFlag>
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(SystemChannelFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [SystemChannelFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: SystemChannelFlag): Boolean =
            this.code and flag.code == flag.code

    /**
     * Checks if this instance of [SystemChannelFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: SystemChannelFlags): Boolean =
            this.code and flags.code == flags.code

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: SystemChannelFlag): SystemChannelFlags =
            SystemChannelFlags(this.code or flag.code, null)

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: SystemChannelFlags): SystemChannelFlags =
            SystemChannelFlags(this.code or flags.code, null)

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this`
     * except the bits that are set in [flag].
     */
    public operator fun minus(flag: SystemChannelFlag): SystemChannelFlags =
            SystemChannelFlags(this.code and flag.code.inv(), null)

    /**
     * Returns an instance of [SystemChannelFlags] that has all bits set that are set in `this`
     * except the bits that are set in [flags].
     */
    public operator fun minus(flags: SystemChannelFlags): SystemChannelFlags =
            SystemChannelFlags(this.code and flags.code.inv(), null)

    /**
     * Returns a copy of this instance of [SystemChannelFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): SystemChannelFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
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
        DeprecationLevel.HIDDEN,
    )
    public operator fun component1(): Int = code

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "SystemChannelFlags is no longer a data class. Deprecated without a replacement.", level = DeprecationLevel.HIDDEN)
    public fun copy(code: Int = this.code): SystemChannelFlags = SystemChannelFlags(code, null)

    public class Builder(
        private var code: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [SystemChannelFlag].
         */
        public operator fun SystemChannelFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Sets all bits in the [Builder] that are set in this [SystemChannelFlags].
         */
        public operator fun SystemChannelFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SystemChannelFlag].
         */
        public operator fun SystemChannelFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [SystemChannelFlags].
         */
        public operator fun SystemChannelFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Returns an instance of [SystemChannelFlags] that has all bits set that are currently set
         * in this [Builder].
         */
        public fun build(): SystemChannelFlags = SystemChannelFlags(code, null)
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
                SystemChannelFlags(decoder.decodeSerializableValue(delegate), null)
    }

    public companion object NewCompanion {
        @Suppress("DEPRECATION_ERROR")
        @Deprecated(
            "Renamed to 'NewCompanion', which no longer implements 'KSerializer<SystemChannelFlags>'.",
            ReplaceWith("SystemChannelFlags.serializer()", imports = ["dev.kord.common.entity.SystemChannelFlags"]),
            DeprecationLevel.HIDDEN,
        )
        @JvmField
        public val Companion: Companion = Companion()
    }

    @Deprecated(
        "Renamed to 'NewCompanion', which no longer implements 'KSerializer<SystemChannelFlags>'.",
        ReplaceWith("SystemChannelFlags.serializer()", imports = ["dev.kord.common.entity.SystemChannelFlags"]),
        DeprecationLevel.HIDDEN,
    )
    public class Companion internal constructor() : KSerializer<SystemChannelFlags> by Serializer {
        public fun serializer(): KSerializer<SystemChannelFlags> = this
    }
}

/**
 * Returns an instance of [SystemChannelFlags] built with [SystemChannelFlags.Builder].
 */
public inline fun SystemChannelFlags(builder: SystemChannelFlags.Builder.() -> Unit = {}):
        SystemChannelFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return SystemChannelFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [SystemChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SystemChannelFlags(vararg flags: SystemChannelFlag): SystemChannelFlags =
        SystemChannelFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SystemChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SystemChannelFlags(vararg flags: SystemChannelFlags): SystemChannelFlags =
        SystemChannelFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SystemChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun SystemChannelFlags(flags: Iterable<SystemChannelFlag>): SystemChannelFlags =
        SystemChannelFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [SystemChannelFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("SystemChannelFlags0")
public fun SystemChannelFlags(flags: Iterable<SystemChannelFlags>): SystemChannelFlags =
        SystemChannelFlags {
    flags.forEach { +it }
}
