package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
        val id: String,
        val channelId: String,
        val guildId: String?,
        val author: User,
        val member: PartialGuildMember?,
        val content: String,
        val timestamp: String,
        val editedTimestamp: String?,
        val tts: Boolean,
        val mentionEveryone: Boolean,
        val mentions: List<OptionallyMemberUser>,
        val mentionRoles: List<Role>,
        val attachments: List<Attachment>,
        val embeds: List<Embed>,
        val reactions: List<Reaction>?,
        val nonce: String?,
        val pinned: Boolean,
        val webhookId: String?,
        val type: Int,
        val activity: MessageActivity?,
        val application: MessageApplication?
)

@Serializable
data class Attachment(
        val id: String,
        val fileName: String,
        val size: Int,
        val url: String,
        val proxyUrl: String,
        val height: Int?,
        val width: Int?
)

@Serializable
data class Embed(
        val title: String?,
        val type: String?,
        val description: String?,
        val url: String?,
        val timestamp: String,
        val color: Int,
        val footer: Footer?,
        val image: Image?,
        val thumbnail: Thumbnail?,
        val video: Video?,
        val provider: Provider?,
        val author: Author?,
        val fields: List<Field>?
) {
    @Serializable
    data class Footer(
            val text: String,
            @SerialName("icon_url")
            val iconUrl: String?,
            @SerialName("proxy_icon_url")
            val proxyIconUrl: String?
    )

    @Serializable
    data class Image(
            val url: String?,
            @SerialName("proxy_url")
            val proxyUrl: String?,
            val height: Int?,
            val width: Int?
    )

    @Serializable
    data class Thumbnail(
            val url: String?,
            @SerialName("proxy_url")
            val proxyUrl: String?,
            val height: Int?,
            val width: Int?
    )

    @Serializable
    data class Video(val url: String?, val height: Int?, val width: Int?)

    @Serializable
    data class Provider(val name: String?, val url: String?)

    @Serializable
    data class Author(
            val name: String?,
            val url: String?,
            @SerialName("icon_url")
            val iconUrl: String?,
            @SerialName("proxy_icon_url")
            val proxyIconUrl: String?
    )

    @Serializable
    data class Field(val name: String, val value: String, val inline: Boolean?)
}

@Serializable
data class Reaction(
        val count: Int,
        val me: Boolean,
        val emoji: Emoji
)

@Serializable
data class MessageActivity(val type: Int, @SerialName("party_id") val partyId: String?)

@Serializable
data class MessageApplication(
        val id: String,
        @SerialName("cover_image")
        val coverImage: String?,
        val description: String,
        val icon: String,
        val name: String
)

@Serializable
data class DeletedMessage(
        val id: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("guild_id")
        val guildId: String?
)

@Serializable
data class BulkDeleteData(
        val ids: List<String>,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("guild_id")
        val guildId: String?
)

@Serializable
data class MessageReaction(
        @SerialName("user_id")
        val userId: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("message_id")
        val messageId: String,
        @SerialName("guild_id")
        val guildId: String?,
        val emoji: PartialEmoji
)

@Serializable
data class PartialEmoji(
        val id: String,
        val name: String
)

@Serializable
data class AllRemovedMessageReactions(
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("message_id")
        val messageId: String,
        @SerialName("guild_id")
        val guildId: String?
)