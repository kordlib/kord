package dev.kord.rest.json

import dev.kord.rest.json.response.DiscordErrorResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Detailed error codes sent by the Discord API in the JSON [error response][DiscordErrorResponse].
 *
 * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
 */
@Serializable(with = JsonErrorCode.Serializer::class)
public enum class JsonErrorCode(public val code: Int) {

    /** Undocumented error. */
    Unknown(-1),

    /** General error (such as a malformed request body, amongst other things). */
    General(0),

    /** Unknown account. */
    UnknownAccount(10001),

    /** Unknown application. */
    UnknownApplication(10002),

    /** Unknown channel. */
    UnknownChannel(10003),

    /** Unknown guild. */
    UnknownGuild(10004),

    /** Unknown integration. */
    UnknownIntegration(10005),

    /** Unknown invite. */
    UnknownInvite(10006),

    /** Unknown member. */
    UnknownMember(10007),

    /** Unknown message. */
    UnknownMessage(10008),

    /** Unknown permission overwrite. */
    UnknownPermissionOverwrite(10009),

    /** Unknown provider. */
    UnknownProvider(10010),

    /** Unknown role. */
    UnknownRole(10011),

    /** Unknown token. */
    UnknownToken(10012),

    /** Unknown user. */
    UnknownUser(10013),

    /** Unknown emoji. */
    UnknownEmoji(10014),

    /** Unknown webhook. */
    UnknownWebhook(10015),

    /** Unknown webhook service. */
    UnknownWebhookService(10016),

    /** Unknown session. */
    UnknownSession(10020),

    /** Unknown ban. */
    UnknownBan(10026),

    /** Unknown SKU. */
    UnknownSKU(10027),

    /** Unknown store Listing. */
    UnknownStoreListing(10028),

    /** Unknown entitlement. */
    UnknownEntitlement(10029),

    /** Unknown build. */
    UnknownBuild(10030),

    /** Unknown lobby. */
    UnknownLobby(10031),

    /** Unknown branch. */
    UnknownBranch(10032),

    /** Unknown store directory layout. */
    UnknownStoreDirectoryLayout(10033),

    /** Unknown redistributable. */
    UnknownRedistributable(10036),

    /** Unknown gift code. */
    UnknownGiftCode(10038),

    /** Unknown stream. */
    UnknownStream(10049),

    /** Unknown premium server subscribe cooldown. */
    UnknownPremiumServerSubscribeCooldown(10050),

    /** Unknown guild template. */
    UnknownGuildTemplate(10057),

    /** Unknown discoverable server category. */
    UnknownDiscoverableServerCategory(10059),

    /** Unknown sticker. */
    UnknownSticker(10060),

    /** Unknown interaction. */
    UnknownInteraction(10062),

    /** Unknown application command. */
    UnknownApplicationCommand(10063),

    /** Unknown voice state. */
    UnknownVoiceState(10065),

    /** Unknown application command permissions. */
    UnknownApplicationCommandPermissions(10066),

    /** Unknown Stage Instance. */
    UnknownStageInstance(10067),

    /** Unknown Guild Member Verification Form. */
    UnknownGuildMemberVerificationForm(10068),

    /** Unknown Guild Welcome Screen. */
    UnknownGuildWelcomeScreen(10069),

    /** Unknown Guild Scheduled Event. */
    UnknownGuildScheduledEvent(10070),

    /** Unknown Guild Scheduled Event User. */
    UnknownGuildScheduledEventUser(10071),

    /** Unknown Tag. */
    UnknownTag(10087),

    /** Bots cannot use this endpoint. */
    NonBotEndpoint(20001),

    /** Only bots can use this endpoint. */
    BotsEndpoint(20002),

    /** Explicit content cannot be sent to the desired recipient(s). */
    CannotSendExplicitContent(20009),

    /** You are not authorized to perform this action on this application. */
    UnauthorizedForAction(20012),

    /** This action cannot be performed due to slow-mode rate limit. */
    SlowModeRateLimit(20016),

    /** Only the owner of this account can perform this action. */
    OnlyOwner(20018),

    /** This message cannot be edited due to announcement rate limits. */
    AnnouncementRateLimit(20022),

    /** Under minimum age. */
    UnderMinimumAge(20024),

    /** The channel you are writing has hit the write rate limit. */
    ChannelWriteRateLimit(20028),

    /** The write action you are performing on the server has hit the write rate limit. */
    ServerWriteRateLimit(20029),

    /** Your Stage topic, server name, server description, or channel names contain words that are not allowed. */
    DisallowedName(20031),

    /** Guild premium subscription level too low. */
    GuildSubscriptionTooLow(20035),

    /** Maximum number of guilds reached (100). */
    MaxGuilds(30001),

    /** Maximum number of friends reached (1000). */
    MaxFriends(30002),

    /** Maximum number of pins reached for the channel (50). */
    MaxPins(30003),

    /** Maximum number of recipients reached (10). */
    MaxRecipients(30004),

    /** Maximum number of guild roles reached (250). */
    MaxGuildRoles(30005),

    /** Maximum number of webhooks reached (15). */
    MaxWebhooks(30007),

    /** Maximum number of emojis reached. */
    MaxEmojis(30008),

    /** Maximum number of reactions reached (20). */
    MaxReactions(30010),

    /** Maximum number of group DMs reached (10). */
    MaxGroupDMs(30011),

    /** Maximum number of guild channels reached (500). */
    MaxGuildChannels(30013),

    /** Maximum number of attachments in a message reached (10). */
    MaxAttachments(30015),

    /** Maximum number of invites reached (1000). */
    MaxInvites(30016),

    /** Maximum number of animated emojis reached. */
    MaxAnimatedEmojis(30018),

    /** Maximum number of server members reached. */
    MaxServerMembers(30019),

    /** Maximum number of server categories has been reached (5). */
    MaxServerCategories(30030),

    /** Guild already has a template. */
    GuildAlreadyHadTemplate(30031),

    /** Maximum number of application commands reached. */
    MaxApplicationCommands(30032),

    /** Max number of thread participants has been reached (1000). */
    MaxThreadParticipants(30033),

    /** Max number of daily application command creates has been reached (200). */
    MaxDailyApplicationCommandCreates(30034),

    /** Maximum number of bans for non-guild members have been exceeded. */
    MaxNonMemberBans(30035),

    /** Maximum number of bans fetches has been reached. */
    MaxBanFetches(30037),

    /** Maximum number of uncompleted guild scheduled events reached (100). */
    MaxUncompletedGuildScheduledEvents(30038),

    /** Maximum number of stickers reached. */
    MaxStickers(30039),

    /** Maximum number of prune requests has been reached. Try again later. */
    MaxPruneRequests(30040),

    /** Maximum number of guild widget settings updates has been reached. Try again later. */
    MaxGuildWidgetSettingsUpdates(30042),

    /** Maximum number of edits to messages older than 1 hour reached. Try again later. */
    MaxOldMessageEdits(30046),

    /** Maximum number of pinned threads in a forum channel has been reached. */
    MaxPinnedThreadsInForumChannel(30047),

    /** Maximum number of tags in a forum channel has been reached. */
    MaxTagsInForumChannel(30048),

    /** Bitrate is too high for channel of this type. */
    BitrateTooHigh(30052),

    /** Maximum number of premium emojis reached (25). */
    MaxPremiumEmojis(30056),

    /** Maximum number of webhooks per guild reached (1000). */
    MaxGuildWebhooks(30058),

    /** Maximum number of channel permission overwrites reached (1000). */
    MaxChannelPermissionOverwrites(30060),

    /** The channels for this guild are too large. */
    ChannelsTooLarge(30061),

    /** Unauthorized. Provide a valid token and try again. */
    Unauthorized(40001),

    /** You need to verify your account in order to perform this action. */
    VerifyAccount(40002),

    /** You are opening direct messages too fast. */
    TooFastDM(40003),

    /** Send messages has been temporarily disabled. */
    SendMessagesTemporarilyDisabled(40004),

    /** Request entity too large. Try sending something smaller in size. */
    RequestEntityTooLarge(40005),

    /** This feature has been temporarily disabled server-side. */
    TemporarilyDisabled(40006),

    /** The user is banned from this guild. */
    UserBannedFromGuild(40007),

    /** Connection has been revoked. */
    ConnectionRevoked(40012),

    /** Target user is not connected to voice. */
    UserNotInVoice(40032),

    /** This message has already been crossposted. */
    AlreadyCrossposted(40033),

    /** An application command with that name already exists. */
    ApplicationCommandNameExists(40041),

    /** Application interaction failed to send. */
    InteractionFailedToSend(40043),

    /** Cannot send a message in a forum channel. */
    CannotSendMessageInForumChannel(40058),

    /** Interaction has already been acknowledged. */
    InteractionAlreadyAcknowledged(40060),

    /** Tag names must be unique. */
    TagNamesMustBeUnique(40061),

    /** Service resource is being rate limited. */
    ResourceIsRateLimited(40062),

    /** There are no tags available that can be set by non-moderators. */
    NoSettableTagsAvailable(40066),

    /** A tag is required to create a forum post in this channel. */
    TagRequired(40067),

    /** Missing access. */
    MissingAccess(50001),

    /** Invalid account type. */
    InvalidAccountType(50002),

    /** Cannot execute action on a DM channel. */
    CannotExecuteOnDM(50003),

    /** Guild widget disabled. */
    GuildWidgetDisabled(50004),

    /** Cannot edit a message authored by another user. */
    CannotEditMessageByAnotherUser(50005),

    /** Cannot send an empty message. */
    CannotSendEmptyMessage(50006),

    /** Cannot send messages to this user. */
    CannotSendMessagesToUser(50007),

    /** Cannot send messages in a non-text channel. */
    CannotSendMessagesInNonTextChannel(50008),

    /** Channel verification level is too high for you to gain access. */
    ChannelVerificationTooHigh(50009),

    /** OAuth2 application does not have a bot. */
    OAuth2HasNoBot(50010),

    /** OAuth2 application limit reached. */
    OAuth2ApplicationLimit(50011),

    /** Invalid OAuth2 state. */
    InvalidOAuth2State(50012),

    /** You lack permissions to perform that action. */
    PermissionLack(50013),

    /** Invalid authentication token provided. */
    InvalidAuthToken(50014),

    /** Note was too long. */
    TooLongNote(50015),

    /**
     * Provided too few or too many messages to delete. Must provide at least 2 and fewer than 100 messages to delete.
     */
    ProvidedMessageCountInsufficient(50016),

    /** Invalid MFA Level. */
    InvalidMFALevel(50017),

    /** A message can only be pinned to the channel it was sent in. */
    CannotPinMessageFromAnotherChannel(50019),

    /** Invite code was either invalid or taken. */
    InviteCodeInvalidOrTaken(50020),

    /** Cannot execute action on a system message. */
    CannotExecuteOnSystemMessage(50021),

    /** Cannot execute action on this channel type. */
    WrongChannelType(50024),

    /** Invalid OAuth2 access token provided. */
    InvalidOAuth2AccessToken(50025),

    /** Missing required OAuth2 scope. */
    MissingOAuthScope(50026),

    /** Invalid webhook token provided. */
    InvalidWebhookToken(50027),

    /** Invalid role. */
    InvalidRole(50028),

    /** Invalid Recipient(s). */
    InvalidRecipients(50033),

    /** A message provided was too old to bulk delete. */
    BulkDeleteOldMessage(50034),

    /**
     * Invalid form body (returned for both `application/json` and `multipart/form-data` bodies),
     * or invalid `Content-Type` provided.
     */
    InvalidFormBody(50035),

    /** An invite was accepted to a guild the application's bot is not in. */
    NonBotGuildInviteAccepted(50036),

    /** Invalid Activity Action. */
    InvalidActivityAction(50039),

    /** Invalid API version provided. */
    InvalidAPIVersion(50041),

    /** File uploaded exceeds the maximum size. */
    FileTooLarge(50045),

    /** Invalid file uploaded. */
    InvalidFile(50046),

    /** Cannot self-redeem this gift. */
    CannotSelfRedeemGift(50054),

    /** Invalid Guild. */
    InvalidGuild(50055),

    /** Invalid request origin. */
    InvalidRequestOrigin(50067),

    /** Invalid message type. */
    InvalidMessageType(50068),

    /** Payment source required to redeem gift. */
    GiftRequiresPaymentSource(50070),

    /** Cannot modify a system webhook. */
    CannotModifySystemWebhook(50073),

    /** Cannot delete a channel required for Community guilds. */
    CannotDeleteRequiredCommunityChannel(50074),

    /** Cannot edit stickers within a message. */
    CannotEditStickersWithinMessage(50080),

    /** Invalid sticker sent. */
    InvalidStickerSent(50081),

    /**
     * Tried to perform an operation on an archived thread, such as editing a message or adding a user to the thread.
     */
    OperationOnArchivedThread(50083),

    /** Invalid thread notification settings. */
    InvalidThreadNotificationSettings(50084),

    /** `before` value is earlier than the thread creation date. */
    BeforeValueBeforeThreadCreate(50085),

    /** Community server channels must be text channels. */
    CommunityServerChannelMustBeTextChannel(50086),

    /** The entity type of the event is different from the entity you are trying to start the event for. */
    EntityOfEventDifferentFromEventEntity(50091),

    /** This server is not available in your location. */
    ServerNotAvailableInLocation(50095),

    /** This server needs monetization enabled in order to perform this action. */
    ServerNeedsMonetizationEnabled(50097),

    /** This server needs more boosts to perform this action. */
    ServerNeedsMoreBoosts(50101),

    /** The request body contains invalid JSON. */
    InvalidJsonInRequestBody(50109),

    /** Owner cannot be pending member. */
    OwnerCannotBePendingMember(50131),

    /** Ownership cannot be transferred to a bot user. */
    OwnershipCannotBeTransferredToBot(50132),

    /** Failed to resize the asset below the maximum size: 262144. */
    FailedToResizeAssetBelowMaximumSize(50138),

    /** Cannot mix subscription and non subscription roles for an emoji. */
    CannotMixSubscriptionAndNonSubscriptionRoles(50144),

    /** Cannot convert between premium emoji and normal emoji. */
    CannotConvertBetweenPremiumAndNormalEmoji(50145),

    /** Uploaded file not found. */
    UnknownUpload(50146),

    /** Voice messages do not support additional content. */
    VoiceMessagesDoNotSupportAdditionalContent(50159),

    /** Voice messages must have a single audio attachment. */
    VoiceMessagesMustHaveASingleAudioAttachment(50160),

    /** Voice messages must have supporting metadata. */
    VoiceMessagesMustHaveSupportingMetadata(50161),

    /** Voice messages cannot be edited. */
    VoiceMessagesCannotBeEdited(50162),

    /** Cannot delete guild subscription integration. */
    CannotDeleteGuildSubscriptionIntegration(50163),

    /** You cannot send voice messages in this channel. */
    CannotSendVoiceMessagesInThisChannel(50173),

    /** The user account must first be verified. */
    UserAccountMustBeVerified(50178),

    /** You do not have permission to send this sticker. */
    StickerPermissionLack(50600),

    /** Two factor is required for this operation. */
    Require2FA(60003),

    /** No users with DiscordTag exist. */
    NoUsersWithDiscordTag(80004),

    /** Reaction was blocked. */
    ReactionBlocked(90001),

    /** Application not yet available. Try again later. */
    ApplicationNotAvailable(110001),

    /** API resource is currently overloaded. Try again a little later. */
    APIResourceOverloaded(130000),

    /** The Stage is already open. */
    StageAlreadyOpen(150006),

    /** Cannot reply without permission to read message history. */
    CannotReplyWithoutMessageHistoryPermission(160002),

    /** A thread has already been created for this message. */
    MessageAlreadyHasThread(160004),

    /** Thread is locked. */
    TheadLocked(160005),

    /** Maximum number of active threads reached. */
    MaxActiveThreads(160006),

    /** Maximum number of active announcement threads reached. */
    MaxActiveAnnouncementThreads(160007),

    /** Invalid JSON for uploaded Lottie file. */
    InvalidJsonForLottieFile(170001),

    /** Uploaded Lotties cannot contain rasterized images such as PNG or JPEG. */
    RasterizedImagesInLotties(170002),

    /** Sticker maximum framerate exceeded. */
    MaxStickerFrameRate(170003),

    /** Sticker frame count exceeds maximum of 1000 frames. */
    MaxStickerFrameCount(170004),

    /** Lottie animation maximum dimensions exceeded. */
    MaxLottieAnimationDimensions(170005),

    /** Sticker frame rate is either too small or too large. */
    InvalidStickerFrameRate(170006),

    /** Sticker animation duration exceeds maximum of 5 seconds. */
    MaxStickerAnimationDuration(170007),

    /** Cannot update a finished event. */
    CannotUpdateFinishedEvent(180000),

    /** Failed to create stage needed for stage event. */
    FailedToCreateStage(180002),

    /** Message was blocked by automatic moderation. */
    MessageBlockedByAutomaticModeration(200000),

    /** Title was blocked by automatic moderation. */
    TitleBlockedByAutomaticModeration(200001),

    /** Webhooks posted to forum channels must have a thread_name or thread_id. */
    WebhookMissingThreadNameOrThreadId(220001),

    /** Webhooks posted to forum channels cannot have both a thread_name and thread_id. */
    WebhookCannotHaveThreadNameAndThreadId(220002),

    /** Webhooks can only create threads in forum channels. */
    WebhooksCanOnlyCreateThreadsInForumChannels(220003),

    /** Webhook services cannot be used in forum channels. */
    WebhookServicesCannotBeUsedInForumChannels(220004),

    /** Message blocked by harmful links filter. */
    MessageBlockedByHarmfulLinksFilter(240000),

    ;

    internal object Serializer : KSerializer<JsonErrorCode> {
        override val descriptor = PrimitiveSerialDescriptor("JsonErrorCodeSerializer", PrimitiveKind.INT)

        private val entriesByCode = entries.associateBy { it.code }
        override fun deserialize(decoder: Decoder): JsonErrorCode {
            val code = decoder.decodeInt()
            return entriesByCode[code] ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: JsonErrorCode) {
            encoder.encodeInt(value.code)
        }
    }
}
