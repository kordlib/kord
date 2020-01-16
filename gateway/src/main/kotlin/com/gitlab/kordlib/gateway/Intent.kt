package com.gitlab.kordlib.gateway

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

enum class Intent(val code: Int) {
    Guilds(1 shl 0),
    GuildMembers(1 shl 1),
    GuildBans(1 shl 2),
    GuildEmojis(1 shl 3),
    GuildIntegrations(1 shl 4),
    GuildWebhooks(1 shl 5),
    GuildInvites(1 shl 6),
    GuildVoiceStates(1 shl 7),
    GuildPresences(1 shl 8),
    GuildMessages(1 shl 9),
    GuildMessageReactions(1 shl 10),
    GuildMessageTyping(1 shl 11),
    DirectMessages(1 shl 12),
    DirectMessagesReactions(1 shl 13),
    DirectMessageTyping(1 shl 14)
}

@Serializable(with = IntentsSerializer::class)
data class Intents internal constructor(val code: Int) {

    val intents = Intent.values().filter { code and it.code != 0 }

    operator fun contains(intent: Intent) = intent in intents

    operator fun plus(intents: Intents): Intents = when {
        code and intents.code == intents.code -> this
        else -> Intents(this.code or intents.code)
    }

    operator fun minus(intent: Intent): Intents = when {
        code and intent.code == intent.code -> Intents(code xor intent.code)
        else -> this
    }

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
