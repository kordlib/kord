package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.MessageReferenceData

class MessageReference(val data: MessageReferenceData, override val kord: Kord) : KordObject {

    val guild: GuildBehavior? get() = data.guildId.value?.let { GuildBehavior(it, kord) }

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(data.channelId.value!!, kord)

    val message: MessageBehavior? get() = data.id.value?.let { MessageBehavior(channel.id, it, kord) }


}