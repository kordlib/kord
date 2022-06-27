package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.IntOrStringSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
 * @param applicationId if the message is a response to an [Interaction][DiscordInteraction], this is the id of the interaction's application
 * @param components a list of [components][DiscordComponent] which have been added to this message
 */

@Serializable
public data class DiscordMessage(
    val id: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val author: DiscordUser,
    val member: Optional<DiscordGuildMember> = Optional.Missing(),
    val content: String,
    val timestamp: Instant,
    @SerialName("edited_timestamp")
    val editedTimestamp: Instant?,
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
    val nonce: Optional<@Serializable(with = IntOrStringSerializer::class) String> = Optional.Missing(),
    val pinned: Boolean,
    @SerialName("webhook_id")
    val webhookId: OptionalSnowflake = OptionalSnowflake.Missing,
    val type: MessageType,
    val activity: Optional<MessageActivity> = Optional.Missing(),
    val application: Optional<MessageApplication> = Optional.Missing(),
    @SerialName("application_id")
    val applicationId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("message_reference")
    val messageReference: Optional<DiscordMessageReference> = Optional.Missing(),
    val flags: Optional<MessageFlags> = Optional.Missing(),
    @SerialName("sticker_items")
    val stickers: Optional<List<DiscordStickerItem>> = Optional.Missing(),
    @SerialName("referenced_message")
    val referencedMessage: Optional<DiscordMessage?> = Optional.Missing(),
    /*
     * don't trust the docs:
     * This is a list even though the docs say it's a component
     */
    val components: Optional<List<DiscordComponent>> = Optional.Missing(),
    val interaction: Optional<DiscordMessageInteraction> = Optional.Missing(),
    val thread: Optional<DiscordChannel> = Optional.Missing()
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
public data class DiscordMessageSticker(
    val id: Snowflake,
    @SerialName("pack_id")
    val packId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: String,
    val description: String?,
    val tags: Optional<String> = Optional.Missing(),
    @SerialName("format_type")
    val formatType: MessageStickerType,
    val available: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val user: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("sort_value")
    val sortValue: OptionalInt = OptionalInt.Missing
)

@Serializable
public data class DiscordStickerPack(
    val id: Snowflake,
    val stickers: List<DiscordMessageSticker>,
    val name: String,
    @SerialName("sku_id")
    val skuId: Snowflake,
    @SerialName("cover_sticker_id")
    val coverStickerId: OptionalSnowflake = OptionalSnowflake.Missing,
    val description: String,
    @SerialName("banner_asset_id")
    val bannerAssetId: Snowflake
)

@Serializable
public data class DiscordStickerItem(
    val id: Snowflake,
    val name: String,
    @SerialName("format_type")
    val formatType: MessageStickerType
)

@Serializable(with = MessageStickerType.Serializer::class)
public sealed class MessageStickerType(public val value: Int) {
    public class Unknown(value: Int) : MessageStickerType(value)
    public object PNG : MessageStickerType(1)
    public object APNG : MessageStickerType(2)
    public object LOTTIE : MessageStickerType(3)

    public companion object {
        public val values: Set<MessageStickerType> = setOf(PNG, APNG, LOTTIE)
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
public data class DiscordPartialMessage(
    val id: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val author: Optional<DiscordUser> = Optional.Missing(),
    val member: Optional<DiscordGuildMember> = Optional.Missing(),
    val content: Optional<String> = Optional.Missing(),
    val timestamp: Optional<Instant> = Optional.Missing(),
    @SerialName("edited_timestamp")
    val editedTimestamp: Optional<Instant?> = Optional.Missing(),
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
    val stickers: Optional<List<DiscordStickerItem>> = Optional.Missing(),
    @SerialName("referenced_message")
    val referencedMessage: Optional<DiscordMessage?> = Optional.Missing(),
    val interaction: Optional<DiscordMessageInteraction> = Optional.Missing(),
)

@Serializable
public data class DiscordMessageReference(
    @SerialName("message_id")
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("fail_if_not_exists")
    val failIfNotExists: OptionalBoolean = OptionalBoolean.Missing
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
public data class DiscordMentionedChannel(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val type: ChannelType,
    val name: String,
)

public enum class MessageFlag(public val code: Int) {
    /** This message has been published to subscribed channels (via Channel Following). */
    CrossPosted(1 shl 0),

    /** This message originated from a message in another channel (via Channel Following). */
    IsCrossPost(1 shl 1),

    /** Do not include any embeds when serializing this message. */
    SuppressEmbeds(1 shl 2),

    /** The source message for this crosspost has been deleted (via Channel Following). */
    SourceMessageDeleted(1 shl 3),

    /** This message came from the urgent message system. */
    Urgent(1 shl 4),

    /** This message has an associated thread, with the same id as the message. */
    HasThread(1 shl 5),

    /** This message is only visible to the user who invoked the Interaction. */
    Ephemeral(1 shl 6),

    /** This message is an Interaction Response and the bot is "thinking". */
    Loading(1 shl 7),

    /** This message failed to mention some roles and add their members to the thread. */
    FailedToMentionSomeRolesInThread(1 shl 8),
}

@Serializable(with = MessageFlags.Serializer::class)
public data class MessageFlags internal constructor(val code: Int) {

    val flags: List<MessageFlag> = MessageFlag.values().filter { code and it.code != 0 }

    public operator fun contains(flag: MessageFlag): Boolean = flag.code and this.code == flag.code

    public operator fun contains(flags: MessageFlags): Boolean = flags.code and this.code == flags.code

    public operator fun plus(flags: MessageFlags): MessageFlags = MessageFlags(this.code or flags.code)

    public operator fun plus(flags: MessageFlag): MessageFlags = MessageFlags(this.code or flags.code)

    public operator fun minus(flags: MessageFlags): MessageFlags = MessageFlags(this.code xor flags.code)

    public operator fun minus(flags: MessageFlag): MessageFlags = MessageFlags(this.code xor flags.code)


    public inline fun copy(block: Builder.() -> Unit): MessageFlags {
        val builder = Builder(code)
        builder.apply(block)
        return builder.flags()
    }

    override fun toString(): String = "MessageFlags(flags=$flags)"

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

    public class Builder(internal var code: Int = 0) {
        public operator fun MessageFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or code
        }

        public operator fun MessageFlag.unaryMinus() {
            if (this@Builder.code and code == code) {
                this@Builder.code = this@Builder.code xor code
            }
        }

        public operator fun MessageFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or code
        }

        public operator fun MessageFlags.unaryMinus() {
            if (this@Builder.code and code == code) {
                this@Builder.code = this@Builder.code xor code
            }
        }

        public fun flags(): MessageFlags = MessageFlags(code)
    }

}

public inline fun MessageFlags(builder: MessageFlags.Builder.() -> Unit): MessageFlags {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return MessageFlags.Builder().apply(builder).flags()
}

public fun MessageFlags(vararg flags: MessageFlag): MessageFlags = MessageFlags {
    flags.forEach { +it }
}

public fun MessageFlags(vararg flags: MessageFlags): MessageFlags = MessageFlags {
    flags.forEach { +it }
}

public fun MessageFlags(flags: Iterable<MessageFlag>): MessageFlags = MessageFlags {
    flags.forEach { +it }
}


@JvmName("MessageFlagsWithIterable")
public fun MessageFlags(flags: Iterable<MessageFlags>): MessageFlags = MessageFlags {
    flags.forEach { +it }
}


/**
 * A representation of a [Discord Attachment structure](https://discord.com/developers/docs/resources/channel#attachment-object).
 *
 * @param id The attachment id.
 * @param filename The name of the attached file.
 * @param description The description for the file.
 * @param contentType The attachment's [media type](https://en.wikipedia.org/wiki/Media_type).
 * @param size The size of the file in bytes.
 * @param url The source url of the file.
 * @param proxyUrl A proxied url of the field.
 * @param height The height of the file (if it is an image).
 * @param width The width of the file (if it is an image).
 */
@Serializable
public data class DiscordAttachment(
    val id: Snowflake,
    val filename: String,
    val description: Optional<String> = Optional.Missing(),
    @SerialName("content_type")
    val contentType: Optional<String> = Optional.Missing(),
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

    val ephemeral: OptionalBoolean = OptionalBoolean.Missing
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
public data class DiscordEmbed(
    val title: Optional<String> = Optional.Missing(),
    @Suppress("DEPRECATION")
    val type: Optional<EmbedType> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val url: Optional<String> = Optional.Missing(),
    val timestamp: Optional<Instant> = Optional.Missing(),
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
    public data class Footer(
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
    public data class Image(
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
    public data class Thumbnail(
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
    public data class Video(
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
    public data class Provider(
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
    public data class Author(
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
    public data class Field(
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
@Deprecated(
    """
    Embed types are "loosely defined" and, for the most part, are not used by clients for rendering. 
    Embed attributes power what is rendered. 
    Embed types should be considered deprecated and might be removed in a future API version.
"""
)
@Serializable(with = EmbedType.Serializer::class)
public sealed class EmbedType(public val value: String) {
    public class Unknown(value: String) : EmbedType(value)

    /** Generic embed rendered from embed attributes. */
    public object Rich : EmbedType("rich")

    /** Image embed. */
    public object Image : EmbedType("image")

    /** Video embed. */
    public object Video : EmbedType("video")

    /** Animated gif image embed rendered as a video embed. */
    public object Gifv : EmbedType("gifv")

    /** Article embed. */
    public object Article : EmbedType("article")

    /** Link embed. */
    public object Link : EmbedType("link")

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
public data class Reaction(
    val count: Int,
    val me: Boolean,
    val emoji: DiscordEmoji,
)

@Serializable
public data class MessageActivity(
    val type: MessageActivityType,
    @SerialName("party_id")
    val partyId: Optional<String> = Optional.Missing(),
)

@Serializable(with = MessageActivityType.Serializer::class)
public sealed class MessageActivityType(public val value: Int) {
    public class Unknown(value: Int) : MessageActivityType(value)
    public object Join : MessageActivityType(1)
    public object Spectate : MessageActivityType(2)
    public object Listen : MessageActivityType(3)
    public object JoinRequest : MessageActivityType(5)

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
public data class MessageApplication(
    val id: Snowflake,
    @SerialName("cover_image")
    val coverImage: Optional<String> = Optional.Missing(),
    val description: String,
    val icon: String? = null,
    val name: String,
)

@Serializable
public data class DeletedMessage(
    val id: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
)

@Serializable
public data class BulkDeleteData(
    val ids: List<Snowflake>,
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
)

@Serializable
public data class MessageReactionAddData(
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
public data class MessageReactionRemoveData(
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
public data class AllRemovedMessageReactions(
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("message_id")
    val messageId: Snowflake,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
)

@Serializable(with = MessageType.MessageTypeSerializer::class)
public sealed class MessageType(public val code: Int) {
    /** The default code for unknown values. */
    public class Unknown(code: Int) : MessageType(code)
    public object Default : MessageType(0)
    public object RecipientAdd : MessageType(1)
    public object RecipientRemove : MessageType(2)
    public object Call : MessageType(3)
    public object ChannelNameChange : MessageType(4)
    public object ChannelIconChange : MessageType(5)
    public object ChannelPinnedMessage : MessageType(6)
    public object GuildMemberJoin : MessageType(7)
    public object UserPremiumGuildSubscription : MessageType(8)
    public object UserPremiumGuildSubscriptionTierOne : MessageType(9)
    public object UserPremiumGuildSubscriptionTwo : MessageType(10)
    public object UserPremiumGuildSubscriptionThree : MessageType(11)
    public object ChannelFollowAdd : MessageType(12)
    public object GuildDiscoveryDisqualified : MessageType(14)

    @Suppress("SpellCheckingInspection")
    public object GuildDiscoveryRequalified : MessageType(15)
    public object GuildDiscoveryGracePeriodInitialWarning : MessageType(16)
    public object GuildDiscoveryGracePeriodFinalWarning : MessageType(17)
    public object ThreadCreated : MessageType(18)
    public object Reply : MessageType(19)
    public object ChatInputCommand : MessageType(20)
    public object ThreadStarterMessage : MessageType(21)
    public object GuildInviteReminder : MessageType(22)
    public object ContextMenuCommand : MessageType(23)

    internal object MessageTypeSerializer : KSerializer<MessageType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): MessageType {
            val code = decoder.decodeInt()
            return values.firstOrNull { it.code == code } ?: Unknown(code)
        }

        override fun serialize(encoder: Encoder, value: MessageType) {
            encoder.encodeInt(value.code)
        }
    }

    public companion object {
        public val values: Set<MessageType>
            get() = setOf(
                Default,
                RecipientAdd,
                RecipientRemove,
                Call,
                ChannelNameChange,
                ChannelIconChange,
                ChannelPinnedMessage,
                GuildMemberJoin,
                UserPremiumGuildSubscription,
                UserPremiumGuildSubscriptionTierOne,
                UserPremiumGuildSubscriptionTwo,
                UserPremiumGuildSubscriptionThree,
                ChannelFollowAdd,
                GuildDiscoveryDisqualified,
                GuildDiscoveryRequalified,
                Reply,
                GuildDiscoveryGracePeriodInitialWarning,
                GuildDiscoveryGracePeriodFinalWarning,
                ThreadCreated,
                ChatInputCommand,
                ThreadStarterMessage,
                GuildInviteReminder,
                ContextMenuCommand,
            )
    }
}

@Serializable(with = AllowedMentionType.Serializer::class)
public sealed class AllowedMentionType(public val value: String) {
    public class Unknown(value: String) : AllowedMentionType(value)
    public object RoleMentions : AllowedMentionType("roles")
    public object UserMentions : AllowedMentionType("users")
    public object EveryoneMentions : AllowedMentionType("everyone")

    internal object Serializer : KSerializer<AllowedMentionType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.DiscordAllowedMentionType", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): AllowedMentionType = when (val value = decoder.decodeString()) {
            "roles" -> RoleMentions
            "users" -> UserMentions
            "everyone" -> EveryoneMentions
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: AllowedMentionType) {
            encoder.encodeString(value.value)
        }
    }
}

@Serializable
public data class AllowedMentions(
    val parse: List<AllowedMentionType>,
    val users: List<String>,
    val roles: List<String>,
    @SerialName("replied_user")
    val repliedUser: OptionalBoolean = OptionalBoolean.Missing,
)


@Serializable
public data class DiscordMessageInteraction(
    val id: Snowflake,
    val type: InteractionType,
    val name: String,
    val user: DiscordUser,
)
