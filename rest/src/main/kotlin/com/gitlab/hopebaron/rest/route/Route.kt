package com.gitlab.hopebaron.rest.route

import com.gitlab.hopebaron.common.entity.Channel
import com.gitlab.hopebaron.common.entity.Message
import com.gitlab.hopebaron.rest.json.AuditLogResponse
import com.gitlab.hopebaron.rest.json.GatewayResponse
import io.ktor.http.HttpMethod
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.internal.ArrayListSerializer

internal sealed class Route<T>(
        val method: HttpMethod,
        val path: String,
        val strategy: DeserializationStrategy<T>
) {

    object GatewayGet
        : Route<GatewayResponse>(HttpMethod.Get, "/gateway", GatewayResponse.serializer())

    object GatewayBotGet
        : Route<GatewayResponse>(HttpMethod.Get, "/gateway/bot", GatewayResponse.serializer())

    object AuditLogGet
        : Route<AuditLogResponse>(HttpMethod.Get, "/guilds/$GuildId/audit-logs", AuditLogResponse.serializer())

    object ChannelGet
        : Route<Channel>(HttpMethod.Get, "/channels/$ChannelId", Channel.serializer())

    object ChannelPut
        : Route<Channel>(HttpMethod.Put, "/channels/$ChannelId", Channel.serializer())

    object ChannelPatch
        : Route<Channel>(HttpMethod.Patch, "/channels/$ChannelId", Channel.serializer())

    object ChannelDelete
        : Route<Channel>(HttpMethod.Delete, "/channels/$ChannelId", Channel.serializer())

    object MessagesGet
        : Route<List<Message>>(HttpMethod.Get, "/channels/$ChannelId/messages", ArrayListSerializer(Message.serializer()))

    object MessageGet
        : Route<Message>(HttpMethod.Get, "/channels/$ChannelId/messages/$MessageId", Message.serializer())

    object MessageCreate
        : Route<Message>(HttpMethod.Post, "/channels/$ChannelId/messages", Message.serializer())

    companion object {
        const val baseUrl = "https://discordapp.com/api/v6"
    }

    internal open class Key(val identifier: String) {
        override fun toString(): String = identifier
    }

    object GuildId : Key("{guild.id}")
    object ChannelId : Key("{channel.id}")
    object MessageId : Key("{message.id}")

}


