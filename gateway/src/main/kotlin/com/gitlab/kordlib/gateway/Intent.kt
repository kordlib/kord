package com.gitlab.kordlib.gateway

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

/**
 * Values that enable a group of events as [defined by Discord](https://github.com/discordapp/discord-api-docs/blob/feature/gateway-intents/docs/topics/Gateway.md#gateway-intents).
 */
enum class Intent(val code: Int) {
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
    Guilds(1 shl 0),

    /**
     * Enables the following events:
     * - [GuildMemberAdd]
     * - [GuildMemberUpdate]
     * - [GuildMemberRemove]
     */
    GuildMembers(1 shl 1),

    /**
     * Enables the following events:
     * - [GuildBanAdd]
     * - [GuildBanRemove]
     */
    GuildBans(1 shl 2),

    /**
     * Enables the following events:
     * - [GuildEmojisUpdate]
     */
    GuildEmojis(1 shl 3),

    /**
     * Enables the following events:
     * - GUILD_INTEGRATIONS_UPDATE
     */
    GuildIntegrations(1 shl 4),

    /**
     * Enables the following events:
     * - [WebhooksUpdate]
     */
    GuildWebhooks(1 shl 5),

    /**
     * Enables the following events:
     * - INVITE_CREATE
     * - INVITE_DELETE
     */
    GuildInvites(1 shl 6),

    /**
     * Enables the following events:
     * - [VoiceStateUpdate]
     */
    GuildVoiceStates(1 shl 7),

    /**
     * Enables the following events:
     * - [PresenceUpdate]
     */
    GuildPresences(1 shl 8),

    /**
     * Enables the following events:
     * - [MessageCreate]
     * - [MessageUpdate]
     * - [MessageDelete]
     */
    GuildMessages(1 shl 9),

    /**
     * Enables the following events:
     * - [MessageReactionAdd]
     * - [MessageReactionRemove]
     * - [MessageReactionRemoveAll]
     * - MESSAGE_REACTION_REMOVE_EMOJI
     */
    GuildMessageReactions(1 shl 10),

    /**
     * Enables the following events:
     * - [TypingStart]
     */
    GuildMessageTyping(1 shl 11),

    /**
     * Enables the following events:
     * - [ChannelCreate]
     * - [ChannelDelete]
     * - [MessageUpdate]
     * - [MessageDelete]
     */
    DirectMessages(1 shl 12),

    /**
     * Enables the following events:
     * - [MessageReactionAdd]
     * - [MessageReactionRemove]
     * - [MessageReactionRemoveAll]
     * - MESSAGE_REACTION_REMOVE_EMOJI
     */
    DirectMessagesReactions(1 shl 13),

    /**
     * Enables the following events:
     * - [TypingStart]
     */
    DirectMessageTyping(1 shl 14)
}

/**
 * A set of [intents][Intent] to be used while [identifying][Identify] a [Gateway] connection to communicate the events the client wishes to receive.
 */
@Serializable(with = IntentsSerializer::class)
data class Intents internal constructor(val code: Int) {

    val intents = Intent.values().filter { code and it.code != 0 }.toSet()

    operator fun contains(intent: Intent) = intent in intents

    /**
     * Returns an [Intents] that added the [intent] to this [code].
     */
    operator fun plus(intent: Intent): Intents = when {
        code and intent.code == intent.code -> this
        else -> Intents(this.code or intent.code)
    }

    /**
     * Returns an [Intents] that removed the [intent] from this [code].
     */
    operator fun minus(intent: Intent): Intents = when {
        code and intent.code == intent.code -> Intents(code xor intent.code)
        else -> this
    }

    /**
     * copy this [Intents] and apply the [block] to it.
     */
    inline fun copy(block: IntentsBuilder.() -> Unit): Intents {
        val builder = IntentsBuilder(code)
        builder.apply(block)
        return builder.flags()
    }

    companion object {

        val all: Intents
            get() = invoke {
                Intent.values().forEach { +it }
            }

        inline operator fun invoke(builder: IntentsBuilder.() -> Unit): Intents {
            return IntentsBuilder().apply(builder).flags()
        }
    }

    class IntentsBuilder(internal var code: Int = 0) {
        operator fun Intent.unaryPlus() {
            this@IntentsBuilder.code = this@IntentsBuilder.code or code
        }

        operator fun Intent.unaryMinus() {
            if (this@IntentsBuilder.code and code == code) {
                this@IntentsBuilder.code = this@IntentsBuilder.code xor code
            }
        }

        fun flags() = Intents(code)
    }

}

@Serializer(forClass = Intents::class)
object IntentsSerializer : KSerializer<Intents> {
    override val descriptor: SerialDescriptor = IntDescriptor

    override fun deserialize(decoder: Decoder): Intents {
        val flags = decoder.decodeInt()
        return Intents(flags)
    }

    override fun serialize(encoder: Encoder, obj: Intents) {
        encoder.encodeInt(obj.code)
    }
}
