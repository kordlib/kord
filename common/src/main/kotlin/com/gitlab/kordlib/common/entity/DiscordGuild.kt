package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@KordUnstableApi
data class DiscordUnavailableGuild(
        val id: String,
        val unavailable: Boolean? = null
)

@Serializable
@KordUnstableApi
data class DiscordGuild(
        val id: String,
        val name: String,
        val icon: String? = null,
        val splash: String? = null,
        @SerialName("discovery_splash")
        val discoverySplash: String? = null,
        val owner: Boolean? = null,
        @SerialName("owner_id")
        val ownerId: String,
        val permissions: Permissions? = null,
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
        val roles: List<DiscordRole>,
        val emojis: List<DiscordEmoji>,
        val features: List<GuildFeature>,
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
        @SerialName("system_channel_flags")
        val systemChannelFlags: SystemChannelFlags? = null,
        /**  The id of the channel in which a discoverable server's rules should be found **/
        @SerialName("rules_channel_id")
        val rulesChannelId: String? = null,
        @SerialName("joined_at")
        val joinedAt: String? = null,
        val large: Boolean? = null,
        val unavailable: Boolean? = null,
        @SerialName("member_count")
        val memberCount: Int? = null,
        @SerialName("voice_states")
        val voiceStates: List<DiscordVoiceState>? = null,
        val members: List<DiscordGuildMember>? = null,
        val channels: List<DiscordChannel>? = null,
        val presences: List<DiscordPresenceUpdateData>? = null,
        @SerialName("max_presences")
        val maxPresences: Int? = null,
        @SerialName("max_members")
        val maxMembers: Int? = null,
        @SerialName("vanity_url_code")
        val vanityUrlCode: String? = null,
        val description: String? = null,
        val banner: String? = null,
        @SerialName("premium_tier")
        val premiumTier: PremiumTier,
        @SerialName("premium_subscription_count")
        val premiumSubscriptionCount: Int? = null,
        @SerialName("preferred_locale")
        val preferredLocale: String,
        @SerialName("public_updates_channel_id")
        val publicUpdatesChannelId: String? = null,

        /**
         * Approximate number of members in this guild,
         * returned from the GET /guild/<id> endpoint when with_counts is true
         */
        @SerialName("approximate_member_count")
        val approximateMemberCount: Int? = null,

        /**
         * Approximate number of online members in this guild,
         * returned from the GET /guild/<id> endpoint when with_counts is true
         */
        @SerialName("approximate_presence_count")
        val approximatePresenceCount: Int? = null
)

@Serializable(with = GuildFeature.Companion::class)
enum class GuildFeature(val value: String) {
    Unknown(""),
    InviteSplash("INVITE_SPLASH"),
    VIPRegions("VIP_REGIONS"),
    VanityUrl("VANITY_URL"),
    Verified("VERIFIED"),
    Partnered("PARTNERED"),
    Public("PUBLIC"),
    Commerce("COMMERCE"),
    News("NEWS"),
    Discoverable("DISCOVERABLE"),
    Featureable("FEATURABLE"),
    AnimatedIcon("ANIMATED_ICON"),
    Banner("BANNER"),
    PublicDisabled("PUBLIC_DISABLED"),

    /**
     * guild has enabled the welcome screen
     */
    WelcomeScreenEnabled("WELCOME_SCREEN_ENABLED");

    @Serializer(forClass = GuildFeature::class)
    companion object : KSerializer<GuildFeature> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("feature", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): GuildFeature {
            val name = decoder.decodeString()
            return values().firstOrNull { it.value == name } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: GuildFeature) {
            encoder.encodeString(obj.value)
        }
    }
}

@Serializable(with = SystemChannelFlags.Companion::class)
class SystemChannelFlags constructor(val code: Int) {

    operator fun contains(flag: SystemChannelFlags): Boolean {
        return this.code and flag.code == flag.code
    }

    @Serializer(forClass = SystemChannelFlags::class)
    companion object : KSerializer<SystemChannelFlags> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("system_channel_flags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): SystemChannelFlags {
            return SystemChannelFlags(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, obj: SystemChannelFlags) {
            encoder.encodeInt(obj.code)
        }
    }

}

enum class SystemChannelFlag(val code: Int) {
    /** Suppress member join notifications. **/
    SuppressJoinNotifications(1.shl(0)),
    /** Suppress server boost notifications. **/
    SuppressPremiumSubscriptions(1.shl(1))
}

@Serializable
@KordUnstableApi
data class DiscordPartialGuild(
        val id: String,
        val name: String,
        val icon: String? = null,
        val owner: Boolean? = null,
        val permissions: Permissions? = null
)

@Serializable
@KordUnstableApi
data class DiscordGuildBan(
        @SerialName("guild_id")
        val guildId: String,
        val user: DiscordUser
)

@Serializable
@KordUnstableApi
data class DiscordGuildIntegrations(
        @SerialName("guild_id")
        val guildId: String
)

@Serializable
@KordUnstableApi
data class DiscordIntegrationAccount(val id: String,
                                     val name: String)


@Serializable
@KordUnstableApi
data class DiscordVoiceServerUpdateData(
        val token: String,
        @SerialName("guild_id")
        val guildId: String,
        val endpoint: String
)

@Serializable
@KordUnstableApi
data class DiscordWebhooksUpdateData(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("channel_id")
        val channelId: String
)

@Serializable
@KordUnstableApi
data class DiscordVoiceState(
        @SerialName("guild_id")
        val guildId: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null,
        @SerialName("user_id")
        val userId: String,
        @SerialName("guild_member")
        val member: DiscordGuildMember? = null,
        @SerialName("session_id")
        val sessionId: String,
        val deaf: Boolean,
        val mute: Boolean,
        @SerialName("self_deaf")
        val selfDeaf: Boolean,
        @SerialName("self_mute")
        val selfMute: Boolean,
        @SerialName("self_stream")
        val selfStream: Boolean? = null,
        val suppress: Boolean
)

@Serializable(with = PremiumTier.PremiumTierSerializer::class)
enum class PremiumTier(val level: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    None(0),
    One(1),
    Two(2),
    Three(3);

    @Serializer(forClass = PremiumTier::class)
    companion object PremiumTierSerializer : KSerializer<PremiumTier> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("premium_tier", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): PremiumTier {
            val level = decoder.decodeInt()
            return values().firstOrNull { it.level == level } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: PremiumTier) {
            encoder.encodeInt(obj.level)
        }

    }
}

@Serializable(with = DefaultMessageNotificationLevel.DefaultMessageNotificationLevelSerializer::class)
enum class DefaultMessageNotificationLevel(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    AllMessages(0),
    OnlyMentions(1);

    @Serializer(forClass = DefaultMessageNotificationLevel::class)
    companion object DefaultMessageNotificationLevelSerializer : KSerializer<DefaultMessageNotificationLevel> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("default_message_notifications", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): DefaultMessageNotificationLevel {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: DefaultMessageNotificationLevel) {
            encoder.encodeInt(obj.code)
        }
    }

}

@Serializable(with = ExplicitContentFilter.ExplicitContentFilterSerializer::class)
enum class ExplicitContentFilter(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    Disabled(0),
    MembersWithoutRoles(1),
    AllMembers(2);

    @Serializer(forClass = ExplicitContentFilter::class)
    companion object ExplicitContentFilterSerializer : KSerializer<ExplicitContentFilter> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("explicit_content_filter", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ExplicitContentFilter {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: ExplicitContentFilter) {
            encoder.encodeInt(obj.code)
        }

    }
}

@Serializable(with = MFALevel.MFALevelSerializer::class)
enum class MFALevel(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    None(0),
    Elevated(1);

    @Serializer(forClass = MFALevel::class)
    object MFALevelSerializer : KSerializer<MFALevel> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("mfa_level", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): MFALevel {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: MFALevel) {
            encoder.encodeInt(obj.code)
        }
    }
}


@Serializable(with = VerificationLevel.VerificationLevelSerializer::class)
enum class VerificationLevel(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    None(0),
    Low(1),
    Medium(2),
    High(3),
    VeryHigh(4);

    @Serializer(forClass = VerificationLevel::class)
    companion object VerificationLevelSerializer : KSerializer<VerificationLevel> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("verification_level", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): VerificationLevel {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: VerificationLevel) {
            encoder.encodeInt(obj.code)
        }

    }
}