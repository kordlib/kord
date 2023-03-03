@file:GenerateKordEnum(
    name = "DefaultMessageNotificationLevel", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-object-default-message-notification-level",
    entries = [
        Entry("AllMessages", intValue = 0, kDoc = "Members will receive notifications for all messages by default."),
        Entry(
            "OnlyMentions", intValue = 1,
            kDoc = "Members will receive notifications only for messages that @mention them by default.",
        ),
    ],
)

@file:GenerateKordEnum(
    name = "ExplicitContentFilter", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-object-explicit-content-filter-level",
    entries = [
        Entry("Disabled", intValue = 0, kDoc = "Media content will not be scanned."),
        Entry(
            "MembersWithoutRoles", intValue = 1,
            kDoc = "Media content sent by members without roles will be scanned.",
        ),
        Entry("AllMembers", intValue = 2, kDoc = "Media content sent by all members will be scanned."),
    ],
)

@file:GenerateKordEnum(
    name = "MFALevel", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-object-mfa-level",
    entries = [
        Entry("None", intValue = 0, kDoc = "Guild has no MFA/2FA requirement for moderation actions."),
        Entry("Elevated", intValue = 1, kDoc = "Guild has a 2FA requirement for moderation actions."),
    ],
)

@file:GenerateKordEnum(
    name = "VerificationLevel", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-object-verification-level",
    entries = [
        Entry("None", intValue = 0, kDoc = "Unrestricted."),
        Entry("Low", intValue = 1, kDoc = "Must have verified email on account."),
        Entry("Medium", intValue = 2, kDoc = "Must be registered on Discord for longer than 5 minutes."),
        Entry("High", intValue = 3, kDoc = "Must be a member of the server for longer than 10 minutes."),
        Entry("VeryHigh", intValue = 4, kDoc = "Must have a verified phone number."),
    ],
)

@file:GenerateKordEnum(
    name = "NsfwLevel", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-object-guild-nsfw-level",
    entries = [
        Entry("Default", intValue = 0),
        Entry("Explicit", intValue = 1),
        Entry("Safe", intValue = 2),
        Entry("AgeRestricted", intValue = 3),
    ],
)

@file:GenerateKordEnum(
    name = "PremiumTier", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-object-premium-tier",
    entries = [
        Entry("None", intValue = 0, kDoc = "Guild has not unlocked any Server Boost perks."),
        Entry("One", intValue = 1, kDoc = "Guild has unlocked Server Boost level 1 perks."),
        Entry("Two", intValue = 2, kDoc = "Guild has unlocked Server Boost level 2 perks."),
        Entry("Three", intValue = 3, kDoc = "Guild has unlocked Server Boost level 3 perks."),
    ],
)

@file:GenerateKordEnum(
    name = "GuildFeature", valueType = STRING,
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-object-guild-features",
    entries = [
        Entry(
            "AnimatedBanner", stringValue = "ANIMATED_BANNER",
            kDoc = "Guild has access to set an animated guild banner image.",
        ),
        Entry("AnimatedIcon", stringValue = "ANIMATED_ICON", kDoc = "Guild has access to set an animated guild icon."),
        Entry(
            "ApplicationCommandPermissionsV2", stringValue = "APPLICATION_COMMAND_PERMISSIONS_V2",
            kDoc = "Guild is using the old permissions configuration behavior.",
        ),
        Entry("AutoModeration", stringValue = "AUTO_MODERATION", kDoc = "Guild has set up auto moderation rules."),
        Entry("Banner", stringValue = "BANNER", kDoc = "Guild has access to set a guild banner image."),
        Entry(
            "Community", stringValue = "COMMUNITY",
            kDoc = "Guild can enable welcome screen, Membership Screening, stage channels and discovery, and " +
                    "receives community updates.",
        ),
        Entry(
            "CreatorMonetizableProvision", stringValue = "CREATOR_MONETIZABLE_PROVISIONAL",
            kDoc = "Guild has enabled monetization."
        ),
        Entry(
            "CreatorStorePage", stringValue = "CREATOR_STORE_PAGE",
            kDoc = "Guild has enabled the role subscription promo page."
        ),
        Entry(
            "DeveloperSupportServer", stringValue = "DEVELOPER_SUPPORT_SERVER",
            kDoc = "Guild has been set as a support server on the App Directory.",
        ),
        Entry("Discoverable", stringValue = "DISCOVERABLE", kDoc = "Guild is able to be discovered in the directory."),
        Entry(
            "Featurable", stringValue = "FEATURABLE",
            kDoc = "Guild is able to be featured in the directory.",
        ),
        Entry(
            "InvitesDisabled", stringValue = "INVITES_DISABLED",
            kDoc = "Guild has paused invites, preventing new users from joining.",
        ),
        Entry(
            "InviteSplash", stringValue = "INVITE_SPLASH",
            kDoc = "Guild has access to set an invite splash background.",
        ),
        Entry(
            "MemberVerificationGateEnabled", stringValue = "MEMBER_VERIFICATION_GATE_ENABLED",
            kDoc = "Guild has enabled Membership Screening.",
        ),
        Entry("MonetizationEnabled", stringValue = "MONETIZATION_ENABLED", kDoc = "Guild has enabled monetization."),
        Entry("MoreStickers", stringValue = "MORE_STICKERS", kDoc = "Guild has increased custom sticker slots."),
        Entry("News", stringValue = "NEWS", kDoc = "Guild has access to create announcement channels."),
        Entry("Partnered", stringValue = "PARTNERED", kDoc = "Guild is partnered."),
        Entry(
            "PreviewEnabled", stringValue = "PREVIEW_ENABLED",
            kDoc = "Guild can be previewed before joining via Membership Screening or the directory.",
        ),
        Entry("RoleIcons", stringValue = "ROLE_ICONS", kDoc = "Guild is able to set role icons."),
        Entry("RoleSubscriptionsAvailableForPurchase", stringValue = "ROLE_SUBSCRIPTIONS_AVAILABLE_FOR_PURCHASE",
            kDoc = "Guild has role subscriptions that can be purchased."
        ),
        Entry("RoleSubscriptionsEnabled", stringValue = "ROLE_SUBSCRIPTIONS_ENABLED",
            kDoc = "Guild has enabled role subscriptions."
        ),
        Entry(
            "TicketedEventsEnabled", stringValue = "TICKETED_EVENTS_ENABLED",
            kDoc = "Guild has enabled ticketed events.",
        ),
        Entry("VanityUrl", stringValue = "VANITY_URL", kDoc = "Guild has access to set a vanity URL."),
        Entry("Verified", stringValue = "VERIFIED", kDoc = "Guild is verified."),
        Entry(
            "VIPRegions", stringValue = "VIP_REGIONS",
            kDoc = "Guild has access to set 384kbps bitrate in voice (previously VIP voice servers).",
        ),
        Entry(
            "WelcomeScreenEnabled", stringValue = "WELCOME_SCREEN_ENABLED",
            kDoc = "Guild has enabled the welcome screen.",
        ),
    ],
    deprecatedEntries = [
        Entry(
            "Commerce", stringValue = "COMMERCE",
            kDoc = "Guild has access to use commerce features (i.e. create store channels).\n\n@suppress",
            deprecationMessage = "Discord no longer offers the ability to purchase a license to sell PC games on " +
                    "Discord and store channels were removed on March 10, 2022. See " +
                    "https://support-dev.discord.com/hc/en-us/articles/6309018858647-Self-serve-Game-Selling-Deprecation" +
                    " for more information.",
            deprecationLevel = HIDDEN,
        ),
        Entry(
            "PrivateThreads", stringValue = "PRIVATE_THREADS", kDoc = "Guild has access to create private threads",
            deprecationMessage = "Creating a private thread no longer requires the server to be boosted.",
            deprecationLevel = WARNING,
        ),
        Entry(
            "SevenDayThreadArchive", stringValue = "SEVEN_DAY_THREAD_ARCHIVE",
            kDoc = "Guild has access to the seven day archive time for threads.\n\n@suppress",
            deprecationMessage = "Thread archive durations are no longer boost locked.", deprecationLevel = HIDDEN,
        ),
        Entry(
            "ThreeDayThreadArchive", stringValue = "THREE_DAY_THREAD_ARCHIVE",
            kDoc = "Guild has access to the three-day archive time for threads.\n\n@suppress",
            deprecationMessage = "Thread archive durations are no longer boost locked.", deprecationLevel = HIDDEN,
        ),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInSeconds
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.DeprecationLevel.HIDDEN
import kotlin.DeprecationLevel.WARNING

/**
 * A partial representation of a [DiscordGuild] that may be [unavailable].
 *
 * @param id the id of the Guild.
 * @param unavailable Whether the Guild is unavailable. Contains a value on true.
 */
@Serializable
public data class DiscordUnavailableGuild(
    val id: Snowflake,
    val unavailable: OptionalBoolean = OptionalBoolean.Missing,
)

/**
 * A representation of a [Discord Guild structure](https://discord.com/developers/docs/resources/guild#guild-object)
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
 * @param afkTimeout The afk timeout.
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
 * @param description The description for the guild.
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
public data class DiscordGuild(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    @SerialName("icon_hash") val iconHash: Optional<String?> = Optional.Missing(),
    val splash: Optional<String?> = Optional.Missing(),
    @SerialName("discovery_splash") val discoverySplash: Optional<String?> = Optional.Missing(),
    val owner: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("owner_id") val ownerId: Snowflake,
    val permissions: Optional<Permissions> = Optional.Missing(),
    @Deprecated(
        "The region field has been moved to Channel#rtcRegion in Discord API v9",
        ReplaceWith("DiscordChannel#rtcRegion")
    ) val region: String,
    @SerialName("afk_channel_id") val afkChannelId: Snowflake?,
    @SerialName("afk_timeout") val afkTimeout: DurationInSeconds,
    @SerialName("widget_enabled") val widgetEnabled: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("widget_channel_id") val widgetChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("verification_level") val verificationLevel: VerificationLevel,
    @SerialName("default_message_notifications") val defaultMessageNotifications: DefaultMessageNotificationLevel,
    @SerialName("explicit_content_filter") val explicitContentFilter: ExplicitContentFilter,
    val roles: List<DiscordRole>,
    val emojis: List<DiscordEmoji>,
    val features: List<GuildFeature>,
    @SerialName("mfa_level") val mfaLevel: MFALevel,
    @SerialName("application_id") val applicationId: Snowflake?,
    @SerialName("system_channel_id") val systemChannelId: Snowflake?,
    @SerialName("system_channel_flags") val systemChannelFlags: SystemChannelFlags,
    @SerialName("rules_channel_id") val rulesChannelId: Snowflake?,
    @SerialName("joined_at") val joinedAt: Optional<Instant> = Optional.Missing(),
    val large: OptionalBoolean = OptionalBoolean.Missing,
    val unavailable: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("member_count") val memberCount: OptionalInt = OptionalInt.Missing,
    @SerialName("voice_states") val voiceStates: Optional<List<DiscordVoiceState>> = Optional.Missing(),
    val members: Optional<List<DiscordGuildMember>> = Optional.Missing(),
    val channels: Optional<List<DiscordChannel>> = Optional.Missing(),
    val threads: Optional<List<DiscordChannel>> = Optional.Missing(),
    val presences: Optional<List<DiscordPresenceUpdate>> = Optional.Missing(),
    @SerialName("max_presences") val maxPresences: OptionalInt? = OptionalInt.Missing,
    @SerialName("max_members") val maxMembers: OptionalInt = OptionalInt.Missing,
    @SerialName("vanity_url_code") val vanityUrlCode: String?,
    val description: String?,
    val banner: String?,
    @SerialName("premium_tier") val premiumTier: PremiumTier,
    @SerialName("premium_subscription_count") val premiumSubscriptionCount: OptionalInt = OptionalInt.Missing,
    @SerialName("preferred_locale") val preferredLocale: String,
    @SerialName("public_updates_channel_id") val publicUpdatesChannelId: Snowflake?,
    @SerialName("max_video_channel_users") val maxVideoChannelUsers: OptionalInt = OptionalInt.Missing,
    @SerialName("approximate_member_count") val approximateMemberCount: OptionalInt = OptionalInt.Missing,
    @SerialName("approximate_presence_count") val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
    @SerialName("welcome_screen") val welcomeScreen: Optional<DiscordWelcomeScreen> = Optional.Missing(),
    @SerialName("nsfw_level") val nsfwLevel: NsfwLevel,
    @SerialName("stage_instances")
    val stageInstances: Optional<List<DiscordStageInstance>> = Optional.Missing(),
    val stickers: Optional<List<DiscordMessageSticker>> = Optional.Missing(),
    @SerialName("guild_scheduled_events")
    val guildScheduledEvents: Optional<List<DiscordGuildScheduledEvent>> = Optional.Missing(),
    @SerialName("premium_progress_bar_enabled")
    val premiumProgressBarEnabled: Boolean
)

/**
 * A partial representation of a [Discord Guild structure](https://discord.com/developers/docs/resources/guild#guild-object)
 *
 * see [Get Current User Guilds](https://discord.com/developers/docs/resources/user#get-current-user-guilds)
 *
 * @param id The guild id.
 * @param name The guild name (2-100 characters, excluding trailing and leading whitespace)
 * @param icon The icon hash.
 * @param owner True if [DiscordUser] is the owner of the guild.
 * @param features The enabled guild features.
 */
@Serializable
public data class DiscordPartialGuild(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val owner: OptionalBoolean = OptionalBoolean.Missing,
    val permissions: Optional<Permissions> = Optional.Missing(),
    val features: List<GuildFeature>,
    @SerialName("welcome_screen") val welcomeScreen: Optional<DiscordWelcomeScreen> = Optional.Missing(),
    @SerialName("vanity_url_code") val vanityUrlCode: Optional<String?> = Optional.Missing(),
    val description: Optional<String?> = Optional.Missing(),
    val banner: Optional<String?> = Optional.Missing(),
    val splash: Optional<String?> = Optional.Missing(),
    @SerialName("nsfw_level") val nsfwLevel: Optional<NsfwLevel> = Optional.Missing(),
    @SerialName("verification_level")
    val verificationLevel: Optional<VerificationLevel> = Optional.Missing(),
    @SerialName("stage_instances")
    val stageInstances: Optional<List<DiscordStageInstance>> = Optional.Missing(),
    val stickers: Optional<List<DiscordMessageSticker>> = Optional.Missing(),
    @SerialName("guild_scheduled_events")
    val guildScheduledEvents: Optional<List<DiscordGuildScheduledEvent>> = Optional.Missing(),
    @SerialName("premium_progress_bar_enabled")
    val premiumProgressBarEnabled: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable(with = SystemChannelFlags.Companion::class)
public data class SystemChannelFlags(val code: Int) {

    public operator fun contains(flag: SystemChannelFlags): Boolean {
        return this.code and flag.code == flag.code
    }

    public companion object : KSerializer<SystemChannelFlags> {

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
public enum class SystemChannelFlag(public val code: Int) {
    /** Suppress member join notifications. **/
    SuppressJoinNotifications(1.shl(0)),

    /** Suppress server boost notifications. **/
    SuppressPremiumSubscriptions(1.shl(1)),

    /** Suppress guild reminder notifications **/
    SuppressGuildReminderNotifications(1.shl(2)),

    /** Suppress join notification replies **/
    SuppressJoinNotificationReplies(1.shl(3)),

    /** Suppress role subscription notifications **/
    SuppressRoleSubscriptionPurchaseNotifications(1.shl(4)),

    /** Suppress role subscription notification replies **/
    SuppressRoleSubscriptionPurchaseNotificationReplies(1.shl(5)),
}

@Serializable
public data class DiscordGuildBan(
    @SerialName("guild_id") val guildId: Snowflake,
    val user: DiscordUser,
)

@Serializable
public data class DiscordGuildIntegrations(
    @SerialName("guild_id") val guildId: Snowflake,
)

@Serializable
public data class DiscordGuildIntegrationsDeleted(
    val id: Snowflake,
    @SerialName("guild_id") val guildId: Snowflake,
    @SerialName("application_id") val applicationId: Snowflake?
)

@Serializable
public data class DiscordIntegrationAccount(
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
public data class DiscordVoiceServerUpdateData(
    val token: String,
    @SerialName("guild_id") val guildId: Snowflake,
    val endpoint: String?,
)

@Serializable
public data class DiscordWebhooksUpdateData(
    @SerialName("guild_id") val guildId: Snowflake,
    @SerialName("channel_id") val channelId: Snowflake,
)

/**
 * A representation of the [Discord Voice State structure](https://discord.com/developers/docs/resources/voice#voice-state-object).
 * Used to represent a user's voice connection status.
 *
 * @param guildId The guild id this voice state is for.
 * @param channelId The channel id this user is connected to.
 * @param userId The user id this voice state is for.
 * @param member The guild member this voice state is for.
 * @param sessionId The session id for this voice state.
 * @param deaf Whether this user is deafened by the server.
 * @param mute Whether this user is muted by the server.
 * @param selfDeaf Whether this user is locally deafened.
 * @param selfMute Whether this user is locally muted.
 * @param selfStream Whether this user is stream using "Go Live".
 * @param selfVideo Whether this user's camera is enabled.
 * @param suppress Whether this user is muted by the current user.
 * @param requestToSpeakTimestamp The time at which the user requested to speak.
 */
@Serializable
public data class DiscordVoiceState(
    @SerialName("guild_id") val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("channel_id") val channelId: Snowflake?,
    @SerialName("user_id") val userId: Snowflake,
    @SerialName("guild_member") val member: Optional<DiscordGuildMember> = Optional.Missing(),
    @SerialName("session_id") val sessionId: String,
    val deaf: Boolean,
    val mute: Boolean,
    @SerialName("self_deaf") val selfDeaf: Boolean,
    @SerialName("self_mute") val selfMute: Boolean,
    @SerialName("self_video") val selfVideo: Boolean,
    @SerialName("self_stream") val selfStream: OptionalBoolean = OptionalBoolean.Missing,
    val suppress: Boolean,
    @SerialName("request_to_speak_timestamp") val requestToSpeakTimestamp: Instant?,
)

/**
 * A representation of the [Discord Voice Region structure](https://discord.com/developers/docs/resources/voice#voice-region-object).
 *
 * @param id Unique id for the region.
 * @param name Name of the region.
 * @param optimal True for a single server that is closest to the current user's client.
 * @param deprecated Whether this is a deprecated voice server (avoid switching to these).
 * @param custom Whether this is a custom voice region (used for events/etc).
 */
@Serializable
public data class DiscordVoiceRegion(
    val id: String,
    val name: String,
    val optimal: Boolean,
    val deprecated: Boolean,
    val custom: Boolean,
)

@Serializable
public data class DiscordWelcomeScreenChannel(
    @SerialName("channel_id") val channelId: Snowflake,
    val description: String,
    @SerialName("emoji_id") val emojiId: Snowflake?,
    @SerialName("emoji_name") val emojiName: String?
)

@Serializable
public data class DiscordWelcomeScreen(
    val description: String?,
    @SerialName("welcome_channels") val welcomeChannels: List<DiscordWelcomeScreenChannel>,
)
