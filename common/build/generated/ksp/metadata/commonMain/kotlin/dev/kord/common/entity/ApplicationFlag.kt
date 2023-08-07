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
 * Convenience container of multiple [ApplicationFlags][ApplicationFlag] which can be combined into
 * one.
 *
 * ## Creating a collection of message flags
 * You can create an [ApplicationFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = ApplicationFlags(ApplicationFlag.ApplicationAutoModerationRuleCreateBadge,
 * ApplicationFlag.GatewayPresence)
 * // From an iterable
 * val flags2 = ApplicationFlags(listOf(ApplicationFlag.ApplicationAutoModerationRuleCreateBadge,
 * ApplicationFlag.GatewayPresence))
 * // Using a builder
 * val flags3 = ApplicationFlags {
 *  +ApplicationFlag.ApplicationAutoModerationRuleCreateBadge
 *  -ApplicationFlag.GatewayPresence
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [ApplicationFlags] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +ApplicationFlag.ApplicationAutoModerationRuleCreateBadge
 * }
 * ```
 *
 * ## Mathematical operators
 * All [ApplicationFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = ApplicationFlags(ApplicationFlag.ApplicationAutoModerationRuleCreateBadge)
 * val flags2 = flags + ApplicationFlag.GatewayPresence
 * val otherFlags = flags - ApplicationFlag.GatewayPresence
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = ApplicationFlag.ApplicationAutoModerationRuleCreateBadge in obj.flags
 * val hasFlags = ApplicationFlag(ApplicationFlag.GatewayPresence,
 * ApplicationFlag.GatewayPresence) in obj.flags
 * ```
 *
 * ## Unknown flag
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [ApplicationFlag.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = ApplicationFlag.Unknown(1 shl 69) in obj.flags
 * ```
 * @see ApplicationFlag
 * @see ApplicationFlags.Builder
 * @property code numeric value of all [ApplicationFlags]s
 */
@Serializable(with = ApplicationFlags.Serializer::class)
public class ApplicationFlags(
    public val code: Int = 0,
) {
    public val values: Set<ApplicationFlag>
        get() = ApplicationFlag.entries.filter { it in this }.toSet()

    @Deprecated(
        message = "Renamed to 'values'.",
        replaceWith = ReplaceWith(expression = "this.values", imports = arrayOf()),
    )
    public val flags: List<ApplicationFlag>
        get() = values.toList()

    public operator fun contains(flag: ApplicationFlag): Boolean =
            this.code and flag.code == flag.code

    public operator fun contains(flags: ApplicationFlags): Boolean =
            this.code and flags.code == flags.code

    public operator fun plus(flag: ApplicationFlag): ApplicationFlags =
            ApplicationFlags(this.code or flag.code)

    public operator fun plus(flags: ApplicationFlags): ApplicationFlags =
            ApplicationFlags(this.code or flags.code)

    public operator fun minus(flag: ApplicationFlag): ApplicationFlags =
            ApplicationFlags(this.code and flag.code.inv())

    public operator fun minus(flags: ApplicationFlags): ApplicationFlags =
            ApplicationFlags(this.code and flags.code.inv())

    public inline fun copy(block: Builder.() -> Unit): ApplicationFlags {
        contract { callsInPlace(block, EXACTLY_ONCE) }
        return Builder(code).apply(block).build()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "ApplicationFlags(values=$values)"

    /**
     * @suppress
     */
    @Deprecated(
        message = "ApplicationFlags is no longer a data class.",
        replaceWith = ReplaceWith(expression = "this.code", imports = arrayOf()),
    )
    public operator fun component1(): Int = code

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ApplicationFlags is no longer a data class. Deprecated without a replacement.")
    public fun copy(code: Int = this.code): ApplicationFlags = ApplicationFlags(code)

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun ApplicationFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ApplicationFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ApplicationFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun ApplicationFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun build(): ApplicationFlags = ApplicationFlags(code)

        /**
         * @suppress
         */
        @Deprecated(
            message = "Renamed to 'build'",
            replaceWith = ReplaceWith(expression = "this.build()", imports = arrayOf()),
        )
        public fun flags(): ApplicationFlags = build()
    }

    internal object Serializer : KSerializer<ApplicationFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ApplicationFlags",
                PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: ApplicationFlags) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): ApplicationFlags =
                ApplicationFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun ApplicationFlags(builder: ApplicationFlags.Builder.() -> Unit): ApplicationFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return ApplicationFlags.Builder().apply(builder).build()
}

public fun ApplicationFlags(vararg flags: ApplicationFlag): ApplicationFlags = ApplicationFlags {
        flags.forEach { +it } }

public fun ApplicationFlags(vararg flags: ApplicationFlags): ApplicationFlags = ApplicationFlags {
        flags.forEach { +it } }

public fun ApplicationFlags(flags: Iterable<ApplicationFlag>): ApplicationFlags = ApplicationFlags {
        flags.forEach { +it } }

@JvmName("ApplicationFlags0")
public fun ApplicationFlags(flags: Iterable<ApplicationFlags>): ApplicationFlags =
        ApplicationFlags { flags.forEach { +it } }

/**
 * See [ApplicationFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/application#application-object-application-flags).
 */
public sealed class ApplicationFlag(
    shift: Int,
) {
    /**
     * The raw code used by Discord.
     */
    public val code: Int = 1 shl shift

    public operator fun plus(flag: ApplicationFlag): ApplicationFlags =
            ApplicationFlags(this.code or flag.code)

    public operator fun plus(flags: ApplicationFlags): ApplicationFlags =
            ApplicationFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationFlag && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()

    final override fun toString(): String = "ApplicationFlag.${this::class.simpleName}(code=$code)"

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ApplicationFlag is no longer an enum class. Deprecated without a replacement.")
    public fun name(): String = this::class.simpleName!!

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message =
            "ApplicationFlag is no longer an enum class. Deprecated without a replacement.")
    public fun ordinal(): Int = when (this) {
        ApplicationAutoModerationRuleCreateBadge -> 0
        GatewayPresence -> 1
        GatewayPresenceLimited -> 2
        GatewayGuildMembers -> 3
        GatewayGuildMembersLimited -> 4
        VerificationPendingGuildLimit -> 5
        Embedded -> 6
        GatewayMessageContent -> 7
        GatewayMessageContentLimited -> 8
        ApplicationCommandBadge -> 9
        is Unknown -> Int.MAX_VALUE
    }

    /**
     * @suppress
     */
    @Deprecated(
        message = "ApplicationFlag is no longer an enum class.",
        replaceWith = ReplaceWith(expression = "ApplicationFlag::class.java", imports =
                    arrayOf("dev.kord.common.entity.ApplicationFlag")),
    )
    public fun getDeclaringClass(): Class<ApplicationFlag>? = ApplicationFlag::class.java

    /**
     * An unknown [ApplicationFlag].
     *
     * This is used as a fallback for [ApplicationFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        shift: Int,
    ) : ApplicationFlag(shift)

    /**
     * Indicates if an app uses the Auto Moderation API.
     */
    public object ApplicationAutoModerationRuleCreateBadge : ApplicationFlag(6)

    /**
     * Intent required for bots in **100 or more servers** to receive `PresenceUpdate` events.
     */
    public object GatewayPresence : ApplicationFlag(12)

    /**
     * Intent required for bots in under 100 servers to receive `PresenceUpdate` events, found on
     * the **Bot** page in your app's settings.
     */
    public object GatewayPresenceLimited : ApplicationFlag(13)

    /**
     * Intent required for bots in **100 or more servers** to receive member-related events like
     * `GuildMemberAdd`.
     *
     * See the list of member-related events [under
     * `GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents).
     */
    public object GatewayGuildMembers : ApplicationFlag(14)

    /**
     * Intent required for bots in under 100 servers to receive member-related events like
     * `GuildMemberAdd`, found on the **Bot** page in your app's settings.
     *
     * See the list of member-related events [under
     * `GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents).
     */
    public object GatewayGuildMembersLimited : ApplicationFlag(15)

    /**
     * Indicates unusual growth of an app that prevents verification.
     */
    public object VerificationPendingGuildLimit : ApplicationFlag(16)

    /**
     * Indicates if an app is embedded within the Discord client (currently unavailable publicly).
     */
    public object Embedded : ApplicationFlag(17)

    /**
     * Intent required for bots in **100 or more servers*to receive
     * [message content](https://support-dev.discord.com/hc/en-us/articles/4404772028055).
     */
    public object GatewayMessageContent : ApplicationFlag(18)

    /**
     * Intent required for bots in under 100 servers to receive [message
     * content](https://support-dev.discord.com/hc/en-us/articles/4404772028055), found on the **Bot**
     * page in your app's settings.
     */
    public object GatewayMessageContentLimited : ApplicationFlag(19)

    /**
     * Indicates if an app has registered global application commands.
     */
    public object ApplicationCommandBadge : ApplicationFlag(23)

    public companion object {
        /**
         * A [List] of all known [ApplicationFlag]s.
         */
        public val entries: List<ApplicationFlag> by lazy(mode = PUBLICATION) {
            listOf(
                ApplicationAutoModerationRuleCreateBadge,
                GatewayPresence,
                GatewayPresenceLimited,
                GatewayGuildMembers,
                GatewayGuildMembersLimited,
                VerificationPendingGuildLimit,
                Embedded,
                GatewayMessageContent,
                GatewayMessageContentLimited,
                ApplicationCommandBadge,
            )
        }


        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val ApplicationAutoModerationRuleCreateBadge: ApplicationFlag =
                ApplicationAutoModerationRuleCreateBadge

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val GatewayPresence: ApplicationFlag = GatewayPresence

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val GatewayPresenceLimited: ApplicationFlag = GatewayPresenceLimited

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val GatewayGuildMembers: ApplicationFlag = GatewayGuildMembers

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val GatewayGuildMembersLimited: ApplicationFlag = GatewayGuildMembersLimited

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val VerificationPendingGuildLimit: ApplicationFlag = VerificationPendingGuildLimit

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Embedded: ApplicationFlag = Embedded

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val GatewayMessageContent: ApplicationFlag = GatewayMessageContent

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val GatewayMessageContentLimited: ApplicationFlag = GatewayMessageContentLimited

        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val ApplicationCommandBadge: ApplicationFlag = ApplicationCommandBadge

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        @Deprecated(message =
                "ApplicationFlag is no longer an enum class. Deprecated without a replacement.")
        @JvmStatic
        public open fun valueOf(name: String): ApplicationFlag = when (name) {
            "ApplicationAutoModerationRuleCreateBadge" -> ApplicationAutoModerationRuleCreateBadge
            "GatewayPresence" -> GatewayPresence
            "GatewayPresenceLimited" -> GatewayPresenceLimited
            "GatewayGuildMembers" -> GatewayGuildMembers
            "GatewayGuildMembersLimited" -> GatewayGuildMembersLimited
            "VerificationPendingGuildLimit" -> VerificationPendingGuildLimit
            "Embedded" -> Embedded
            "GatewayMessageContent" -> GatewayMessageContent
            "GatewayMessageContentLimited" -> GatewayMessageContentLimited
            "ApplicationCommandBadge" -> ApplicationCommandBadge
            else -> throw IllegalArgumentException(name)
        }

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT"))
        @Deprecated(
            message = "ApplicationFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "ApplicationFlag.entries.toTypedArray()", imports
                        = arrayOf("dev.kord.common.entity.ApplicationFlag")),
        )
        @JvmStatic
        public open fun values(): Array<ApplicationFlag> = entries.toTypedArray()

        /**
         * @suppress
         */
        @Suppress(names = arrayOf("NON_FINAL_MEMBER_IN_OBJECT", "UPPER_BOUND_VIOLATED"))
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "ApplicationFlag is no longer an enum class.",
            replaceWith = ReplaceWith(expression = "ApplicationFlag.entries", imports =
                        arrayOf("dev.kord.common.entity.ApplicationFlag")),
        )
        @JvmStatic
        public open fun getEntries(): EnumEntries<ApplicationFlag> = EnumEntriesList

        @Suppress(names = arrayOf("SEALED_INHERITOR_IN_DIFFERENT_MODULE",
                        "SEALED_INHERITOR_IN_DIFFERENT_PACKAGE", "UPPER_BOUND_VIOLATED"))
        private object EnumEntriesList : EnumEntries<ApplicationFlag>, List<ApplicationFlag> by
                entries {
            override fun equals(other: Any?): Boolean = entries == other

            override fun hashCode(): Int = entries.hashCode()

            override fun toString(): String = entries.toString()
        }
    }
}
