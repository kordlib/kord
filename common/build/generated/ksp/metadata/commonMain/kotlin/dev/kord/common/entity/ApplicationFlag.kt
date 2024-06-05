// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlinx.serialization.Serializable

/**
 * See [ApplicationFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/application#application-object-application-flags).
 */
public sealed class ApplicationFlag(
    /**
     * The position of the bit that is set in this [ApplicationFlag]. This is always in 0..30.
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
     * Returns an instance of [ApplicationFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: ApplicationFlag): ApplicationFlags =
            ApplicationFlags(this.code or flag.code)

    /**
     * Returns an instance of [ApplicationFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: ApplicationFlags): ApplicationFlags =
            ApplicationFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "ApplicationFlag.Unknown(shift=$shift)"
            else "ApplicationFlag.${this::class.simpleName}"

    /**
     * An unknown [ApplicationFlag].
     *
     * This is used as a fallback for [ApplicationFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
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
     * See the list of member-related events
     * [under `GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents).
     */
    public object GatewayGuildMembers : ApplicationFlag(14)

    /**
     * Intent required for bots in under 100 servers to receive member-related events like
     * `GuildMemberAdd`, found on the **Bot** page in your app's settings.
     *
     * See the list of member-related events
     * [under `GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents).
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
     * Intent required for bots in **100 or more servers** to receive
     * [message content](https://support-dev.discord.com/hc/en-us/articles/4404772028055).
     */
    public object GatewayMessageContent : ApplicationFlag(18)

    /**
     * Intent required for bots in under 100 servers to receive
     * [message content](https://support-dev.discord.com/hc/en-us/articles/4404772028055), found on the
     * **Bot** page in your app's settings.
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


        /**
         * Returns an instance of [ApplicationFlag] with [ApplicationFlag.shift] equal to the
         * specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): ApplicationFlag = when (shift) {
            6 -> ApplicationAutoModerationRuleCreateBadge
            12 -> GatewayPresence
            13 -> GatewayPresenceLimited
            14 -> GatewayGuildMembers
            15 -> GatewayGuildMembersLimited
            16 -> VerificationPendingGuildLimit
            17 -> Embedded
            18 -> GatewayMessageContent
            19 -> GatewayMessageContentLimited
            23 -> ApplicationCommandBadge
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [ApplicationFlag]s.
 *
 * ## Creating an instance of [ApplicationFlags]
 *
 * You can create an instance of [ApplicationFlags] using the following methods:
 * ```kotlin
 * // from individual ApplicationFlags
 * val applicationFlags1 = ApplicationFlags(ApplicationFlag.ApplicationAutoModerationRuleCreateBadge, ApplicationFlag.GatewayPresence)
 *
 * // from an Iterable
 * val iterable: Iterable<ApplicationFlag> = TODO()
 * val applicationFlags2 = ApplicationFlags(iterable)
 *
 * // using a builder
 * val applicationFlags3 = ApplicationFlags {
 *     +applicationFlags2
 *     +ApplicationFlag.ApplicationAutoModerationRuleCreateBadge
 *     -ApplicationFlag.GatewayPresence
 * }
 * ```
 *
 * ## Modifying an existing instance of [ApplicationFlags]
 *
 * You can create a modified copy of an existing instance of [ApplicationFlags] using the [copy]
 * method:
 * ```kotlin
 * applicationFlags.copy {
 *     +ApplicationFlag.ApplicationAutoModerationRuleCreateBadge
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [ApplicationFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val applicationFlags1 = applicationFlags + ApplicationFlag.ApplicationAutoModerationRuleCreateBadge
 * val applicationFlags2 = applicationFlags - ApplicationFlag.GatewayPresence
 * val applicationFlags3 = applicationFlags1 + applicationFlags2
 * ```
 *
 * ## Checking for [ApplicationFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [ApplicationFlags] contains
 * specific [ApplicationFlag]s:
 * ```kotlin
 * val hasApplicationFlag = ApplicationFlag.ApplicationAutoModerationRuleCreateBadge in applicationFlags
 * val hasApplicationFlags = ApplicationFlags(ApplicationFlag.ApplicationAutoModerationRuleCreateBadge, ApplicationFlag.GatewayPresence) in applicationFlags
 * ```
 *
 * ## Unknown [ApplicationFlag]s
 *
 * Whenever [ApplicationFlag]s haven't been added to Kord yet, they will be deserialized as
 * instances of [ApplicationFlag.Unknown].
 *
 * You can also use [ApplicationFlag.fromShift] to check for [unknown][ApplicationFlag.Unknown]
 * [ApplicationFlag]s.
 * ```kotlin
 * val hasUnknownApplicationFlag = ApplicationFlag.fromShift(23) in applicationFlags
 * ```
 *
 * @see ApplicationFlag
 * @see ApplicationFlags.Builder
 */
@JvmInline
@Serializable
public value class ApplicationFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    /**
     * A [Set] of all [ApplicationFlag]s contained in this instance of [ApplicationFlags].
     */
    public val values: Set<ApplicationFlag>
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(ApplicationFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [ApplicationFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: ApplicationFlag): Boolean =
            this.code and flag.code == flag.code

    /**
     * Checks if this instance of [ApplicationFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: ApplicationFlags): Boolean =
            this.code and flags.code == flags.code

    /**
     * Returns an instance of [ApplicationFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: ApplicationFlag): ApplicationFlags =
            ApplicationFlags(this.code or flag.code)

    /**
     * Returns an instance of [ApplicationFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: ApplicationFlags): ApplicationFlags =
            ApplicationFlags(this.code or flags.code)

    /**
     * Returns an instance of [ApplicationFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flag].
     */
    public operator fun minus(flag: ApplicationFlag): ApplicationFlags =
            ApplicationFlags(this.code and flag.code.inv())

    /**
     * Returns an instance of [ApplicationFlags] that has all bits set that are set in `this` except
     * the bits that are set in [flags].
     */
    public operator fun minus(flags: ApplicationFlags): ApplicationFlags =
            ApplicationFlags(this.code and flags.code.inv())

    /**
     * Returns a copy of this instance of [ApplicationFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): ApplicationFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }

    override fun toString(): String = "ApplicationFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [ApplicationFlag].
         */
        public operator fun ApplicationFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Sets all bits in the [Builder] that are set in this [ApplicationFlags].
         */
        public operator fun ApplicationFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [ApplicationFlag].
         */
        public operator fun ApplicationFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [ApplicationFlags].
         */
        public operator fun ApplicationFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Returns an instance of [ApplicationFlags] that has all bits set that are currently set in
         * this [Builder].
         */
        public fun build(): ApplicationFlags = ApplicationFlags(code)
    }
}

/**
 * Returns an instance of [ApplicationFlags] built with [ApplicationFlags.Builder].
 */
public inline fun ApplicationFlags(builder: ApplicationFlags.Builder.() -> Unit = {}):
        ApplicationFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return ApplicationFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [ApplicationFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun ApplicationFlags(vararg flags: ApplicationFlag): ApplicationFlags = ApplicationFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [ApplicationFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun ApplicationFlags(flags: Iterable<ApplicationFlag>): ApplicationFlags = ApplicationFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [ApplicationFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("ApplicationFlags0")
public fun ApplicationFlags(flags: Iterable<ApplicationFlags>): ApplicationFlags =
        ApplicationFlags {
    flags.forEach { +it }
}
