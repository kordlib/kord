package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class UnavailableGuild(
        val id: String,
        val unavailable: Boolean? = null
)

@Serializable
data class Guild(
        val id: String,
        val name: String,
        val icon: String? = null,
        val splash: String? = null,
        val owner: Boolean? = null,
        @SerialName("owner_id")
        val ownerId: String,
        val permissions: Int? = null,
        val region: String,
        @SerialName("afk_channel_id")
        val afkChannelId: String? = null,
        @SerialName("afk_timeout")
        val afkTimeout: Int,
        @SerialName("embed_enabled")
        val embedEnabled: Boolean? = null,
        @SerialName("embed_channel_id")
        val embedChannelId: String? = null,
        @SerialName("verification_level")
        val verificationLevel: VerificationLevel,
        @SerialName("default_message_notifications")
        val defaultMessageNotifications: DefaultMessageNotificationLevel,
        @SerialName("explicit_content_filter")
        val explicitContentFilter: ExplicitContentFilter,
        val roles: List<Role>,
        val emojis: List<Emoji>,
        val features: List<String>,
        @SerialName("mfa_level")
        val mfaLevel: MFALevel,
        @SerialName("application_id")
        val applicationId: String? = null,
        @SerialName("widget_enabled")
        val widgetEnabled: Boolean? = null,
        @SerialName("widget_channel_id")
        val widgetChannelId: String? = null,
        @SerialName("system_channel_id")
        val systemChannelId: String? = null,
        @SerialName("joined_at")
        val joinedAt: String? = null,
        val large: Boolean? = null,
        val unavailable: Boolean? = null,
        @SerialName("member_count")
        val memberCount: Int? = null,
        @SerialName("voice_states")
        val voiceStates: List<VoiceState>? = null,
        val members: List<GuildMember>? = null,
        val channels: List<Channel>? = null,
        val presences: List<PresenceUpdateData>? = null,
        @SerialName("max_presences")
        val maxPresences: Int? = null,
        @SerialName("max_members")
        val maxMembers: Int? = null,
        @SerialName("vanity_url_code")
        val vanityUrlCode: String? = null,
        val description: String? = null,
        val banner: String? = null
)

@Serializable
data class GuildBan(
        @SerialName("guild_id")
        val guildId: String,
        val user: User
)

@Serializable
data class GuildIntegrations(@SerialName("guild_id") val guildId: String)

@Serializable
data class GuildMembersChunkData(
        @SerialName("guild_id")
        val guildId: String,
        val members: List<GuildMember>
)

@Serializable
data class VoiceServerUpdateData(
        val token: String,
        @SerialName("guild_id")
        val guildId: String,
        val endpoint: String
)

@Serializable
data class WebhooksUpdateData(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("channel_id")
        val channelId: String
)

@Serializable
data class VoiceState(
        @SerialName("guild_id")
        val guildId: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null,
        @SerialName("user_id")
        val userId: String,
        @SerialName("guild_member")
        val member: GuildMember? = null,
        @SerialName("session_id")
        val sessionId: String,
        val deaf: Boolean,
        val mute: Boolean,
        @SerialName("self_deaf")
        val selfDeaf: Boolean,
        @SerialName("self_mute")
        val selfMute: Boolean,
        val suppress: Boolean
)

@Serializable(with = DefaultMessageNotificationLevelSerializer::class)
enum class DefaultMessageNotificationLevel(val code: Int) {
    AllMessages(0),
    OnlyMentions(1)
}

@Serializer(forClass = DefaultMessageNotificationLevel::class)
private object DefaultMessageNotificationLevelSerializer : KSerializer<DefaultMessageNotificationLevel> {
    override val descriptor: SerialDescriptor
        get() = IntDescriptor.withName("default_message_notifications")

    override fun deserialize(decoder: Decoder): DefaultMessageNotificationLevel {
        val code = decoder.decodeInt()
        return DefaultMessageNotificationLevel.values().first { it.code == code }
    }

    override fun serialize(encoder: Encoder, obj: DefaultMessageNotificationLevel) {
        encoder.encodeInt(obj.code)
    }
}

@Serializable(with = ExplicitContentFilterSerializer::class)
enum class ExplicitContentFilter(val code: Int) {
    Disabled(0),
    MembersWithoutRoles(1),
    AllMembers(2)
}

@Serializer(forClass = ExplicitContentFilter::class)
private object ExplicitContentFilterSerializer : KSerializer<ExplicitContentFilter> {

    override val descriptor: SerialDescriptor
        get() = IntDescriptor.withName("explicit_content_filter")

    override fun deserialize(decoder: Decoder): ExplicitContentFilter {
        val code = decoder.decodeInt()
        return ExplicitContentFilter.values().first { it.code == code }
    }

    override fun serialize(encoder: Encoder, obj: ExplicitContentFilter) {
        encoder.encodeInt(obj.code)
    }

}

@Serializable(with = MFALevelSerializer::class)
enum class MFALevel(val code: Int) {
    None(0),
    Elevated(1)
}

@Serializer(forClass = MFALevel::class)
private object MFALevelSerializer : KSerializer<MFALevel> {

    override val descriptor: SerialDescriptor
        get() = IntDescriptor.withName("mfa_level")

    override fun deserialize(decoder: Decoder): MFALevel {
        val code = decoder.decodeInt()
        return MFALevel.values().first { it.code == code }
    }

    override fun serialize(encoder: Encoder, obj: MFALevel) {
        encoder.encodeInt(obj.code)
    }

}

@Serializable(with = VerificationLevel.VerificationLevelSerializer::class)
enum class VerificationLevel(val code: Int) {
    None(0),
    Low(1),
    Medium(2),
    High(3),
    VeryHigh(4);

    @Serializer(forClass = VerificationLevel::class)
    companion object VerificationLevelSerializer : KSerializer<VerificationLevel> {

        override val descriptor: SerialDescriptor
            get() = IntDescriptor.withName("verification_level")

        override fun deserialize(decoder: Decoder): VerificationLevel {
            val code = decoder.decodeInt()
            return values().first { it.code == code }
        }

        override fun serialize(encoder: Encoder, obj: VerificationLevel) {
            encoder.encodeInt(obj.code)
        }

    }
}