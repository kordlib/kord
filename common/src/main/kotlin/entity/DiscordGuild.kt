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

/**
 * A representation of a [Discord Guild structure](https://discord.com/developers/docs/resources/guild#guild-object
 *
 * @param id The guild id.
 * @param name The guild name (2-100 characters, excluding trailing and leading whitespace)
 * @param icon The icon hash.
 * @param iconHash The icon hash, returned when in the template object.
 * @param splash The splash hash.
 * @param discoverySplash The discovery splash hash; only present for guilds with the [GuildFeature.Discoverable] feature.
 * @param owner True if [DiscordUser] is the owner of the guild.
 * @param ownerId The id of the owner.
 * @param permissions The total permissions for [DiscordUser] in the guild (excludes [overwrites][Overwrite]).
 * @param region [DiscordVoiceRegion] id for the guild.
 * @param afkChannelId The id of afk channel.
 * @param afkTimeout The afk timeout in seconds.
 * @param widgetEnabled True if the server widget is enabled.
 * @param widgetChannelId The channel id that the widget will generate an invite to, or `null` if set to no invite.
 * @param verificationLevel [VerificationLevel] required for the guild.
 * @param defaultMessageNotifications The [DefaultMessageNotificationLevel].
 * @param explicitContentFilter The [ExplicitContentFilter].
 * @param roles The roles in the guild.
 * @param emojis The custom guild emojis.
 * @param features The enabled guild features.
 * @param mfaLevel The required [MFALevel] for the guild.
 * @param applicationId The application id of the guild creator if it is bot-created.
 * @param systemChannelId The id of the channel where guild notices such as welcome messages and boost events are posted.
 * @param systemChannelFlags [SystemChannelFlags].
 * @param rulesChannelId The id of the channel where Community guilds can display rules and/or guidelines.
 * @param joinedAt When this guild was joined at.
 * @param large True if this is considered a large guild.
 * @param unavailable True if this guild is unavailable due to an outage.
 * @param memberCount The total number of members in this guild.
 * @param voiceStates The states of members currently in voice channels; lacks the [DiscordVoiceState.guildId] key.
 * @param members The users in the guild.
 * @param channels The channels in the guild.
 * @param presences The presences of the members in the guild, will only include non-offline members if the size is greater than `large threshold`.
 * @param maxPresences The maximum number of presences for the guild (the default value, currently 25000, is in effect when `null` is returned).
 * @param maxMembers The maximum number of members for the guild.
 * @param vanityUrlCode The vanity url code for the guild.
 * @param description The description for the guild, if the guild is discoverable.
 * @param banner The banner hash.
 * @param premiumTier The [PremiumTier] (Server Boost level).
 * @param premiumSubscriptionCount The number of boosts this guild currently has.
 * @param preferredLocale The preferred locale of a Community guild; used in server discovery and notices from Discord; defaults to "en-US".
 * @param publicUpdatesChannelId The id of the channel where admins and moderators of Community guilds receive notices from Discord.
 * @param maxVideoChannelUsers The maximum amount of users in a video channel.
 * @param approximateMemberCount The approximate number of members in this guild, returned from the `GET /guild/<id>` endpoint when `with_counts` is `true`.
 * @param approximatePresenceCount The approximate number of non-offline members in this guild, returned from the `GET /guild/<id>` endpoint when `with_counts` is `true`.
 * @param welcomeScreen The welcome screen of a Community guild, shown to new members.
 * @param nsfwLevel Guild NSFW level.
 */
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
    val threads: Optional<List<DiscordChannel>> = Optional.Missing(),
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
    @SerialName("welcome_screen")
    val welcomeScreen: Optional<DiscordWelcomeScreen> = Optional.Missing(),
    @SerialName("nsfw_level")
    val nsfwLevel: NsfwLevel
)

/**
 * A partial representation of a [Discord Guild structure](https://discord.com/developers/docs/resources/guild#guild-object
 *
 * @param id The guild id.
 * @param name The guild name (2-100 characters, excluding trailing and leading whitespace)
 * @param icon The icon hash.
 * @param owner True if [DiscordUser] is the owner of the guild.
 * @param features The enabled guild features.
 */
@Serializable
class DiscordPartialGuild(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val owner: OptionalBoolean = OptionalBoolean.Missing,
    val permissions: Optional<Permissions> = Optional.Missing(),
    val features: List<GuildFeature>,
    @SerialName("welcome_screen")
    val welcomeScreen: Optional<DiscordWelcomeScreen> = Optional.Missing()
)

/**
 * A representation of a [Discord Guild Feature](https://discord.com/developers/docs/resources/guild#guild-object-guild-features).
 */
@Serializable(with = GuildFeature.Serializer::class)
sealed class GuildFeature(val value: String) {

    override fun toString(): String = "GuildFeature(value=$value)"

    class Unknown(value: String) : GuildFeature(value)

    /** Guild has access to set an invite splash background */
    object InviteSplash : GuildFeature("INVITE_SPLASH")

    /** Guild has access to set 384kbps bitrate in voice (previously VIP voice servers) */
    object VIPRegions : GuildFeature("VIP_REGIONS")

    /** Guild has access to set a vanity URL */
    object VanityUrl : GuildFeature("VANITY_URL")

    /** Guild is verified */
    object Verified : GuildFeature("VERIFIED")

    /** Guild is partnered */
    object Partnered : GuildFeature("PARTNERED")

    /** Guild can enable welcome screen and discovery, and receives community updates */
    object Community : GuildFeature("COMMUNITY")

    /** Guild has access to use commerce features (i.e. create store channels) */
    object Commerce : GuildFeature("COMMERCE")

    /** Guild has access to create news channels */
    object News : GuildFeature("NEWS")

    /** Guild is lurkable and able to be discovered directly */
    object Discoverable : GuildFeature("DISCOVERABLE")

    /** Guild is able to be featured in the directory */
    object Featurable : GuildFeature("FEATURABLE")

    /** Guild has access to set an animated guild icon */
    object AnimatedIcon : GuildFeature("ANIMATED_ICON")

    /** Guild has access to set a guild banner image */
    object Banner : GuildFeature("BANNER")

    /** Guild has enabled the welcome screen */
    object WelcomeScreenEnabled : GuildFeature("WELCOME_SCREEN_ENABLED")

    /** Guild has enabled ticketed events */
    object TicketedEventsEnabled : GuildFeature("TICKETED_EVENTS_ENABLED")

    /** Guild has enabled monetization */
    object MonetizationEnabled : GuildFeature("MONETIZATION_ENABLED")

    /** Guild has increased custom sticker slots */
    object MoreStickers : GuildFeature("MORE_STICKERS")

    /** Guild has access to the three-day archive time for threads */
    object ThreeDayThreadArchive : GuildFeature("THREE_DAY_THREAD_ARCHIVE")

    /** Guild has access to the seven day archive time for threads */
    object SevenDayThreadArchive : GuildFeature("SEVEN_DAY_THREAD_ARCHIVE")

    /** Guild has access to create private threads */
    object PrivateThreads : GuildFeature("PRIVATE_THREADS")

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
            "TICKETED_EVENTS_ENABLED" -> TicketedEventsEnabled
            "MONETIZATION_ENABLED" -> MonetizationEnabled
            "MORE_STICKERS" -> MoreStickers
            "THREE_DAY_THREAD_ARCHIVE" -> ThreeDayThreadArchive
            "SEVEN_DAY_THREAD_ARCHIVE" -> SevenDayThreadArchive
            "PRIVATE_THREADS" -> PrivateThreads
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

/**
 * A representation of a [Discord Channels Flag](https://discord.com/developers/docs/resources/guild#guild-object-system-channel-flags).
 */
enum class SystemChannelFlag(val code: Int) {
    /** Suppress member join notifications. **/
    SuppressJoinNotifications(1.shl(0)),

    /** Suppress server boost notifications. **/
    SuppressPremiumSubscriptions(1.shl(1))
}

@Serializable
data class DiscordGuildBan(
    @SerialName("guild_id")
    val guildId: Snowflake,
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


/**
 * @param token The voice connection token.
 * @param guildId The guild id this server update is for.
 * @param endpoint The voice server host.
 * A null endpoint means that the voice server allocated has gone away and is trying to be reallocated.
 * You should attempt to disconnect from the currently connected voice server,
 * and not attempt to reconnect until a new voice server is allocated.
 */
@Serializable
data class DiscordVoiceServerUpdateData(
    val token: String,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val endpoint: String?,
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
    @SerialName("request_to_speak_timestamp")
    val requestToSpeakTimestamp: String?
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
    val optimal: Boolean,
    val deprecated: Boolean,
    val custom: Boolean,
)

/**
 * A representation of a [Discord Premium tier](https://discord.com/developers/docs/resources/guild#guild-object-premium-tier).
 */
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

        override fun deserialize(decoder: Decoder): DefaultMessageNotificationLevel =
            when (val value = decoder.decodeInt()) {
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

/**
 * A representation of a [Discord Guild NSFW Level](https://discord.com/developers/docs/resources/guild#guild-object-guild-nsfw-level).
 */
@Serializable(with = NsfwLevel.Serializer::class)
sealed class NsfwLevel(val value: Int) {
    class Unknown(value: Int) : NsfwLevel(value)

    object Default : NsfwLevel(0)

    object Explicit : NsfwLevel(1)

    object Safe : NsfwLevel(2)

    object AgeRestricted : NsfwLevel(3)

    internal object Serializer : KSerializer<NsfwLevel> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.GuildNsfwLevel", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): NsfwLevel = when (val value = decoder.decodeInt()) {
            0 -> Default
            1 -> Explicit
            2 -> Safe
            3 -> AgeRestricted
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: NsfwLevel) {
            encoder.encodeInt(value.value)
        }

    }
}


/**
 * A representation of a [Discord Verification Level](https://discord.com/developers/docs/resources/guild#guild-object-verification-level).
 */
@Serializable(with = VerificationLevel.Serializer::class)
sealed class VerificationLevel(val value: Int) {
    class Unknown(value: Int) : VerificationLevel(value)

    /** Unrestricted. */
    object None : VerificationLevel(0)

    /** Must have verified email and account.  */
    object Low : VerificationLevel(1)

    /** Must be registered on Discord for longer than 5 minutes. */
    object Medium : VerificationLevel(2)

    /** Must be member of the server for longer than 10 minutes */
    object High : VerificationLevel(3)

    /** Must have a verified phone number */
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

@Serializable
data class DiscordWelcomeScreenChannel(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val description: String,
    @SerialName("emoji_id")
    val emojiId: Snowflake?,
    @SerialName("emoji_name")
    val emojiName: String?
)

@Serializable
data class DiscordWelcomeScreen(
    val description: String?,
    @SerialName("welcome_channels")
    val welcomeChannels: List<DiscordWelcomeScreenChannel>
)
