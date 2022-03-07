package dev.kord.gateway

import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet
import dev.kord.gateway.Intent.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.RequiresOptIn.Level
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Some intents are defined as "Privileged" due to the sensitive nature of the data and cannot be used by Kord without enabling them.
 *
 * See [the official documentation](https://discord.com/developers/docs/topics/gateway#privileged-intents) for more info on
 * how to enable these.
 */
@RequiresOptIn(
    """
    Some intents are defined as "Privileged" due to the sensitive nature of the data and cannot be used by Kord without enabling them.
    
    See https://discord.com/developers/docs/topics/gateway#privileged-intents for more info on how to enable these.
""", Level.ERROR
)
public annotation class PrivilegedIntent

/**
 * Values that enable a group of events as
 * [defined by Discord](https://discord.com/developers/docs/topics/gateway#gateway-intents).
 */
public sealed class Intent(public val code: DiscordBitSet) {
    protected constructor(vararg code: Long) : this(DiscordBitSet(code))


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
    public object Guilds : Intent(1 shl 0)

    /**
     * Enables the following events:
     * - [GuildMemberAdd]
     * - [GuildMemberUpdate]
     * - [GuildMemberRemove]
     * - [ThreadMembersUpdate] (contains different data depending on which intents are used, see
     * [here](https://discord.com/developers/docs/topics/gateway#thread-members-update))
     */
    @PrivilegedIntent
    public object GuildMembers : Intent(1 shl 1)

    /**
     * Enables the following events:
     * - [GuildBanAdd]
     * - [GuildBanRemove]
     */
    public object GuildBans : Intent(1 shl 2)

    /**
     * Enables the following events:
     * - [GuildEmojisUpdate]
     */
    public object GuildEmojis : Intent(1 shl 3)

    /**
     * Enables the following events:
     * - [GuildIntegrationsUpdate]
     */
    public object GuildIntegrations : Intent(1 shl 4)

    /**
     * Enables the following events:
     * - [WebhooksUpdate]
     */
    public object GuildWebhooks : Intent(1 shl 5)

    /**
     * Enables the following events:
     * - [InviteCreate]
     * - [InviteDelete]
     */
    public object GuildInvites : Intent(1 shl 6)

    /**
     * Enables the following events:
     * - [VoiceStateUpdate]
     */
    public object GuildVoiceStates : Intent(1 shl 7)

    /**
     * Enables the following events:
     * - [PresenceUpdate]
     */
    @PrivilegedIntent
    public object GuildPresences : Intent(1 shl 8)

    /**
     * Enables the following events:
     * - [MessageCreate]
     * - [MessageUpdate]
     * - [MessageDelete]
     * - [MessageDeleteBulk]
     */
    public object GuildMessages : Intent(1 shl 9)

    /**
     * Enables the following events:
     * - [MessageReactionAdd]
     * - [MessageReactionRemove]
     * - [MessageReactionRemoveAll]
     * - [MessageReactionRemoveEmoji]
     */
    public object GuildMessageReactions : Intent(1 shl 10)

    /**
     * Enables the following events:
     * - [TypingStart]
     */
    public object GuildMessageTyping : Intent(1 shl 11)

    /**
     * Enables the following events:
     * - [MessageCreate]
     * - [MessageUpdate]
     * - [MessageDelete]
     * - [ChannelPinsUpdate]
     */
    public object DirectMessages : Intent(1 shl 12)

    /**
     * Enables the following events:
     * - [MessageReactionAdd]
     * - [MessageReactionRemove]
     * - [MessageReactionRemoveAll]
     * - [MessageReactionRemoveEmoji]
     */
    public object DirectMessagesReactions : Intent(1 shl 13)

    /**
     * Enables the following events:
     * - [TypingStart]
     */
    public object DirectMessageTyping : Intent(1 shl 14)

    @PrivilegedIntent
    public object MessageContent : Intent(1 shl 15)

    /**
     * Enables the following events:
     * - [GuildScheduledEventCreate]
     * - [GuildScheduledEventUpdate]
     * - [GuildScheduledEventDelete]
     * - [GuildScheduledEventUserAdd]
     * - [GuildScheduledEventUserRemove]
     */
    public object GuildScheduledEvents : Intent(1 shl 16)


    public companion object {
        @OptIn(PrivilegedIntent::class)
        public val values: Set<Intent>
            get() = setOf(
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
            )
    }
}

public inline fun Intents(builder: Intents.IntentsBuilder.() -> Unit = {}): Intents {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return Intents.IntentsBuilder().apply(builder).flags()
}

public fun Intents(vararg intents: Intents): Intents = Intents {
    intents.forEach { +it }
}

public fun Intents(vararg intents: Intent): Intents = Intents {
    intents.forEach { +it }
}

public fun Intents(intents: Iterable<Intents>): Intents = Intents {
    intents.forEach { +it }
}

public fun Intents(value: String): Intents = Intents(DiscordBitSet(value))

@JvmName("IntentsWithIterable")
public fun Intents(intents: Iterable<Intent>): Intents = Intents {
    intents.forEach { +it }
}

/**
 * A set of [intents][Intent] to be used while [identifying][Identify] a [Gateway] connection to communicate the events the client wishes to receive.
 */
@Serializable(with = IntentsSerializer::class)
public data class Intents internal constructor(val code: DiscordBitSet) {
    /**
     *  Returns this [Intents] as a [Set] of [Intent]
     */
    val values: Set<Intent> = Intent.values.filter { it.code in code }.toSet()

    public operator fun contains(intent: Intent): Boolean = intent.code in code

    /**
     * Returns an [Intents] that added the [intent] to this [code].
     */
    public operator fun plus(intent: Intent): Intents = Intents(code + intent.code)

    /**
     * Returns an [Intents] that removed the [intent] from this [code].
     */
    public operator fun minus(intent: Intent): Intents = Intents(code - intent.code)


    public operator fun contains(intent: Intents): Boolean = intent.code in code

    /**
     * Returns an [Intents] that added the [intent] to this [code].
     */
    public operator fun plus(intent: Intents): Intents = Intents(code + intent.code)

    /**
     * Returns an [Intents] that removed the [intent] from this [code].
     */
    public operator fun minus(intent: Intents): Intents = Intents(code - intent.code)


    /**
     * copy this [Intents] and apply the [block] to it.
     */
    public inline fun copy(block: IntentsBuilder.() -> Unit): Intents {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val builder = IntentsBuilder(code)
        builder.apply(block)
        return builder.flags()
    }

    public companion object {

        @PrivilegedIntent
        public val all: Intents
            get() = Intents(Intent.values)

        @PrivilegedIntent
        public val privileged: Intents
            get() = Intents(GuildPresences, GuildMembers, MessageContent)

        @OptIn(PrivilegedIntent::class)
        public val nonPrivileged: Intents
            get() = Intents {
                +all
                -privileged
            }

        public val none: Intents = Intents()

    }


    public class IntentsBuilder(internal var code: DiscordBitSet = EmptyBitSet()) {
        public operator fun Intents.unaryPlus() {
            this@IntentsBuilder.code.add(code)
        }

        public operator fun Intent.unaryPlus() {
            this@IntentsBuilder.code.add(code)
        }

        public operator fun Intent.unaryMinus() {
            this@IntentsBuilder.code.remove(code)
        }

        public operator fun Intents.unaryMinus() {
            this@IntentsBuilder.code.remove(code)
        }

        public fun flags(): Intents = Intents(code)
    }

}

public object IntentsSerializer : KSerializer<Intents> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("intents", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Intents {
        val intents = decoder.decodeString()
        return Intents(intents)
    }


    override fun serialize(encoder: Encoder, value: Intents) {
        val intents = value.code
        encoder.encodeString(intents.value)

    }
}
