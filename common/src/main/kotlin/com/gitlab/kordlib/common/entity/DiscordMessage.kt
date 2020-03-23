package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class DiscordMessage(
        val id: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("guild_id")
        val guildId: String? = null,
        val author: DiscordUser? = null,
        val member: DiscordPartialGuildMember? = null,
        val content: String,
        val timestamp: String,
        @SerialName("edited_timestamp")
        val editedTimestamp: String? = null,
        val tts: Boolean,
        @SerialName("mention_everyone")
        val mentionEveryone: Boolean,
        val mentions: List<DiscordOptionallyMemberUser>,
        @SerialName("mention_roles")
        val mentionRoles: List<String>,
        val attachments: List<Attachment>,
        val embeds: List<Embed>,
        val reactions: List<Reaction>? = null,
        val nonce: String? = null,
        val pinned: Boolean,
        @SerialName("webhook_id")
        val webhookId: String? = null,
        val type: MessageType,
        val activity: MessageActivity? = null,
        val application: MessageApplication? = null,
        @SerialName("message_reference")
        val messageReference: MessageReference? = null,
        @SerialName("mention_channels")
        val mentionedChannels: List<MentionedChannel>? = null,
        val flags: Flags? = null
)
@Serializable
data class DiscordPartialMessage(
        val id: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("guild_id")
        val guildId: String? = null,
        val author: DiscordUser? = null,
        val member: DiscordPartialGuildMember? = null,
        val content: String? = null,
        val timestamp: String? = null,
        @SerialName("edited_timestamp")
        val editedTimestamp: String? = null,
        val tts: Boolean? = null,
        @SerialName("mention_everyone")
        val mentionEveryone: Boolean? = null,
        val mentions: List<DiscordOptionallyMemberUser>? = null,
        @SerialName("mention_roles")
        val mentionRoles: List<String> ? = null,
        val attachments: List<Attachment> ? = null,
        val embeds: List<Embed> ? = null,
        val reactions: List<Reaction>? = null,
        val nonce: String? = null,
        val pinned: Boolean? = null,
        @SerialName("webhook_id")
        val webhookId: String? = null,
        val type: MessageType? = null,
        val activity: MessageActivity? = null,
        val application: MessageApplication? = null,
        @SerialName("message_reference")
        val messageReference: MessageReference? = null,
        @SerialName("mention_channels")
        val mentionedChannels: List<MentionedChannel>? = null,
        val flags: Flags? = null
)

@Serializable
data class MessageReference(
        @SerialName("message_id")
        val id: String? = null,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("guild_id")
        val guildId: String? = null
)

@Serializable
data class MentionedChannel(
        val id: String,
        @SerialName("guild_id")
        val guildId: String,
        val name: String,
        val type: MessageType
)

enum class Flag(val code: Int) {
    CrossPosted(1),
    IsCrossPost(2),
    SuppressEmbeds(4);
}

@Serializable(with = Flags.FlagsSerializer::class)
data class Flags internal constructor(val code: Int) {

    val flags = Flag.values().filter { code and it.code != 0 }

    operator fun contains(flag: Flag) = flag in flags

    operator fun plus(flags: Flags): Flags = when {
        code and flags.code == flags.code -> this
        else -> Flags(this.code or flags.code)
    }

    operator fun minus(flag: Flag): Flags = when {
        code and flag.code == flag.code -> Flags(code xor flag.code)
        else -> this
    }

    inline fun copy(block: FlagsBuilder.() -> Unit): Flags {
        val builder = FlagsBuilder(code)
        builder.apply(block)
        return builder.flags()
    }


    @Serializer(forClass = Flags::class)
    companion object FlagsSerializer : DeserializationStrategy<Flags> {

        inline operator fun invoke(builder: FlagsBuilder.() -> Unit): Flags {
            return FlagsBuilder().apply(builder).flags()
        }

        override val descriptor: SerialDescriptor = IntDescriptor

        override fun deserialize(decoder: Decoder): Flags {
            val flags = decoder.decodeInt()
            return Flags(flags)
        }

    }

    class FlagsBuilder(internal var code: Int = 0) {
        operator fun Flag.unaryPlus() {
            this@FlagsBuilder.code = this@FlagsBuilder.code or code
        }

        operator fun Flag.unaryMinus() {
            if (this@FlagsBuilder.code and code == code) {
                this@FlagsBuilder.code = this@FlagsBuilder.code xor code
            }
        }

        fun flags() = Flags(code)
    }

}

@Serializable
data class Attachment(
        val id: String,
        val filename: String? = null,
        val size: Int,
        val url: String,
        @SerialName("proxy_url")
        val proxyUrl: String,
        val height: Int? = null,
        val width: Int? = null
)

@Serializable
data class Embed(
        val title: String? = null,
        val type: String? = null,
        val description: String? = null,
        val url: String? = null,
        val timestamp: String? = null,
        val color: Int? = null,
        val footer: Footer? = null,
        val image: Image? = null,
        val thumbnail: Thumbnail? = null,
        val video: Video? = null,
        val provider: Provider? = null,
        val author: Author? = null,
        val fields: List<Field>? = null
) {
    @Serializable
    data class Footer(
            val text: String,
            @SerialName("icon_url")
            val iconUrl: String? = null,
            @SerialName("proxy_icon_url")
            val proxyIconUrl: String? = null
    )

    @Serializable
    data class Image(
            val url: String? = null,
            @SerialName("proxy_url")
            val proxyUrl: String? = null,
            val height: Int? = null,
            val width: Int? = null
    )

    @Serializable
    data class Thumbnail(
            val url: String? = null,
            @SerialName("proxy_url")
            val proxyUrl: String? = null,
            val height: Int? = null,
            val width: Int? = null
    )

    @Serializable
    data class Video(val url: String? = null, val height: Int? = null, val width: Int? = null)

    @Serializable
    data class Provider(val name: String? = null, val url: String? = null)

    @Serializable
    data class Author(
            val name: String? = null,
            val url: String? = null,
            @SerialName("icon_url")
            val iconUrl: String? = null,
            @SerialName("proxy_icon_url")
            val proxyIconUrl: String? = null
    )

    @Serializable
    data class Field(val name: String, val value: String, val inline: Boolean? = null)
}

@Serializable
data class Reaction(
        val count: Int,
        val me: Boolean,
        val emoji: DiscordEmoji
)

@Serializable
data class MessageActivity(val type: Int, @SerialName("party_id") val partyId: String? = null)

@Serializable
data class MessageApplication(
        val id: String,
        @SerialName("cover_image")
        val coverImage: String? = null,
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
        val guildId: String? = null
)

@Serializable
data class BulkDeleteData(
        val ids: List<String>,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("guild_id")
        val guildId: String? = null
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
        val guildId: String? = null,
        val member: DiscordGuildMember? = null,
        val emoji: DiscordPartialEmoji
)

@Serializable
data class AllRemovedMessageReactions(
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("message_id")
        val messageId: String,
        @SerialName("guild_id")
        val guildId: String? = null
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
    GuildDiscoveryRequalified(15);

    @Serializer(forClass = MessageType::class)
    companion object MessageTypeSerializer : KSerializer<MessageType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): MessageType {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: MessageType) {
            encoder.encodeInt(obj.code)
        }
    }
}

