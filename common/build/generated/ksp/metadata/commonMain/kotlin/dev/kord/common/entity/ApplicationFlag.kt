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

    public override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationFlags && this.code == other.code)

    public override fun hashCode(): Int = code.hashCode()

    public override fun toString(): String = "ApplicationFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun ApplicationFlag.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ApplicationFlags.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ApplicationFlag.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun ApplicationFlags.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): ApplicationFlags = ApplicationFlags(code)
    }

    internal object Serializer : KSerializer<ApplicationFlags> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ApplicationFlags",
                PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        public override fun serialize(encoder: Encoder, `value`: ApplicationFlags) =
                encoder.encodeSerializableValue(delegate, value.code)

        public override fun deserialize(decoder: Decoder) =
                ApplicationFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun ApplicationFlags(builder: ApplicationFlags.Builder.() -> Unit): ApplicationFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return ApplicationFlags.Builder().apply(builder).flags()
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

public inline fun ApplicationFlags.copy(block: ApplicationFlags.Builder.() -> Unit):
        ApplicationFlags {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return ApplicationFlags.Builder(code).apply(block).flags()
}

/**
 * See [ApplicationFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/application#application-object-application-flags).
 */
public sealed class ApplicationFlag(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationFlag && this.code == other.code)

    public final override fun hashCode(): Int = code.hashCode()

    public final override fun toString(): String =
            "ApplicationFlag.${this::class.simpleName}(code=$code)"

    /**
     * An unknown [ApplicationFlag].
     *
     * This is used as a fallback for [ApplicationFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        code: Int,
    ) : ApplicationFlag(code)

    /**
     * Indicates if an app uses the Auto Moderation API.
     */
    public object ApplicationAutoModerationRuleCreateBadge : ApplicationFlag(64)

    /**
     * Intent required for bots in **100 or more servers** to receive `PresenceUpdate` events.
     */
    public object GatewayPresence : ApplicationFlag(4_096)

    /**
     * Intent required for bots in under 100 servers to receive `PresenceUpdate` events, found on
     * the **Bot** page in your app's settings.
     */
    public object GatewayPresenceLimited : ApplicationFlag(8_192)

    /**
     * Intent required for bots in **100 or more servers** to receive member-related events like
     * `GuildMemberAdd`.
     *
     * See the list of member-related events [under
     * `GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents).
     */
    public object GatewayGuildMembers : ApplicationFlag(16_384)

    /**
     * Intent required for bots in under 100 servers to receive member-related events like
     * `GuildMemberAdd`, found on the **Bot** page in your app's settings.
     *
     * See the list of member-related events [under
     * `GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents).
     */
    public object GatewayGuildMembersLimited : ApplicationFlag(32_768)

    /**
     * Indicates unusual growth of an app that prevents verification.
     */
    public object VerificationPendingGuildLimit : ApplicationFlag(65_536)

    /**
     * Indicates if an app is embedded within the Discord client (currently unavailable publicly).
     */
    public object Embedded : ApplicationFlag(131_072)

    /**
     * Intent required for bots in **100 or more servers*to receive
     * [message content](https://support-dev.discord.com/hc/en-us/articles/4404772028055).
     */
    public object GatewayMessageContent : ApplicationFlag(262_144)

    /**
     * Intent required for bots in under 100 servers to receive [message
     * content](https://support-dev.discord.com/hc/en-us/articles/4404772028055), found on the **Bot**
     * page in your app's settings.
     */
    public object GatewayMessageContentLimited : ApplicationFlag(524_288)

    /**
     * Indicates if an app has registered global application commands.
     */
    public object ApplicationCommandBadge : ApplicationFlag(8_388_608)

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

    }
}
