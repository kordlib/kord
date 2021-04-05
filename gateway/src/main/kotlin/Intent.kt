package dev.kord.gateway

import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.RequiresOptIn.Level
import kotlin.contracts.ExperimentalContracts
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
annotation class PrivilegedIntent

/**
 * Values that enable a group of events as [defined by Discord](https://github.com/discord/discord-api-docs/blob/feature/gateway-intents/docs/topics/Gateway.md#gateway-intents).
 */
sealed class Intent(val code: DiscordBitSet) {
    constructor(vararg code: Long) : this(DiscordBitSet(code))


    /**
     * Enables the following events:
     * - [GuildCreate]
     * - [GuildDelete]
     * - [GuildRoleCreate]
     * - [GuildRoleUpdate]
     * - [GuildRoleDelete]
     * - [ChannelCreate]
     * - [ChannelUpdate]
     * - [ChannelDelete]
     * - [ChannelPinsUpdate]
     */
    object Guilds : Intent(1 shl 0)

    /**
     * Enables the following events:
     * - [GuildMemberAdd]
     * - [GuildMemberUpdate]
     * - [GuildMemberRemove]
     */
    @PrivilegedIntent
    object GuildMembers : Intent(1 shl 1)

    /**
     * Enables the following events:
     * - [GuildBanAdd]
     * - [GuildBanRemove]
     */
    object GuildBans : Intent(1 shl 2)

    /**
     * Enables the following events:
     * - [GuildEmojisUpdate]
     */
    object GuildEmojis : Intent(1 shl 3)

    /**
     * Enables the following events:
     * - [GuildIntegrationsUpdate]
     */
    object GuildIntegrations : Intent(1 shl 4)

    /**
     * Enables the following events:
     * - [WebhooksUpdate]
     */
    object GuildWebhooks : Intent(1 shl 5)

    /**
     * Enables the following events:
     * - INVITE_CREATE
     * - INVITE_DELETE
     */
    object GuildInvites : Intent(1 shl 6)

    /**
     * Enables the following events:
     * - [VoiceStateUpdate]
     */
    object GuildVoiceStates : Intent(1 shl 7)

    /**
     * Enables the following events:
     * - [PresenceUpdate]
     */
    @PrivilegedIntent
    object GuildPresences : Intent(1 shl 8)

    /**
     * Enables the following events:
     * - [MessageCreate]
     * - [MessageUpdate]
     * - [MessageDelete]
     * - [MessageDeleteBulk]
     */
    object GuildMessages : Intent(1 shl 9)

    /**
     * Enables the following events:
     * - [MessageReactionAdd]
     * - [MessageReactionRemove]
     * - [MessageReactionRemoveAll]
     * - MESSAGE_REACTION_REMOVE_EMOJI
     */
    object GuildMessageReactions : Intent(1 shl 10)

    /**
     * Enables the following events:
     * - [TypingStart]
     */
    object GuildMessageTyping : Intent(1 shl 11)

    /**
     * Enables the following events:
     * - [ChannelCreate]
     * - [ChannelDelete]
     * - [MessageUpdate]
     * - [MessageDelete]
     */
    object DirectMessages : Intent(1 shl 12)

    /**
     * Enables the following events:
     * - [MessageReactionAdd]
     * - [MessageReactionRemove]
     * - [MessageReactionRemoveAll]
     * - MESSAGE_REACTION_REMOVE_EMOJI
     */
    object DirectMessagesReactions : Intent(1 shl 13)

    /**
     * Enables the following events:
     * - [TypingStart]
     */
    object DirectMessageTyping : Intent(1 shl 14)
    companion object {
        @OptIn(PrivilegedIntent::class)
        val values = setOf(
            DirectMessageTyping,
            GuildIntegrations,
            GuildEmojis,
            DirectMessageTyping,
            DirectMessages,
            DirectMessagesReactions,
            GuildBans,
            Guilds,
            GuildVoiceStates,
            GuildMessages,
            GuildMessageReactions,
            GuildWebhooks,
            GuildInvites,
            GuildPresences,
            GuildMembers
        )
    }
}

@OptIn(ExperimentalContracts::class)
inline fun Intents(builder: Intents.IntentsBuilder.() -> Unit = {}): Intents {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return Intents.IntentsBuilder().apply(builder).flags()
}

fun Intents(vararg intents: Intents) = Intents {
    intents.forEach { +it }
}

fun Intents(vararg intents: Intent) = Intents {
    intents.forEach { +it }
}

fun Intents(intents: Iterable<Intents>) = Intents {
    intents.forEach { +it }
}

fun Intents(value: String) = Intents(DiscordBitSet(value))

@JvmName("IntentsWithIterable")
fun Intents(intents: Iterable<Intent>) = Intents {
    intents.forEach { +it }
}
/**
 * A set of [intents][Intent] to be used while [identifying][Identify] a [Gateway] connection to communicate the events the client wishes to receive.
 */
@Serializable(with = IntentsSerializer::class)
data class Intents internal constructor(val code: DiscordBitSet) {
    /**
     *  Returns this [Intents] as a [Set] of [Intent]
     */
    @OptIn(PrivilegedIntent::class)
    val values = Intent.values.filter { it.code in code }

    operator fun contains(intent: Intent) = intent.code in code

    /**
     * Returns an [Intents] that added the [intent] to this [code].
     */
    operator fun plus(intent: Intent): Intents = Intents(code + intent.code)

    /**
     * Returns an [Intents] that removed the [intent] from this [code].
     */
    operator fun minus(intent: Intent): Intents = Intents(code - intent.code)


    operator fun contains(intent: Intents) = intent.code in code

    /**
     * Returns an [Intents] that added the [intent] to this [code].
     */
    operator fun plus(intent: Intents): Intents = Intents(code + intent.code)

    /**
     * Returns an [Intents] that removed the [intent] from this [code].
     */
    operator fun minus(intent: Intents): Intents = Intents(code - intent.code)


    /**
     * copy this [Intents] and apply the [block] to it.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun copy(block: IntentsBuilder.() -> Unit): Intents {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val builder = IntentsBuilder(code)
        builder.apply(block)
        return builder.flags()
    }

    companion object {

        @PrivilegedIntent
        val all: Intents
            get() = Intents(Intent.values)

        @OptIn(PrivilegedIntent::class)
        val nonPrivileged: Intents
            get() = Intents {
                +all
                -Intent.GuildPresences
                -Intent.GuildMembers
            }

        val none: Intents = Intents()

    }



    class IntentsBuilder(internal var code: DiscordBitSet = EmptyBitSet()) {
        operator fun Intents.unaryPlus() {
            this@IntentsBuilder.code.add(code)
        }

        operator fun Intent.unaryPlus() {
            this@IntentsBuilder.code.add(code)
        }

        operator fun Intent.unaryMinus() {
            this@IntentsBuilder.code.remove(code)

        }

        fun flags() = Intents(code)
    }

}

object IntentsSerializer : KSerializer<Intents> {
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


