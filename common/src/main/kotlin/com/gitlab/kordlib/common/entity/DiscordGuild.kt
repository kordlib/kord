package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalInt
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

/**
 * A partial representation of a [DiscordGuild] that may be [unavailable].
 *
 * @param id the id of the Guild.
 * @param unavailable Whether the Guild is unavailable. Contains a value on true.
 */
@Serializable
data class DiscordUnavailableGuild(
        val id: Snowflake,
        val unavailable: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable
data class DiscordGuild(
        val id: Snowflake,
        val name: String,
        val icon: String?,
        @SerialName("icon_hash")
        val iconHash: Optional<String?> = Optional.Missing(),
        val splash: Optional<String?> = Optional.Missing(),
        @SerialName("discovery_splash")
        val discoverySplash: Optional<String?> = Optional.Missing(),
        val owner: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("owner_id")
        val ownerId: Snowflake,
        val permissions: Optional<Permissions> = Optional.Missing(),
        val region: String,
        @SerialName("afk_channel_id")
        val afkChannelId: Snowflake?,
        @SerialName("afk_timeout")
        val afkTimeout: Int,
        @SerialName("widget_enabled")
        val widgetEnabled: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("widget_channel_id")
        val widgetChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
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
        val applicationId: Snowflake?,
        @SerialName("system_channel_id")
        val systemChannelId: Snowflake?,
        @SerialName("system_channel_flags")
        val systemChannelFlags: SystemChannelFlags,
        @SerialName("rules_channel_id")
        val rulesChannelId: Snowflake?,
        @SerialName("joined_at")
        val joinedAt: Optional<String> = Optional.Missing(),
        val large: OptionalBoolean = OptionalBoolean.Missing,
        val unavailable: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("member_count")
        val memberCount: OptionalInt = OptionalInt.Missing,
        @SerialName("voice_states")
        val voiceStates: Optional<List<DiscordVoiceState>> = Optional.Missing(),
        val members: Optional<List<DiscordGuildMember>> = Optional.Missing(),
        val channels: Optional<List<DiscordChannel>> = Optional.Missing(),
        val presences: Optional<List<DiscordPresenceUpdate>> = Optional.Missing(),
        @SerialName("max_presences")
        val maxPresences: OptionalInt? = OptionalInt.Missing,
        @SerialName("max_members")
        val maxMembers: OptionalInt = OptionalInt.Missing,
        @SerialName("vanity_url_code")
        val vanityUrlCode: String?,
        val description: String?,
        val banner: String?,
        @SerialName("premium_tier")
        val premiumTier: PremiumTier,
        @SerialName("premium_subscription_count")
        val premiumSubscriptionCount: OptionalInt = OptionalInt.Missing,
        @SerialName("preferred_locale")
        val preferredLocale: String,
        @SerialName("public_updates_channel_id")
        val publicUpdatesChannelId: Snowflake?,
        @SerialName("max_video_channel_users")
        val maxVideoChannelUsers: OptionalInt = OptionalInt.Missing,
        @SerialName("approximate_member_count")
        val approximateMemberCount: OptionalInt = OptionalInt.Missing,
        @SerialName("approximate_presence_count")
        val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
)

@Serializable
class DiscordPartialGuild(
        val id: Snowflake,
        val name: String,
        val icon: String?,
        val owner: OptionalBoolean = OptionalBoolean.Missing,
        val permissions: Optional<Permissions> = Optional.Missing(),
        val features: List<GuildFeature>
)

@Serializable(with = GuildFeature.Serializer::class)
sealed class GuildFeature(val value: String) {

    override fun toString(): String = "GuildFeature(value=$value)"

    class Unknown(value: String) : GuildFeature(value)
    object InviteSplash : GuildFeature("INVITE_SPLASH")
    object VIPRegions : GuildFeature("VIP_REGIONS")
    object VanityUrl : GuildFeature("VANITY_URL")
    object Verified : GuildFeature("VERIFIED")
    object Partnered : GuildFeature("PARTNERED")
    object Community : GuildFeature("COMMUNITY")
    object Commerce : GuildFeature("COMMERCE")
    object News : GuildFeature("NEWS")
    object Discoverable : GuildFeature("DISCOVERABLE")
    object Featurable : GuildFeature("FEATURABLE")
    object AnimatedIcon : GuildFeature("ANIMATED_ICON")
    object Banner : GuildFeature("BANNER")
    object WelcomeScreenEnabled : GuildFeature("WELCOME_SCREEN_ENABLED")

    internal object Serializer : KSerializer<GuildFeature> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("feature", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): GuildFeature = when (val value = decoder.decodeString()) {
            "INVITE_SPLASH" -> InviteSplash
            "VIP_REGIONS" -> VIPRegions
            "VANITY_URL" -> VanityUrl
            "VERIFIED" -> Verified
            "PARTNERED" -> Partnered
            "COMMUNITY" -> Community
            "COMMERCE" -> Commerce
            "NEWS" -> News
            "DISCOVERABLE" -> Discoverable
            "FEATURABLE" -> Featurable
            "ANIMATED_ICON" -> AnimatedIcon
            "BANNER" -> Banner
            "WELCOME_SCREEN_ENABLED" -> WelcomeScreenEnabled
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: GuildFeature) {
            encoder.encodeString(value.value)
        }
    }
}

@Serializable(with = SystemChannelFlags.Companion::class)
class SystemChannelFlags constructor(val code: Int) {

    operator fun contains(flag: SystemChannelFlags): Boolean {
        return this.code and flag.code == flag.code
    }

    override fun equals(other: Any?): Boolean {
        return (other as? SystemChannelFlags ?: return false).code == code
    }

    companion object : KSerializer<SystemChannelFlags> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("system_channel_flags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): SystemChannelFlags {
            return SystemChannelFlags(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: SystemChannelFlags) {
            encoder.encodeInt(value.code)
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
data class DiscordGuildBan(
        @SerialName("guild_id")
        val guildId: String,
        val user: DiscordUser,
)

@Serializable
data class DiscordGuildIntegrations(
        @SerialName("guild_id")
        val guildId: Snowflake,
)

@Serializable
data class DiscordIntegrationAccount(
        val id: String,
        val name: String,
)


@Serializable
data class DiscordVoiceServerUpdateData(
        val token: String,
        @SerialName("guild_id")
        val guildId: Snowflake,
        val endpoint: String,
)

@Serializable
data class DiscordWebhooksUpdateData(
        @SerialName("guild_id")
        val guildId: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake,
)

/**
 * A representation of the [Discord Voice State structure](https://discord.com/developers/docs/resources/voice#voice-state-object).
 * Used to represent a user's voice connection status.
 *
 * @param guildId the guild id this voice state is for.
 * @param channelId the channel id this user is connection to.
 * @param userId The user id this voice state is for.
 * @param member the guild member this voice state is for.
 * @param sessionId The session id for this voice state.
 * @param deaf Whether this user is deafened by the server.
 * @param mute Whether this user is muted by the server.
 * @param selfDeaf Whether this user is locally deafened.
 * @param selfMute Whether this is locally muted
 * @param selfStream Whether this user is stream using "Go Live".
 * @param selfVideo Whether this user's camera is enabled.
 * @param suppress Whether this user is muted by the current user.
 */
@Serializable
data class DiscordVoiceState(
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("channel_id")
        val channelId: Snowflake?,
        @SerialName("user_id")
        val userId: Snowflake,
        @SerialName("guild_member")
        val member: Optional<DiscordGuildMember> = Optional.Missing(),
        @SerialName("session_id")
        val sessionId: String,
        val deaf: Boolean,
        val mute: Boolean,
        @SerialName("self_deaf")
        val selfDeaf: Boolean,
        @SerialName("self_mute")
        val selfMute: Boolean,
        @SerialName("self_video")
        val selfVideo: Boolean,
        @SerialName("self_stream")
        val selfStream: OptionalBoolean = OptionalBoolean.Missing,
        val suppress: Boolean,
)

/**
 * A representation of the [Discord Voice Region structure](https://discord.com/developers/docs/resources/voice#voice-region-object).
 *
 * @param id Unique id for the region.
 * @param name Name of the region.
 * @param vip True if this is a vip-only server.
 * @param optimal True for a single server that is closest to the current user's client.
 * @param deprecated Whether this is a deprecated voice server (avoid switching to these).
 * @param custom Whether this is a custom voice region (used for events/etc).
 */
@Serializable
data class DiscordVoiceRegion(
        val id: String,
        val name: String,
        val vip: Boolean,
        val optimal: Boolean,
        val deprecated: Boolean,
        val custom: Boolean,
)

@Serializable(with = PremiumTier.Serializer::class)
sealed class PremiumTier(val value: Int) {
    class Unknown(value: Int) : PremiumTier(value)
    object None : PremiumTier(0)
    object One : PremiumTier(1)
    object Two : PremiumTier(2)
    object Three : PremiumTier(3)

    internal object Serializer : KSerializer<PremiumTier> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.PremiumTier", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): PremiumTier = when (val value = decoder.decodeInt()) {
            0 -> None
            1 -> One
            2 -> Two
            3 -> Three
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: PremiumTier) {
            encoder.encodeInt(value.value)
        }

    }
}

@Serializable(with = DefaultMessageNotificationLevel.Serializer::class)
sealed class DefaultMessageNotificationLevel(val value: Int) {
    class Unknown(value: Int) : DefaultMessageNotificationLevel(value)
    object AllMessages : DefaultMessageNotificationLevel(0)
    object OnlyMentions : DefaultMessageNotificationLevel(1)

    internal object Serializer : KSerializer<DefaultMessageNotificationLevel> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("default_message_notifications", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): DefaultMessageNotificationLevel = when (val value = decoder.decodeInt()) {
            0 -> AllMessages
            1 -> OnlyMentions
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: DefaultMessageNotificationLevel) {
            encoder.encodeInt(value.value)
        }
    }

}

@Serializable(with = ExplicitContentFilter.Serializer::class)
sealed class ExplicitContentFilter(val value: Int) {
    class Unknown(value: Int) : ExplicitContentFilter(value)
    object Disabled : ExplicitContentFilter(0)
    object MembersWithoutRoles : ExplicitContentFilter(1)
    object AllMembers : ExplicitContentFilter(2)

    internal object Serializer : KSerializer<ExplicitContentFilter> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("explicit_content_filter", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ExplicitContentFilter = when (val value = decoder.decodeInt()) {
            0 -> Disabled
            1 -> MembersWithoutRoles
            2 -> AllMembers
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: ExplicitContentFilter) {
            encoder.encodeInt(value.value)
        }

    }
}

@Serializable(with = MFALevel.Serializer::class)
sealed class MFALevel(val value: Int) {
    class Unknown(value: Int) : MFALevel(value)
    object None : MFALevel(0)
    object Elevated : MFALevel(1)

    internal object Serializer : KSerializer<MFALevel> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.MFALevel", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): MFALevel = when (val value = decoder.decodeInt()) {
            0 -> None
            1 -> Elevated
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: MFALevel) {
            encoder.encodeInt(value.value)
        }
    }
}


@Serializable(with = VerificationLevel.Serializer::class)
sealed class VerificationLevel(val value: Int) {
    class Unknown(value: Int) : VerificationLevel(value)
    object None : VerificationLevel(0)
    object Low : VerificationLevel(1)
    object Medium : VerificationLevel(2)
    object High : VerificationLevel(3)
    object VeryHigh : VerificationLevel(4)

    internal object Serializer : KSerializer<VerificationLevel> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.VerificationLevel", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): VerificationLevel = when (val value = decoder.decodeInt()) {
            0 -> None
            1 -> Low
            2 -> Medium
            3 -> High
            4 -> VeryHigh
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: VerificationLevel) {
            encoder.encodeInt(value.value)
        }

    }
}
