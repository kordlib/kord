@file:Generate(
    INT_KORD_ENUM, name = "MessageType", valueName = "code",
    docUrl = "https://discord.com/developers/docs/resources/channel#message-object-message-types",
    entries = [
        Entry("Default", intValue = 0),
        Entry("RecipientAdd", intValue = 1),
        Entry("RecipientRemove", intValue = 2),
        Entry("Call", intValue = 3),
        Entry("ChannelNameChange", intValue = 4),
        Entry("ChannelIconChange", intValue = 5),
        Entry("ChannelPinnedMessage", intValue = 6),
        Entry("UserJoin", intValue = 7),
        Entry("GuildBoost", intValue = 8),
        Entry("GuildBoostTier1", intValue = 9),
        Entry("GuildBoostTier2", intValue = 10),
        Entry("GuildBoostTier3", intValue = 11),
        Entry("ChannelFollowAdd", intValue = 12),
        Entry("GuildDiscoveryDisqualified", intValue = 14),
        Entry("GuildDiscoveryRequalified", intValue = 15),
        Entry("GuildDiscoveryGracePeriodInitialWarning", intValue = 16),
        Entry("GuildDiscoveryGracePeriodFinalWarning", intValue = 17),
        Entry("ThreadCreated", intValue = 18),
        Entry("Reply", intValue = 19),
        Entry("ChatInputCommand", intValue = 20),
        Entry("ThreadStarterMessage", intValue = 21),
        Entry("GuildInviteReminder", intValue = 22),
        Entry("ContextMenuCommand", intValue = 23),
        Entry("AutoModerationAction", intValue = 24),
        Entry("RoleSubscriptionPurchase", intValue = 25),
        Entry("InteractionPremiumUpsell", intValue = 26),
        Entry("StageStart", intValue = 27),
        Entry("StageEnd", intValue = 28),
        Entry("StageSpeaker", intValue = 29),
        Entry("StageTopic", intValue = 31),
        Entry("GuildApplicationPremiumSubscription", intValue = 32),
        Entry("PurchaseNotification", intValue = 44),
    ],
)

@file:Generate(
    INT_FLAGS, name = "MessageFlag", valueName = "code",
    docUrl = "https://discord.com/developers/docs/resources/channel#message-object-message-flags",
    entries = [
        Entry(
            "CrossPosted", shift = 0,
            kDoc = "This message has been published to subscribed channels (via Channel Following).",
        ),
        Entry(
            "IsCrossPost", shift = 1,
            kDoc = "This message originated from a message in another channel (via Channel Following).",
        ),
        Entry("SuppressEmbeds", shift = 2, kDoc = "Do not include any embeds when serializing this message."),
        Entry(
            "SourceMessageDeleted", shift = 3,
            kDoc = "The source message for this crosspost has been deleted (via Channel Following).",
        ),
        Entry("Urgent", shift = 4, kDoc = "This message came from the urgent message system."),
        Entry("HasThread", shift = 5, kDoc = "This message has an associated thread, with the same id as the message."),
        Entry("Ephemeral", shift = 6, kDoc = "This message is only visible to the user who invoked the Interaction."),
        Entry("Loading", shift = 7, kDoc = """This message is an Interaction Response and the bot is "thinking"."""),
        Entry(
            "FailedToMentionSomeRolesInThread", shift = 8,
            kDoc = "This message failed to mention some roles and add their members to the thread.",
        ),
        Entry(
            "SuppressNotifications", shift = 12, kDoc = "This message will not trigger push and desktop notifications.",
        ),
        Entry("IsVoiceMessage", shift = 13, kDoc = "This message is a voice message."),
        Entry(
            "IsComponentsV2", shift = 15,
            kDoc = "Allows you to create fully [component](https://discord.com/developers/docs/components/overview)-" +
                "driven messages.",
        ),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "MessageActivityType",
    docUrl = "https://discord.com/developers/docs/resources/channel#message-object-message-activity-types",
    entries = [
        Entry("Join", intValue = 1),
        Entry("Spectate", intValue = 2),
        Entry("Listen", intValue = 3),
        Entry("JoinRequest", intValue = 5),
    ],
)

@file:Generate(
    STRING_KORD_ENUM, name = "EmbedType",
    docUrl = "https://discord.com/developers/docs/resources/channel#embed-object-embed-types",
    entries = [
        Entry("Rich", stringValue = "rich", kDoc = "Generic embed rendered from embed attributes."),
        Entry("Image", stringValue = "image", kDoc = "Image embed."),
        Entry("Video", stringValue = "video", kDoc = "Video embed."),
        Entry("Gifv", stringValue = "gifv", kDoc = "Animated gif image embed rendered as a video embed."),
        Entry("Article", stringValue = "article", kDoc = "Article embed."),
        Entry("Link", stringValue = "link", kDoc = "Link embed."),
    ],
)

@file:Generate(
    STRING_KORD_ENUM, name = "AllowedMentionType",
    docUrl = "https://discord.com/developers/docs/resources/channel#allowed-mentions-object-allowed-mention-types",
    entries = [
        Entry("RoleMentions", stringValue = "roles", kDoc = "Controls role mentions."),
        Entry("UserMentions", stringValue = "users", kDoc = "Controls user mentions"),
        Entry("EveryoneMentions", stringValue = "everyone", kDoc = "Controls @everyone and @here mentions."),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "MessageStickerType",
    docUrl = "https://discord.com/developers/docs/resources/sticker#sticker-object-sticker-format-types",
    entries = [
        Entry("PNG", intValue = 1),
        Entry("APNG", intValue = 2),
        Entry("LOTTIE", intValue = 3),
        Entry("GIF", intValue = 4)
    ],
)

@file:Generate(
    INT_FLAGS, name = "AttachmentFlag",
    docUrl = "https://discord.com/developers/docs/resources/channel#attachment-object-attachment-flags",
    entries = [
        Entry("IsRemix", shift = 2, kDoc = "This attachment has been edited using the remix feature on mobile."),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInDoubleSeconds
import dev.kord.common.serialization.LongOrStringSerializer
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_FLAGS
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.EntityType.STRING_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
 * @param roleSubscriptionData [RoleSubscription] object data of the role subscription purchase or renewal that prompted this message.
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
    val nonce: Optional<@Serializable(with = LongOrStringSerializer::class) String> = Optional.Missing(),
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
    @SerialName("role_subscription_data")
    val roleSubscriptionData: Optional<RoleSubscription> = Optional.Missing(),

    /*
     * don't trust the docs:
     * This is a list even though the docs say it's a component
     */
    val components: Optional<List<DiscordComponent>> = Optional.Missing(),
    val interaction: Optional<DiscordMessageInteraction> = Optional.Missing(),
    val thread: Optional<DiscordChannel> = Optional.Missing(),
    val position: OptionalInt = OptionalInt.Missing,
)

/**
 * @param id id of the sticker
 * @param packId id of the pack the sticker is from
 * @param name name of the sticker
 * @param description description of the sticker
 * @param tags a comma-separated list of tags for the sticker
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
    // The docs says this is non-nullable, non optional, but it still returns null in our tests
    val formatType: MessageStickerType?,
    val available: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val user: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("sort_value")
    val sortValue: OptionalInt = OptionalInt.Missing,
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
    val bannerAssetId: Snowflake,
)

@Serializable
public data class DiscordStickerItem(
    val id: Snowflake,
    val name: String,
    @SerialName("format_type")
    val formatType: MessageStickerType,
)

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
    val position: OptionalInt = OptionalInt.Missing,
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
    val failIfNotExists: OptionalBoolean = OptionalBoolean.Missing,
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

/**
 * A representation of a [Discord Attachment structure](https://discord.com/developers/docs/resources/channel#attachment-object).
 *
 * @property id The attachment id.
 * @property filename The name of the attached file.
 * @property description The description for the file.
 * @property contentType The attachment's [media type](https://en.wikipedia.org/wiki/Media_type).
 * @property size The size of the file in bytes.
 * @property url The source url of the file.
 * @property proxyUrl A proxied url of the field.
 * @property height The height of the file (if it is an image).
 * @property width The width of the file (if it is an image).
 * @property ephemeral Whether this attachment is ephemeral
 * @property durationSecs The duration of the audio file (currently for voice messages)
 * @property waveform Base64 encoded bytearray representing a sampled waveform (currently for voice messages)
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
    val height: OptionalInt? = OptionalInt.Missing,
    val width: OptionalInt? = OptionalInt.Missing,
    val ephemeral: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("duration_secs")
    val durationSecs: Optional<DurationInDoubleSeconds> = Optional.Missing(),
    val waveform: Optional<String> = Optional.Missing(),
    val flags: Optional<AttachmentFlags> = Optional.Missing(),
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
        val url: Optional<String?> = Optional.Missing(), // see https://github.com/kordlib/kord/issues/838
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
    @SerialName("message_author_id")
    val messageAuthorId: OptionalSnowflake = OptionalSnowflake.Missing,
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

@Serializable
public data class RoleSubscription(
    @SerialName("role_subscription_listing_id")
    val subscriptionId: Snowflake,
    @SerialName("tier_name")
    val tierName: String,
    @SerialName("total_months_subscribed")
    val totalMonthsSubscribed: Int,
    @SerialName("is_renewal")
    val isRenewal: Boolean,
)
