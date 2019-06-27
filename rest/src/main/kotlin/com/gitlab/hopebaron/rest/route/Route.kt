package com.gitlab.hopebaron.rest.route

import com.gitlab.hopebaron.common.entity.Channel
import com.gitlab.hopebaron.common.entity.Message
import com.gitlab.hopebaron.common.entity.Reaction
import com.gitlab.hopebaron.rest.json.response.AuditLogResponse
import com.gitlab.hopebaron.rest.json.response.GatewayResponse
import com.gitlab.hopebaron.rest.json.response.InviteResponse
import io.ktor.http.HttpMethod
import kotlinx.serialization.Decoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.UnitDescriptor
import com.gitlab.hopebaron.common.entity.Emoji as EmojiEntity

sealed class Route<T>(
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

    object MessageCreate
        : Route<Message>(HttpMethod.Post, "/channels/$ChannelId/messages", Message.serializer())

    object MessageGet
        : Route<Message>(HttpMethod.Get, "/channels/$ChannelId/messages/$MessageId", Message.serializer())

    object MessagesGet
        : Route<List<Message>>(HttpMethod.Get, "/channels/$ChannelId/messages", ArrayListSerializer(Message.serializer()))

    object PinsGet
        : Route<List<Message>>(HttpMethod.Get, "/channels/$ChannelId/pins", ArrayListSerializer(Message.serializer()))


    object InvitesGet
        : Route<List<InviteResponse>>(HttpMethod.Get, "/channels/$ChannelId/invites", ArrayListSerializer(InviteResponse.serializer()))

    object InvitePost
        : Route<InviteResponse>(HttpMethod.Post, "/channels/$ChannelId/invites", InviteResponse.serializer())

    object ReactionPut
        : Route<Reaction>(HttpMethod.Put, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", Reaction.serializer())

    object OwnReactionDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", NoStrategy)

    object ReactionDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/$UserId", NoStrategy)

    object MessageDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId", NoStrategy)

    object PinDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/pins/$MessageId", NoStrategy)

    object AllReactionsDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/ChannelId/messages/$MessageId/reactions", NoStrategy)

    object ChannelPermissionDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/permissions/$OverwriteId", NoStrategy)

    object ReactionsGet
        : Route<List<Reaction>>(HttpMethod.Get, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji", ArrayListSerializer(Reaction.serializer()))

    object TypingIndicatorPost
        : Route<Unit>(HttpMethod.Post, "/channels/$ChannelId/typing", NoStrategy)

    object GroupDMUserDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    object GroupDMUserPut
        : Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    object EditMessagePatch
        : Route<Message>(HttpMethod.Put, "/channels/$ChannelId/messages/$MessageId", Message.serializer())

    object GuildEmojiGet
        : Route<EmojiEntity>(HttpMethod.Get, "/guilds/$GuildId/emojis/$Emoji", EmojiEntity.serializer())

    object GuildEmojisGet
        : Route<List<EmojiEntity>>(HttpMethod.Get, "/guilds/$GuildId/emojis", ArrayListSerializer(EmojiEntity.serializer()))

    object GuildEmojiDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/emojis/$Emoji", NoStrategy)

    object GuildEmojiCreate
        : Route<EmojiEntity>(HttpMethod.Post, "/guilds/$GuildId/emojis", EmojiEntity.serializer())

    object GuildEmojiPatch
        : Route<EmojiEntity>(HttpMethod.Patch, "/guilds/$GuildId/emojis", EmojiEntity.serializer())


    companion object {
        const val baseUrl = "https://discordapp.com/api/v6"
    }

    open class Key(val identifier: String) {
        override fun toString(): String = identifier
    }

    object GuildId : Key("{guild.id}")
    object ChannelId : Key("{channel.id}")
    object MessageId : Key("{message.id}")
    object Emoji : Key("{emoji}")
    object UserId : Key("{user.id}")
    object OverwriteId : Key("{overwrite.id}")

}

object NoStrategy : DeserializationStrategy<Unit> {
    override val descriptor: SerialDescriptor
        get() = UnitDescriptor

    override fun deserialize(decoder: Decoder) {}

    override fun patch(decoder: Decoder, old: Unit) {}
}
