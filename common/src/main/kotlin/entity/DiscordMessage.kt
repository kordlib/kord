package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents [a message sent in a channel within Discord](https://discord.com/developers/docs/resources/channel#message-object).
 *
 * @param id id of the message.
 * @param channelId id of the channel the message was sent in.
 * @param guildId id of the guild the message was sent in.
 * @param author The author of this message.
 *
 * The author is only a valid user in the case where the message is generated by a user or bot user.
 *
 * If the message is generated by a webhook, the author object corresponds to the webhook's
 * [DiscordWebhook.id], [DiscordWebhook.name], and [DiscordWebhook.avatar].
 * You can tell if a message is generated by a webhook by checking for the [webhookId] on the message object.
 *
 * @param member Member properties for this message's author.
 *
 * It only exists in MESSAGE_CREATE and MESSAGE_UPDATE events from text-based guild channels.
 *
 * @param content Contents of the message.
 * @param editedTimestamp When this message was edited, null if never.
 * @param tts Whether this was a text-to-speech message.
 * @param mentionEveryone Whether this message mentions everyone.
 * @param mentions Users specifically mentioned in the message.
 * @param mentionRoles Roles specifically mentioned in this message.
 * @param mentionedChannels Channels specifically mentioned in this message.
 *
 * Not all channel mentions in a message will appear in [mentionedChannels]:
 * * Only textual channels that are visible to everyone in a lurkable guild will ever be included.
 * * Only crossposted messages (via Channel Following) currently include [mentionedChannels] at all.
 *
 * If no mentions in the message meet these requirements, this field will not be sent.
 *
 * @param attachments Any attached files.
 * @param embeds Any embedded content.
 * @param reactions reactions to the message.
 * @param nonce Used for validating a message was sent.
 * @param pinned Whether this message is pinned.
 * @param webhookId If the message is generated by a webhook, this is the webhook's id.
 * @param type Type of message.
 * @param activity Sent with Rich Presence-related chat embeds.
 * @param application Sent with Rich Presence-related chat embeds.
 * @param messageReference Reference data sent with crossposted messages and replies.
 * @param flags Message flags.
 * @param stickers The stickers sent with the message (bots currently can only receive messages with stickers, not send).
 * @param referencedMessage the message associated with [messageReference].
 */
@Serializable
data class DiscordMessage(
        val id: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        val author: DiscordUser,
        val member: Optional<DiscordGuildMember> = Optional.Missing(),
        val content: String,
        val timestamp: String,
        @SerialName("edited_timestamp")
        val editedTimestamp: String?,
        val tts: Boolean,
        @SerialName("mention_everyone")
        val mentionEveryone: Boolean,
        val mentions: List<DiscordOptionallyMemberUser>,
        @SerialName("mention_roles")
        val mentionRoles: List<Snowflake>,
        @SerialName("mention_channels")
        val mentionedChannels: Optional<List<DiscordMentionedChannel>> = Optional.Missing(),
        val attachments: List<DiscordAttachment>,
        val embeds: List<DiscordEmbed>,
        val reactions: Optional<List<Reaction>> = Optional.Missing(),
        val nonce: Optional<String> = Optional.Missing(),
        val pinned: Boolean,
        @SerialName("webhook_id")
        val webhookId: OptionalSnowflake = OptionalSnowflake.Missing,
        val type: MessageType,
        val activity: Optional<MessageActivity> = Optional.Missing(),
        val application: Optional<MessageApplication> = Optional.Missing(),
        @SerialName("message_reference")
        val messageReference: Optional<DiscordMessageReference> = Optional.Missing(),
        val flags: Optional<MessageFlags> = Optional.Missing(),
        val stickers: Optional<List<DiscordMessageSticker>> = Optional.Missing(),
        @SerialName("referenced_message")
        val referencedMessage: Optional<DiscordMessage?> = Optional.Missing(),
)

/**
 * @param id id of the sticker
 * @param packId id of the pack the sticker is from
 * @param name name of the sticker
 * @param description description of the sticker
 * @param tags a comma-separated list of tags for the sticker
 * @param asset sticker asset hash
 * @param previewAsset sticker preview asset hash
 * @param formatType type of sticker format
 */
@Serializable
data class DiscordMessageSticker(
        val id: Snowflake,
        @SerialName("pack_id")
        val packId: Snowflake,
        val name: String,
        val description: String,
        val tags: Optional<String> = Optional.Missing(),
        val asset: String,
        @SerialName("preview_asset")
        val previewAsset: String?,
        @SerialName("format_type")
        val formatType: MessageStickerType,
)

@Serializable(with = MessageStickerType.Serializer::class)
sealed class MessageStickerType(val value: Int) {
    class Unknown(value: Int) : MessageStickerType(value)
    object PNG : MessageStickerType(1)
    object APNG : MessageStickerType(2)
    object LOTTIE : MessageStickerType(3)

    companion object {
        val values: Set<MessageStickerType> = setOf(PNG, APNG, LOTTIE)
    }

    internal object Serializer : KSerializer<MessageStickerType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.MessageStickerType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): MessageStickerType = when (val value = decoder.decodeInt()) {
            1 -> PNG
            2 -> APNG
            3 -> LOTTIE
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: MessageStickerType) {
            encoder.encodeInt(value.value)
        }
    }
}


/**
 * Represents [a partial message sent in a channel within Discord](https://discord.com/developers/docs/resources/channel#message-object).
 *
 * @param id id of the message.
 * @param channelId id of the channel the message was sent in.
 * @param guildId id of the guild the message was sent in.
 * @param author The author of this message.
 *
 * The author is only a valid user in the case where the message is generated by a user or bot user.
 *
 * If the message is generated by a webhook, the author object corresponds to the webhook's
 * [DiscordWebhook.id], [DiscordWebhook.name], and [DiscordWebhook.avatar].
 * You can tell if a message is generated by a webhook by checking for the [webhookId] on the message object.
 *
 * @param member Member properties for this message's author.
 *
 * It only exists in MESSAGE_CREATE and MESSAGE_UPDATE events from text-based guild channels.
 *
 * @param content Contents of the message.
 * @param editedTimestamp When this message was edited, null if never.
 * @param tts Whether this was a text-to-speech message.
 * @param mentionEveryone Whether this message mentions everyone.
 * @param mentions Users specifically mentioned in the message.
 * @param mentionRoles Roles specifically mentioned in this message.
 * @param mentionedChannels Channels specifically mentioned in this message.
 *
 * Not all channel mentions in a message will appear in [mentionedChannels]:
 * * Only textual channels that are visible to everyone in a lurkable guild will ever be included.
 * * Only crossposted messages (via Channel Following) currently include [mentionedChannels] at all.
 *
 * If no mentions in the message meet these requirements, this field will not be sent.
 *
 * @param attachments Any attached files.
 * @param embeds Any embedded content.
 * @param reactions reactions to the message.
 * @param nonce Used for validating a message was sent.
 * @param pinned Whether this message is pinned.
 * @param webhookId If the message is generated by a webhook, this is the webhook's id.
 * @param type Type of message.
 * @param activity Sent with Rich Presence-related chat embeds.
 * @param application Sent with Rich Presence-related chat embeds.
 * @param messageReference Reference data sent with crossposted messages and replies.
 * @param flags Message flags.
 * @param stickers The stickers sent with the message (bots currently can only receive messages with stickers, not send).
 * @param referencedMessage the message associated with [messageReference].
 */
@Serializable
data class DiscordPartialMessage(
        val id: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        val author: Optional<DiscordUser> = Optional.Missing(),
        val member: Optional<DiscordGuildMember> = Optional.Missing(),
        val content: Optional<String> = Optional.Missing(),
        val timestamp: Optional<String> = Optional.Missing(),
        @SerialName("edited_timestamp")
        val editedTimestamp: Optional<String?> = Optional.Missing(),
        val tts: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("mention_everyone")
        val mentionEveryone: OptionalBoolean = OptionalBoolean.Missing,
        val mentions: Optional<List<DiscordOptionallyMemberUser>> = Optional.Missing(),
        @SerialName("mention_roles")
        val mentionRoles: Optional<List<Snowflake>> = Optional.Missing(),
        @SerialName("mention_channels")
        val mentionedChannels: Optional<List<DiscordMentionedChannel>> = Optional.Missing(),
        val attachments: Optional<List<DiscordAttachment>> = Optional.Missing(),
        val embeds: Optional<List<DiscordEmbed>> = Optional.Missing(),
        val reactions: Optional<List<Reaction>> = Optional.Missing(),
        val nonce: Optional<String> = Optional.Missing(),
        val pinned: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("webhook_id")
        val webhookId: OptionalSnowflake = OptionalSnowflake.Missing,
        val type: Optional<MessageType> = Optional.Missing(),
        val activity: Optional<MessageActivity> = Optional.Missing(),
        val application: Optional<MessageApplication> = Optional.Missing(),
        @SerialName("message_reference")
        val messageReference: Optional<DiscordMessageReference> = Optional.Missing(),
        val flags: Optional<MessageFlags> = Optional.Missing(),
        val stickers: Optional<List<DiscordMessageSticker>> = Optional.Missing(),
        @SerialName("referenced_message")
        val referencedMessage: Optional<DiscordMessage?> = Optional.Missing(),
)

@Serializable
data class DiscordMessageReference(
        @SerialName("message_id")
        val id: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("channel_id")
        val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
)

/**
 * A representation of a [Discord Channel Mention structure](https://discord.com/developers/docs/resources/channel#channel-mention-object-channel-mention-structure).
 *
 * @param id The id of the channel.
 * @param guildId The id of the guild containing the channel.
 * @param type The type of channel.
 * @param name the name of the channel.
 */
@Serializable
data class DiscordMentionedChannel(
        val id: Snowflake,
        @SerialName("guild_id")
        val guildId: Snowflake,
        val type: ChannelType,
        val name: String,
)

enum class MessageFlag(val code: Int) {
    /** This message has been published to subscribed channels (via Channel Following) */
    CrossPosted(1),

    /** This message originated from a message in another channel (via Channel Following) */
    IsCrossPost(2),

    /** Do not include any embeds when serializing this message. */
    SuppressEmbeds(4),

    /** The source message for this crosspost has been deleted (via Channel Following). */
    SourceMessageDeleted(8),

    /* This message came from the urgent message system. */
    Urgent(16);
}

@Serializable(with = MessageFlags.Serializer::class)
data class MessageFlags internal constructor(val code: Int) {

    val flags = MessageFlag.values().filter { code and it.code != 0 }

    operator fun contains(flag: MessageFlag) = flag in flags

    operator fun plus(flags: MessageFlags): MessageFlags = when {
        code and flags.code == flags.code -> this
        else -> MessageFlags(this.code or flags.code)
    }

    operator fun minus(flag: MessageFlag): MessageFlags = when {
        code and flag.code == flag.code -> MessageFlags(code xor flag.code)
        else -> this
    }

    inline fun copy(block: Builder.() -> Unit): MessageFlags {
        val builder = Builder(code)
        builder.apply(block)
        return builder.flags()
    }

    companion object {
        inline operator fun invoke(builder: Builder.() -> Unit): MessageFlags {
            return Builder().apply(builder).flags()
        }
    }

    internal object Serializer : KSerializer<MessageFlags> {

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("flags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): MessageFlags {
            val flags = decoder.decodeInt()
            return MessageFlags(flags)
        }

        override fun serialize(encoder: Encoder, value: MessageFlags) {
            encoder.encodeInt(value.code)
        }
    }

    class Builder(internal var code: Int = 0) {
        operator fun MessageFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or code
        }

        operator fun MessageFlag.unaryMinus() {
            if (this@Builder.code and code == code) {
                this@Builder.code = this@Builder.code xor code
            }
        }

        fun flags() = MessageFlags(code)
    }

}

/**
 * A representation of a [Discord Attachment structure](https://discord.com/developers/docs/resources/channel#attachment-object).
 *
 * @param id The attachment id.
 * @param filename The name of the attached file.
 * @param size The size of the file in bytes.
 * @param url The source url of the file.
 * @param proxyUrl A proxied url of the field.
 * @param height The height of the file (if it is an image).
 * @param width The width of the file (if it is an image).
 */
@Serializable
data class DiscordAttachment(
        val id: Snowflake,
        val filename: String,
        val size: Int,
        val url: String,
        @SerialName("proxy_url")
        val proxyUrl: String,
        /*
        Do not trust the docs:
        2020-11-06 This field is marked as nullable but can be missing instead.
        */
        val height: OptionalInt? = OptionalInt.Missing,
        /*
        Do not trust the docs:
        2020-11-06 This field is marked as nullable but can be missing instead.
        */
        val width: OptionalInt? = OptionalInt.Missing,
)

/**
 * A representation of a [Discord Embed structure](https://discord.com/developers/docs/resources/channel#embed-object-embed-structure).
 *
 * @param title The title of the embed.
 * @param type The type of the embed (always [EmbedType.Rich] for webhook embeds).
 * @param description The description of the embed.
 * @param url The url of the embed.
 * @param timestamp The timestamp of the embed content.
 * @param color The color code of the embed.
 * @param footer The footer information.
 * @param image The image information.
 * @param thumbnail The thumbnail information.
 * @param video The video information.
 * @param provider The provider information.
 * @param author The author information.
 * @param fields The fields information.
 */
@Serializable
data class DiscordEmbed(
        val title: Optional<String> = Optional.Missing(),
        @Suppress("DEPRECATION")
        val type: Optional<EmbedType> = Optional.Missing(),
        val description: Optional<String> = Optional.Missing(),
        val url: Optional<String> = Optional.Missing(),
        val timestamp: Optional<String> = Optional.Missing(),
        val color: OptionalInt = OptionalInt.Missing,
        val footer: Optional<Footer> = Optional.Missing(),
        val image: Optional<Image> = Optional.Missing(),
        val thumbnail: Optional<Thumbnail> = Optional.Missing(),
        val video: Optional<Video> = Optional.Missing(),
        val provider: Optional<Provider> = Optional.Missing(),
        val author: Optional<Author> = Optional.Missing(),
        val fields: Optional<List<Field>> = Optional.Missing(),
) {

    /**
     * A representation of a [Discord Embed Footer structure](https://discord.com/developers/docs/resources/channel#embed-object-embed-footer-structure).
     *
     * @param text The footer text.
     * @param iconUrl The url of the footer icon (only supports http(s) and attachments).
     * @param proxyIconUrl A proxied url of a footer icon.
     */
    @Serializable
    data class Footer(
            val text: String,
            @SerialName("icon_url")
            val iconUrl: Optional<String> = Optional.Missing(),
            @SerialName("proxy_icon_url")
            val proxyIconUrl: Optional<String> = Optional.Missing(),
    )

    /**
     * A representation of a [Discord Embed Image structure](https://discord.com/developers/docs/resources/channel#embed-object-embed-image-structure).
     *
     * @param url The source url of the image (only supports http(s) and attachments).
     * @param proxyUrl A proxied url of the image.
     * @param height The height of the image.
     * @param width The width of the image.
     */
    @Serializable
    data class Image(
            val url: Optional<String> = Optional.Missing(),
            @SerialName("proxy_url")
            val proxyUrl: Optional<String> = Optional.Missing(),
            val height: OptionalInt = OptionalInt.Missing,
            val width: OptionalInt = OptionalInt.Missing,
    )

    /**
     * A representation of a [Discord Embed Thumbnail structure](https://discord.com/developers/docs/resources/channel#embed-object-embed-thumbnail-structure).
     *
     * @param url The source url of a thumbnail (only supports http(s) and attachments).
     * @param proxyUrl A proxied url of the thumbnail.
     * @param height The height of the thumbnail.
     * @param width The height of the thumbnail.
     */
    @Serializable
    data class Thumbnail(
            val url: Optional<String> = Optional.Missing(),
            @SerialName("proxy_url")
            val proxyUrl: Optional<String> = Optional.Missing(),
            val height: OptionalInt = OptionalInt.Missing,
            val width: OptionalInt = OptionalInt.Missing,
    )

    /**
     * A representation of a [Discord Embed Video structure](https://discord.com/developers/docs/resources/channel#embed-object-embed-video-structure).
     *
     * @param url The source url of the video.
     * @param height The height of the video.
     * @param width The width of the video.
     */
    @Serializable
    data class Video(
            val url: Optional<String> = Optional.Missing(),
            val height: OptionalInt = OptionalInt.Missing,
            val width: OptionalInt = OptionalInt.Missing,
    )

    /**
     * A representation of a [Discord Embed Provider structure](https://discord.com/developers/docs/resources/channel#embed-object-embed-provider-structure).
     *
     * @param name The name of the provider.
     * @param url The url of the provider.
     */
    @Serializable
    data class Provider(
            val name: Optional<String> = Optional.Missing(),
            val url: Optional<String?> = Optional.Missing(), //see https://github.com/kordlib/kord/issues/149
    )

    /**
     * A representation of a [Discord Embed Author structure](https://discord.com/developers/docs/resources/channel#embed-object-embed-author-structure).
     *
     * @param name the Name of the author.
     * @param url The url of the author.
     * @param iconUrl The url of the author icon (only supports http(s) and attachments).
     * @param proxyIconUrl A proxied url of the author icon.
     */
    @Serializable
    data class Author(
            val name: Optional<String> = Optional.Missing(),
            val url: Optional<String> = Optional.Missing(),
            @SerialName("icon_url")
            val iconUrl: Optional<String> = Optional.Missing(),
            @SerialName("proxy_icon_url")
            val proxyIconUrl: Optional<String> = Optional.Missing(),
    )

    /**
     * A representation of a [Discord Embed Field structure](https://discord.com/developers/docs/resources/channel#embed-object-embed-field-structure).
     *
     * @param name The name of the field.
     * @param value The value of the field.
     * @param inline Whether or not this field should display inline.
     */
    @Serializable
    data class Field(
            val name: String,
            val value: String,
            val inline: OptionalBoolean = OptionalBoolean.Missing,
    )
}

/**
 * A representation of a [Discord Embed Type structure](https://discord.com/developers/docs/resources/channel#embed-object-embed-types).
 *
 * Embed types are "loosely defined" and, for the most part, are not used by our clients for rendering.
 * Embed attributes power what is rendered.
 * Embed types should be considered deprecated and might be removed in a future API version.
 */
@Suppress("DEPRECATION")
@Deprecated("""
    Embed types are "loosely defined" and, for the most part, are not used by clients for rendering. 
    Embed attributes power what is rendered. 
    Embed types should be considered deprecated and might be removed in a future API version.
""")
@Serializable(with = EmbedType.Serializer::class)
sealed class EmbedType(val value: String) {
    class Unknown(value: String) : EmbedType(value)

    /** Generic embed rendered from embed attributes. */
    object Rich : EmbedType("rich")

    /** Image embed. */
    object Image : EmbedType("image")

    /** Video embed. */
    object Video : EmbedType("video")

    /** Animated gif image embed rendered as a video embed. */
    object Gifv : EmbedType("gifv")

    /** Article embed. */
    object Article : EmbedType("article")

    /** Link embed. */
    object Link : EmbedType("link")

    internal object Serializer : KSerializer<EmbedType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.EmbedType", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): EmbedType = when (val value = decoder.decodeString()) {
            "rich" -> Rich
            "image" -> Image
            "video" -> Video
            "gifv" -> Gifv
            "article" -> Article
            "Link" -> Link
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: EmbedType) {
            encoder.encodeString(value.value)
        }
    }

}

@Serializable
data class Reaction(
        val count: Int,
        val me: Boolean,
        val emoji: DiscordEmoji,
)

@Serializable
data class MessageActivity(
        val type: MessageActivityType,
        @SerialName("party_id")
        val partyId: Optional<String> = Optional.Missing(),
)

@Serializable(with = MessageActivityType.Serializer::class)
sealed class MessageActivityType(val value: Int) {
    class Unknown(value: Int) : MessageActivityType(value)
    object Join : MessageActivityType(1)
    object Spectate : MessageActivityType(2)
    object Listen : MessageActivityType(3)
    object JoinRequest : MessageActivityType(5)

    internal object Serializer : KSerializer<MessageActivityType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.MessageActivivtyType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): MessageActivityType = when (val value = decoder.decodeInt()) {
            1 -> Join
            2 -> Spectate
            3 -> Listen
            5 -> JoinRequest
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: MessageActivityType) {
            encoder.encodeInt(value.value)
        }
    }
}

@Serializable
data class MessageApplication(
        val id: Snowflake,
        @SerialName("cover_image")
        val coverImage: Optional<String> = Optional.Missing(),
        val description: String,
        val icon: String? = null,
        val name: String,
)

@Serializable
data class DeletedMessage(
        val id: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
)

@Serializable
data class BulkDeleteData(
        val ids: List<Snowflake>,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
)

@Serializable
data class MessageReactionAddData(
        @SerialName("user_id")
        val userId: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("message_id")
        val messageId: Snowflake,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        val member: Optional<DiscordGuildMember> = Optional.Missing(),
        val emoji: DiscordPartialEmoji,
)

@Serializable
data class MessageReactionRemoveData(
        @SerialName("user_id")
        val userId: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("message_id")
        val messageId: Snowflake,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        val emoji: DiscordPartialEmoji,
)

@Serializable
data class AllRemovedMessageReactions(
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("message_id")
        val messageId: Snowflake,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
)

@Serializable(with = MessageType.MessageTypeSerializer::class)
enum class MessageType(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    Default(0),
    RecipientAdd(1),
    RecipientRemove(2),
    Call(3),
    ChannelNameChange(4),
    ChannelIconChange(5),
    ChannelPinnedMessage(6),
    GuildMemberJoin(7),
    UserPremiumGuildSubscription(8),
    UserPremiumGuildSubscriptionTierOne(9),
    UserPremiumGuildSubscriptionTwo(10),
    UserPremiumGuildSubscriptionThree(11),
    ChannelFollowAdd(12),
    GuildDiscoveryDisqualified(14),

    @Suppress("SpellCheckingInspection")
    GuildDiscoveryRequalified(15),
    Reply(19);

    companion object MessageTypeSerializer : KSerializer<MessageType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): MessageType {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: MessageType) {
            encoder.encodeInt(value.code)
        }
    }
}

