// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.gateway

import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet
import dev.kord.common.`annotation`.KordUnsafe
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Convenience container of multiple [Intents][Intent] which can be combined into one.
 *
 * ## Creating a collection of message flags
 * You can create an [Intents] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = Intents(Intent.Guilds, Intent.GuildMembers)
 * // From an iterable
 * val flags2 = Intents(listOf(Intent.Guilds, Intent.GuildMembers))
 * // Using a builder
 * val flags3 = Intents {
 *  +Intent.Guilds
 *  -Intent.GuildMembers
 * }
 * ```
 *
 * ## Modifying existing intents
 * You can crate a modified copy of a [Intents] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +Intent.Guilds
 * }
 * ```
 *
 * ## Mathematical operators
 * All [Intents] objects can use +/- operators
 *
 * ```kotlin
 * val flags = Intents(Intent.Guilds)
 * val flags2 = flags + Intent.GuildMembers
 * val otherFlags = flags - Intent.GuildMembers
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for an intent
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = Intent.Guilds in gateway.intents
 * val hasFlags = Intent(Intent.GuildMembers, Intent.GuildMembers) in gateway.intents
 * ```
 *
 * ## Unknown intent
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [Intent.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = Intent.Unknown(1 shl 69) in gateway.intents
 * ```
 * @see Intent
 * @see Intents.Builder
 * @property code numeric value of all [Intents]s
 */
@Serializable(with = Intents.Serializer::class)
public class Intents(
    public val code: DiscordBitSet = EmptyBitSet(),
) {
    public val values: Set<Intent>
        get() = Intent.entries.filter { it in this }.toSet()

    public operator fun contains(flag: Intent): Boolean = flag.code in this.code

    public operator fun contains(flags: Intents): Boolean = flags.code in this.code

    public operator fun plus(flag: Intent): Intents = Intents(this.code + flag.code)

    public operator fun plus(flags: Intents): Intents = Intents(this.code + flags.code)

    public operator fun minus(flag: Intent): Intents = Intents(this.code - flag.code)

    public operator fun minus(flags: Intents): Intents = Intents(this.code - flags.code)

    public inline fun copy(block: Builder.() -> Unit): Intents {
        contract { callsInPlace(block, EXACTLY_ONCE) }
        return Builder(code).apply(block).flags()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is Intents && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "Intents(values=$values)"

    /**
     * @suppress
     */
    @Deprecated(
        message = "Intents is no longer a data class.",
        replaceWith = ReplaceWith(expression = "this.code", imports = arrayOf()),
    )
    public operator fun component1(): DiscordBitSet = code

    /**
     * @suppress
     */
    @Suppress(names = arrayOf("DeprecatedCallableAddReplaceWith"))
    @Deprecated(message = "Intents is no longer a data class. Deprecated without a replacement.")
    public fun copy(code: DiscordBitSet = this.code): Intents = Intents(code)

    public class Builder(
        private var code: DiscordBitSet = EmptyBitSet(),
    ) {
        public operator fun Intent.unaryPlus() {
            this@Builder.code.add(this.code)
        }

        public operator fun Intents.unaryPlus() {
            this@Builder.code.add(this.code)
        }

        public operator fun Intent.unaryMinus() {
            this@Builder.code.remove(this.code)
        }

        public operator fun Intents.unaryMinus() {
            this@Builder.code.remove(this.code)
        }

        public fun flags(): Intents = Intents(code)
    }

    internal object Serializer : KSerializer<Intents> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.gateway.Intents", PrimitiveKind.STRING)

        private val `delegate`: KSerializer<DiscordBitSet> = DiscordBitSet.serializer()

        override fun serialize(encoder: Encoder, `value`: Intents) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): Intents =
                Intents(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun Intents(builder: Intents.Builder.() -> Unit): Intents {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return Intents.Builder().apply(builder).flags()
}

public fun Intents(vararg flags: Intent): Intents = Intents { flags.forEach { +it } }

public fun Intents(vararg flags: Intents): Intents = Intents { flags.forEach { +it } }

public fun Intents(flags: Iterable<Intent>): Intents = Intents { flags.forEach { +it } }

@JvmName("Intents0")
public fun Intents(flags: Iterable<Intents>): Intents = Intents { flags.forEach { +it } }

/**
 * Values that enable a group of events as [defined by
 * Discord](https://discord.com/developers/docs/topics/gateway#gateway-intents).
 *
 * See [Intent]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/gateway#gateway-intents).
 */
public sealed class Intent(
    shift: Int,
) {
    /**
     * The raw code used by Discord.
     */
    public val code: DiscordBitSet = EmptyBitSet().also { it[shift] = true }

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is Intent && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()

    final override fun toString(): String = "Intent.${this::class.simpleName}(code=$code)"

    /**
     * An unknown [Intent].
     *
     * This is used as a fallback for [Intent]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
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
     * [here](https://discord.com/developers/docs/topics/gateway#thread-members-update))
     */
    public object Guilds : Intent(0)

    /**
     * Enables the following events:
     * - [GuildMemberAdd]
     * - [GuildMemberUpdate]
     * - [GuildMemberRemove]
     * - [ThreadMembersUpdate] (contains different data depending on which intents are used, see
     * [here](https://discord.com/developers/docs/topics/gateway#thread-members-update))
     */
    @PrivilegedIntent
    public object GuildMembers : Intent(1)

    /**
     * Enables the following events:
     * - [GuildAuditLogEntryCreate]
     * - [GuildBanAdd]
     * - [GuildBanRemove]
     */
    public object GuildBans : Intent(2)

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
     * associated with any
     * Gateway [event][Event]s. Instead, access to [MessageContent] permits your app to receive
     * message content data
     * across the APIs.
     *
     * For example, the [content][dev.kord.common.entity.DiscordMessage.content],
     * [embeds][dev.kord.common.entity.DiscordMessage.embeds],
     * [attachments][dev.kord.common.entity.DiscordMessage.attachments],
     * and [components][dev.kord.common.entity.DiscordMessage.components] fields in
     * [message objects][dev.kord.common.entity.DiscordMessage] all contain message content and
     * therefore require this
     * intent.
     *
     * Apps **without** this intent will receive empty values in fields that contain user-inputted
     * content with a few
     * exceptions:
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
                GuildBans,
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

    }
}
