// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordUnsafe
import dev.kord.common.entity.flags.BitFlags
import dev.kord.common.entity.flags.IntBitFlag
import dev.kord.common.entity.flags.IntBitFlags
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind

/**
 * Convenience container of multiple [ApplicationFlags][ApplicationFlag] which can be combined into
 * one.
 *
 * ## Creating a collection of message flags
 * You can create an [ApplicationFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = ApplicationFlags(ApplicationFlag.GatewayPresence,
 * ApplicationFlag.GatewayPresenceLimited)
 * // From an iterable
 * val flags2 = ApplicationFlags(listOf(ApplicationFlag.GatewayPresence,
 * ApplicationFlag.GatewayPresenceLimited))
 * // Using a builder
 * val flags3 = ApplicationFlags {
 *  +ApplicationFlag.GatewayPresence
 *  -ApplicationFlag.GatewayPresenceLimited
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [ApplicationFlags] instance using the
 * [dev.kord.common.entity.flags.copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +ApplicationFlag.GatewayPresence
 * }
 * ```
 *
 * ## Mathematical operators
 * All [ApplicationFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = ApplicationFlags(ApplicationFlag.GatewayPresence)
 * val flags2 = flags + ApplicationFlag.GatewayPresenceLimited
 * val otherFlags = flags - ApplicationFlag.GatewayGuildMembers
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = ApplicationFlag.GatewayPresence in obj.flags
 * val hasFlags = ApplicationFlags(ApplicationFlag.GatewayGuildMembersLimited,
 * ApplicationFlag.VerificationPendingGuildLimit) in obj.flags
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
 * @property code numeric value of all [ApplicationFlag]s
 */
@Serializable(with = ApplicationFlags.Serializer::class)
public class ApplicationFlags(
    code: Int = 0,
) :
        IntBitFlags<ApplicationFlag, ApplicationFlags, ApplicationFlags.Builder>(ApplicationFlag.entries,
        code) {
    protected override val name: String = "ApplicationFlags"

    public override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationFlags && this.code == other.code)

    public override fun hashCode(): Int = code.hashCode()

    internal override fun buildUpon(): Builder = Builder(code)

    protected override fun Implementation(flags: Int): ApplicationFlags = ApplicationFlags(flags)

    public class Builder(
        code: Int = 0,
    ) : IntBitFlags.Builder<ApplicationFlag, ApplicationFlags>(code) {
        public override fun flags(): ApplicationFlags = ApplicationFlags(code)
    }

    public class Serializer :
            BitFlags.Serializer<Int, ApplicationFlag, ApplicationFlags>(PrimitiveKind.INT, "code",
            Int.serializer()) {
        public override fun Implementation(code: Int): ApplicationFlags = ApplicationFlags(code)
    }

    public companion object : BitFlags.Companion<Int, ApplicationFlag, ApplicationFlags, Builder>()
            {
        internal override fun Builder(): Builder = ApplicationFlags.Builder()
    }
}

public sealed class ApplicationFlag(
    /**
     * The raw code used by Discord.
     */
    public override val code: Int,
) : IntBitFlag {
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
     * Intent required for bots in **100 or more servers** to receive
     * [`PresenceUpdate` events](https://discord.com/developers/docs/topics/gateway#presence-update).
     */
    public object GatewayPresence : ApplicationFlag(4096)

    /**
     * Intent required for bots in under 100 servers to receive
     * [`PresenceUpdate` events](https://discord.com/developers/docs/topics/gateway#presence-update),
     * found in Bot
     * Settings.
     */
    public object GatewayPresenceLimited : ApplicationFlag(8192)

    /**
     * Intent required for bots in **100 or more servers** to receive member-related events like
     * `GuildMemberAdd`.
     *
     * See list of member-related events under
     * [`GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents).
     */
    public object GatewayGuildMembers : ApplicationFlag(16384)

    /**
     * Intent required for bots in under 100 servers to receive member-related events like
     * `GuildMemberAdd`, found in
     * Bot Settings.
     *
     * See list of member-related events under
     * [`GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents).
     */
    public object GatewayGuildMembersLimited : ApplicationFlag(32768)

    /**
     * Indicates unusual growth of an app that prevents verification.
     */
    public object VerificationPendingGuildLimit : ApplicationFlag(65536)

    /**
     * Indicates if an app is embedded within the Discord client (currently unavailable publicly).
     */
    public object Embedded : ApplicationFlag(131072)

    /**
     * Intent required for bots in **100 or more servers*to receive
     * [message content](https://support-dev.discord.com/hc/en-us/articles/4404772028055).
     */
    public object GatewayMessageContent : ApplicationFlag(262144)

    /**
     * Intent required for bots in under 100 servers to receive
     * [message content](https://support-dev.discord.com/hc/en-us/articles/4404772028055), found in
     * Bot Settings.
     */
    public object GatewayMessageContentLimited : ApplicationFlag(524288)

    /**
     * Indicates if an app has registered global application commands.
     */
    public object ApplicationCommandBadge : ApplicationFlag(8388608)

    public companion object {
        /**
         * A [List] of all known [ApplicationFlag]s.
         */
        public val entries: List<ApplicationFlag> by lazy(mode = PUBLICATION) {
            listOf(
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
