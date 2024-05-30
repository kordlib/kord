// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection", "MemberVisibilityCanBePrivate"))

package dev.kord.gateway

import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlinx.serialization.Serializable

/**
 * Values that enable a group of events as defined by Discord.
 *
 * See [Intent]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/gateway#gateway-intents).
 */
public sealed class Intent(
    /**
     * The position of the bit that is set in this [Intent]. This is always >= 0.
     */
    public val shift: Int,
) {
    init {
        require(shift >= 0) { """shift has to be >= 0 but was $shift""" }
    }

    /**
     * The raw code used by Discord.
     */
    public val code: DiscordBitSet
        get() = EmptyBitSet().also { it[shift] = true }

    /**
     * Returns an instance of [Intents] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: Intent): Intents = Intents(this.code + flag.code)

    /**
     * Returns an instance of [Intents] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: Intents): Intents = Intents(this.code + flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is Intent && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown) "Intent.Unknown(shift=$shift)"
            else "Intent.${this::class.simpleName}"

    /**
     * An unknown [Intent].
     *
     * This is used as a fallback for [Intent]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : Intent(shift)

    /**
     * Enables the following events:
     * - [GuildCreate]
     * - [GuildUpdate]
     * - [GuildDelete]
     * - [GuildRoleCreate]
     * - [GuildRoleUpdate]
     * - [GuildRoleDelete]
     * - [ChannelCreate]
     * - [ChannelUpdate]
     * - [ChannelDelete]
     * - [ChannelPinsUpdate]
     * - [ThreadCreate]
     * - [ThreadUpdate]
     * - [ThreadDelete]
     * - [ThreadListSync]
     * - [ThreadMemberUpdate]
     * - [ThreadMembersUpdate] (contains different data depending on which intents are used, see
     * [here](https://discord.com/developers/docs/topics/gateway-events#thread-members-update))
     */
    public object Guilds : Intent(0)

    /**
     * Enables the following events:
     * - [GuildMemberAdd]
     * - [GuildMemberUpdate]
     * - [GuildMemberRemove]
     * - [ThreadMembersUpdate] (contains different data depending on which intents are used, see
     * [here](https://discord.com/developers/docs/topics/gateway-events#thread-members-update))
     */
    @PrivilegedIntent
    public object GuildMembers : Intent(1)

    /**
     * Enables the following events:
     * - [GuildAuditLogEntryCreate]
     * - [GuildBanAdd]
     * - [GuildBanRemove]
     */
    public object GuildModeration : Intent(2)

    /**
     * Enables the following events:
     * - [GuildEmojisUpdate]
     */
    public object GuildEmojis : Intent(3)

    /**
     * Enables the following events:
     * - [GuildIntegrationsUpdate]
     * - [IntegrationCreate]
     * - [IntegrationUpdate]
     * - [IntegrationDelete]
     */
    public object GuildIntegrations : Intent(4)

    /**
     * Enables the following events:
     * - [WebhooksUpdate]
     */
    public object GuildWebhooks : Intent(5)

    /**
     * Enables the following events:
     * - [InviteCreate]
     * - [InviteDelete]
     */
    public object GuildInvites : Intent(6)

    /**
     * Enables the following events:
     * - [VoiceStateUpdate]
     */
    public object GuildVoiceStates : Intent(7)

    /**
     * Enables the following events:
     * - [PresenceUpdate]
     */
    @PrivilegedIntent
    public object GuildPresences : Intent(8)

    /**
     * Enables the following events:
     * - [MessageCreate]
     * - [MessageUpdate]
     * - [MessageDelete]
     * - [MessageDeleteBulk]
     */
    public object GuildMessages : Intent(9)

    /**
     * Enables the following events:
     * - [MessageReactionAdd]
     * - [MessageReactionRemove]
     * - [MessageReactionRemoveAll]
     * - [MessageReactionRemoveEmoji]
     */
    public object GuildMessageReactions : Intent(10)

    /**
     * Enables the following events:
     * - [TypingStart]
     */
    public object GuildMessageTyping : Intent(11)

    /**
     * Enables the following events:
     * - [MessageCreate]
     * - [MessageUpdate]
     * - [MessageDelete]
     * - [ChannelPinsUpdate]
     */
    public object DirectMessages : Intent(12)

    /**
     * Enables the following events:
     * - [MessageReactionAdd]
     * - [MessageReactionRemove]
     * - [MessageReactionRemoveAll]
     * - [MessageReactionRemoveEmoji]
     */
    public object DirectMessagesReactions : Intent(13)

    /**
     * Enables the following events:
     * - [TypingStart]
     */
    public object DirectMessageTyping : Intent(14)

    /**
     * [MessageContent] is a unique [privileged intent][PrivilegedIntent] that isn't directly
     * associated with any Gateway [event][Event]s. Instead, access to [MessageContent] permits your
     * app to receive message content data across the APIs.
     *
     * For example, the [content][dev.kord.common.entity.DiscordMessage.content],
     * [embeds][dev.kord.common.entity.DiscordMessage.embeds],
     * [attachments][dev.kord.common.entity.DiscordMessage.attachments], and
     * [components][dev.kord.common.entity.DiscordMessage.components] fields in [message
     * objects][dev.kord.common.entity.DiscordMessage] all contain message content and therefore
     * require this intent.
     *
     * Apps **without** this intent will receive empty values in fields that contain user-inputted
     * content with a few exceptions:
     * - Content in messages that an app sends
     * - Content in DMs with the app
     * - Content in which the app is mentioned
     * - Content of the message a message context menu command is used on
     */
    @PrivilegedIntent
    public object MessageContent : Intent(15)

    /**
     * Enables the following events:
     * - [GuildScheduledEventCreate]
     * - [GuildScheduledEventUpdate]
     * - [GuildScheduledEventDelete]
     * - [GuildScheduledEventUserAdd]
     * - [GuildScheduledEventUserRemove]
     */
    public object GuildScheduledEvents : Intent(16)

    /**
     * Enables the following events:
     * - [AutoModerationRuleCreate]
     * - [AutoModerationRuleUpdate]
     * - [AutoModerationRuleDelete]
     */
    public object AutoModerationConfiguration : Intent(20)

    /**
     * Enables the following events:
     * - [AutoModerationActionExecution]
     */
    public object AutoModerationExecution : Intent(21)

    public companion object {
        /**
         * A [List] of all known [Intent]s.
         */
        @OptIn(PrivilegedIntent::class)
        public val entries: List<Intent> by lazy(mode = PUBLICATION) {
            listOf(
                Guilds,
                GuildMembers,
                GuildModeration,
                GuildEmojis,
                GuildIntegrations,
                GuildWebhooks,
                GuildInvites,
                GuildVoiceStates,
                GuildPresences,
                GuildMessages,
                GuildMessageReactions,
                GuildMessageTyping,
                DirectMessages,
                DirectMessagesReactions,
                DirectMessageTyping,
                MessageContent,
                GuildScheduledEvents,
                AutoModerationConfiguration,
                AutoModerationExecution,
            )
        }


        /**
         * Returns an instance of [Intent] with [Intent.shift] equal to the specified [shift].
         *
         * @throws IllegalArgumentException if [shift] is not >= 0.
         */
        @OptIn(PrivilegedIntent::class)
        public fun fromShift(shift: Int): Intent = when (shift) {
            0 -> Guilds
            1 -> GuildMembers
            2 -> GuildModeration
            3 -> GuildEmojis
            4 -> GuildIntegrations
            5 -> GuildWebhooks
            6 -> GuildInvites
            7 -> GuildVoiceStates
            8 -> GuildPresences
            9 -> GuildMessages
            10 -> GuildMessageReactions
            11 -> GuildMessageTyping
            12 -> DirectMessages
            13 -> DirectMessagesReactions
            14 -> DirectMessageTyping
            15 -> MessageContent
            16 -> GuildScheduledEvents
            20 -> AutoModerationConfiguration
            21 -> AutoModerationExecution
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [Intent]s.
 *
 * ## Creating an instance of [Intents]
 *
 * You can create an instance of [Intents] using the following methods:
 * ```kotlin
 * // from individual Intents
 * val intents1 = Intents(Intent.Guilds, Intent.GuildMembers)
 *
 * // from an Iterable
 * val iterable: Iterable<Intent> = TODO()
 * val intents2 = Intents(iterable)
 *
 * // using a builder
 * val intents3 = Intents {
 *     +intents2
 *     +Intent.Guilds
 *     -Intent.GuildMembers
 * }
 * ```
 *
 * ## Modifying an existing instance of [Intents]
 *
 * You can create a modified copy of an existing instance of [Intents] using the [copy] method:
 * ```kotlin
 * intents.copy {
 *     +Intent.Guilds
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [Intents] objects can use `+`/`-` operators:
 * ```kotlin
 * val intents1 = intents + Intent.Guilds
 * val intents2 = intents - Intent.GuildMembers
 * val intents3 = intents1 + intents2
 * ```
 *
 * ## Checking for [Intent]s
 *
 * You can use the [contains] operator to check whether an instance of [Intents] contains specific
 * [Intent]s:
 * ```kotlin
 * val hasIntent = Intent.Guilds in intents
 * val hasIntents = Intents(Intent.Guilds, Intent.GuildMembers) in intents
 * ```
 *
 * ## Unknown [Intent]s
 *
 * Whenever [Intent]s haven't been added to Kord yet, they will be deserialized as instances of
 * [Intent.Unknown].
 *
 * You can also use [Intent.fromShift] to check for [unknown][Intent.Unknown] [Intent]s.
 * ```kotlin
 * val hasUnknownIntent = Intent.fromShift(23) in intents
 * ```
 *
 * @see Intent
 * @see Intents.Builder
 */
@JvmInline
@Serializable
public value class Intents internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: DiscordBitSet,
) {
    /**
     * A [Set] of all [Intent]s contained in this instance of [Intents].
     */
    public val values: Set<Intent>
        get() = buildSet {
            for (shift in 0..<code.size) {
                if (code[shift]) add(Intent.fromShift(shift))
            }
        }

    /**
     * Checks if this instance of [Intents] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: Intent): Boolean = flag.code in this.code

    /**
     * Checks if this instance of [Intents] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: Intents): Boolean = flags.code in this.code

    /**
     * Returns an instance of [Intents] that has all bits set that are set in `this` and [flag].
     */
    public operator fun plus(flag: Intent): Intents = Intents(this.code + flag.code)

    /**
     * Returns an instance of [Intents] that has all bits set that are set in `this` and [flags].
     */
    public operator fun plus(flags: Intents): Intents = Intents(this.code + flags.code)

    /**
     * Returns an instance of [Intents] that has all bits set that are set in `this` except the bits
     * that are set in [flag].
     */
    public operator fun minus(flag: Intent): Intents = Intents(this.code - flag.code)

    /**
     * Returns an instance of [Intents] that has all bits set that are set in `this` except the bits
     * that are set in [flags].
     */
    public operator fun minus(flags: Intents): Intents = Intents(this.code - flags.code)

    /**
     * Returns a copy of this instance of [Intents] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): Intents {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code.copy()).apply(builder).build()
    }

    @Deprecated(
        level = DeprecationLevel.HIDDEN,
        message = "Binary compatibility, keep for some releases.",
    )
    public inline fun copy0(builder: Builder.() -> Unit): Intents {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return copy(builder)
    }

    override fun toString(): String = "Intents(values=$values)"

    public class Builder(
        private val code: DiscordBitSet = EmptyBitSet(),
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [Intent].
         */
        public operator fun Intent.unaryPlus() {
            this@Builder.code.add(this.code)
        }

        /**
         * Sets all bits in the [Builder] that are set in this [Intents].
         */
        public operator fun Intents.unaryPlus() {
            this@Builder.code.add(this.code)
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [Intent].
         */
        public operator fun Intent.unaryMinus() {
            this@Builder.code.remove(this.code)
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [Intents].
         */
        public operator fun Intents.unaryMinus() {
            this@Builder.code.remove(this.code)
        }

        /**
         * Returns an instance of [Intents] that has all bits set that are currently set in this
         * [Builder].
         */
        public fun build(): Intents = Intents(code.copy())
    }
}

/**
 * Returns an instance of [Intents] built with [Intents.Builder].
 */
public inline fun Intents(builder: Intents.Builder.() -> Unit = {}): Intents {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return Intents.Builder().apply(builder).build()
}

@Suppress(names = arrayOf("FunctionName"))
@Deprecated(
    level = DeprecationLevel.HIDDEN,
    message = "Binary compatibility, keep for some releases.",
)
public inline fun Intents0(builder: Intents.Builder.() -> Unit = {}): Intents {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return Intents(builder)
}

/**
 * Returns an instance of [Intents] that has all bits set that are set in any element of [flags].
 */
public fun Intents(vararg flags: Intent): Intents = Intents {
    flags.forEach { +it }
}

/**
 * Returns an instance of [Intents] that has all bits set that are set in any element of [flags].
 */
public fun Intents(flags: Iterable<Intent>): Intents = Intents {
    flags.forEach { +it }
}

/**
 * Returns an instance of [Intents] that has all bits set that are set in any element of [flags].
 */
@JvmName("Intents0")
public fun Intents(flags: Iterable<Intents>): Intents = Intents {
    flags.forEach { +it }
}
