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

    /** Unknown connection. */
    UnknownConnection(10017),

    /** Unknown session. */
    UnknownSession(10020),

    /** Unknown asset. */
    UnknownAsset(10021),

    /** Unknown approval form. */
    UnknownApprovalForm(10023),

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

    /** Unknown price tier. */
    UnknownPriceTier(10035),

    /** Unknown redistributable. */
    UnknownRedistributable(10036),

    /** Unknown gift code. */
    UnknownGiftCode(10038),

    /** Unknown team. */
    UnknownTeam(10039),

    /** Unknown team member. */
    UnknownTeamMember(10040),

    /** Unknown company. */
    UnknownCompany(10041),

    /** Unknown manifest labels. */
    UnknownManifestLabels(10042),

    /** Unknown achievement. */
    UnknownAchievement(10043),

    /** Unknown EULA. */
    UnknownEula(10044),

    /** Unknown application news. */
    UnknownApplicationNews(10045),

    /** This channel is not associated with a SKU. */
    UnknownChannelSku(10046),

    /** Unknown Premium Guild Subscription. */
    UnknownPremiumGuildSubscription(10047),

    /** Unknown Gift code batch. */
    UnknownGiftCodeBatch(10048),

    /** Unknown stream. */
    UnknownStream(10049),

    /** Unknown premium server subscribe cooldown. */
    UnknownPremiumServerSubscribeCooldown(10050),

    /** Unknown payout. */
    UnknownPayout(10053),

    /** Unknown premium guild subscription slot. */
    UnknownPremiumGuildSubscriptionSlot(10055),

    /** Unknown remote authentication session. */
    UnknownRemoteAuthenticationSession(10056),

    /** Unknown guild template. */
    UnknownGuildTemplate(10057),

    /** Unknown User Identity Verification. */
    UnknownUserIdentityVerification(10058),

    /** Unknown discoverable server category. */
    UnknownDiscoverableServerCategory(10059),

    /** Unknown sticker. */
    UnknownSticker(10060),

    /** Unknown sticker pack. */
    UnknownStickerPack(10061),

    /** Unknown interaction. */
    UnknownInteraction(10062),

    /** Unknown application command. */
    UnknownApplicationCommand(10063),

    /** Unknown user trial offer. */
    UnknownUserTrialOffer(10064),

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

    /** Unknown Subscription Plan. */
    UnknownSubscriptionPlan(10073),

    /** Unknown directory entry. */
    UnknownDirectoryEntry(10075),

    /** Unknown promotion. */
    UnknownPromotion(10076),

    /** Endpoint not available. */
    EndpointNotAvailable(10077),

    /** Unknown survey. */
    UnknownSurvey(10082),

    /** Unknown server role subscription settings. */
    UnknownServerRoleSubscriptionSettings(10083),

    /** Premium usage not available. */
    PremiumUsageNotAvailable(10084),

    /** Unknown creator monetization enable request. */
    UnknownCreatorMonetizationEnableRequest(10085),

    /** Unknown Tag. */
    UnknownTag(10087),

    /** Unknown featured item. */
    UnknownFeaturedItem(10091),

    /** Unknown WebAuthn Authenticatior. */
    UnknownWebAuthnAuthenticator(10093),

    /** Unknown User code. */
    UnknownUserCode(10094),

    /** Unknown channel for new member action. */
    UnknownChannelForNewMemberAction(10095),

    /** Unknown message attachment. */
    UnknownMessageAttachment(10096),

    /** Unknown sound. */
    UnknownSound(10097),

    /** Unknown game invite. */
    UnknownGameInvite(10099),

    /** Unknown user discount. */
    UnknownUserDiscount(10100),

    /** Unknown poll. */
    UnknownPoll(10102),

    /** Unknown snapshot. */
    UnknownSnapshot(10105),

    /** Unknown tenure reward status. */
    UnknownTenureRewardStatus(10111),

    /** Unknown google play package name. */
    UnknownGooglePlayPackageName(10112),

    /** Guild is not a Clan. */
    GuildIsNotClan(10113),

    /** Unknown avatar. */
    UnknownAvatar(10118),

    /** Unknown social SDK release version. */
    UnknownSocialSDKReleaseVersion(10120),

    /** Bots cannot use this endpoint. */
    NonBotEndpoint(20001),

    /** Only bots can use this endpoint. */
    BotsEndpoint(20002),

    /** RPC Proxy Disallowed. */
    RPCProxyDisallowed(20003),

    /** Explicit content cannot be sent to the desired recipient(s). */
    CannotSendExplicitContent(20009),

    /** This account is scheduled for deletion. */
    AccountScheduledForDeletion(20011),

    /** You are not authorized to perform this action on this application. */
    UnauthorizedForAction(20012),

    /** This account is disabled. */
    AccountDisabled(20013),

    /** This action requires a premium subscription. */
    RequiresPremiumSubscription(20015),

    /** This action cannot be performed due to slow-mode rate limit. */
    SlowModeRateLimit(20016),

    /** The Maze isn't meant for you. */
    NotYourMaze(20017),

    /** Only the owner of this account can perform this action. */
    OnlyOwner(20018),

    /** You must accept the invitation to this team before you can perform this action. */
    AcceptTeamInviteFirst(20019),

    /** The SKU is not available for purchase. */
    SKUNotForSale(20020),

    /** Friend list sync requires user to have verified phone. */
    SyncRequiresVerifiedPhone(20021),

    /** This message cannot be edited due to announcement rate limits. */
    AnnouncementRateLimit(20022),

    /** The specified local account provider is not recognized. */
    AccountProviderUnrecognized(20023),

    /** Under minimum age. */
    UnderMinimumAge(20024),

    /** Cannot perform action because application is verified. Please contact support. */
    ApplicationVerified(20025),

    /** This bot has been flagged by our anti-spam system for abusive behavior pending review, and is currently unable to be added to any additional servers. */
    FlaggedByAntiSpam(20026),

    /** This action can only be performed for approved store applications. */
    ApprovedStoreApplicationsOnly(20027),

    /** The channel you are writing has hit the write rate limit. */
    ChannelWriteRateLimit(20028),

    /** The write action you are performing on the server has hit the write rate limit. */
    ServerWriteRateLimit(20029),

    /** Your Stage topic, server name, server description, or channel names contain words that are not allowed. */
    DisallowedName(20031),

    /** The write action you are performing on the channel has hit the write rate limit for messages greater than 2000 characters. */
    LargeMessageRateLimit(20032),

    /** This update is not allowed for this connection type. */
    UpdateNotAllowedForConnectionType(20033),

    /** Guild premium subscription level too low. */
    GuildSubscriptionTooLow(20035),

    /** You must be friends with this user to perform this action. */
    NotFriends(20037),

    /** This action cannot be performed because the team owns a verified application. Please contact support. */
    OwnsVerifiedApplication(20039),

    /** This action cannot be performed because a vanity url is requiered for published guilds. */
    VanityUrlRequired(20040),

    /** Invalid remote authentication ticket. */
    InvalidRemoteAuthTicket(20042),

    /** ⚠️ VANITY_URL_EMPLOYEE_ONLY_GUILD_DISABLED. */
    VanityUrlEmployeeOnlyGuildDisabled(20044),

    /** This guild does not meet requirements of using the vanity url feature. */
    VanityUrlRequirementsNotMet(20045),

    /** New requirements can only be added when there are no members in the role. */
    RoleNotEmpty(20054),

    /** Application is restricted from joining server. See https://i.dis.gd/500 for more info. */
    ApplicationJoinRestricted(20055),

    /** This action cannot be performed because the team has submitted identity verification. Please contact support. */
    SubmittedIdentityVerification(20058),

    /** This action requires an application to be authorized by the user. */
    ApplicationMustBeAuthorized(20062),

    /** Maximum number of guilds reached (100). */
    MaxGuilds(30001),

    /** Maximum number of friends reached (1000). */
    MaxFriends(30002),

    /** Maximum number of pins reached for the channel (250). */
    MaxPins(30003),

    /** Maximum number of recipients reached (10). */
    MaxRecipients(30004),

    /** Maximum number of guild roles reached (250). */
    MaxGuildRoles(30005),

    /** Too many users have this username, please try another. */
    TooManyUsernames(30006),

    /** Maximum number of webhooks reached (10). */
    MaxWebhooks(30007),

    /** Maximum number of emojis reached. */
    MaxEmojis(30008),

    /** Maximum number of connections reached (50). */
    MaxConnections(30009),

    /** Maximum number of reactions reached (20). */
    MaxReactions(30010),

    /** Maximum number of group DMs reached (10). */
    MaxGroupDMs(30011),

    /** Maximum number of whitelisted users reached (50). */
    MaxWhitelistedUsers(30012),

    /** Maximum number of guild channels reached (500). */
    MaxGuildChannels(30013),

    /** Maximum number of attachments in a message reached (10). */
    MaxAttachments(30015),

    /** Maximum number of invites reached (1000). */
    MaxInvites(30016),

    /** Maximum number of application assets reached (300). */
    MaxApplicationAssets(30017),

    /** Maximum number of animated emojis reached. */
    MaxAnimatedEmojis(30018),

    /** Maximum number of server members reached. */
    MaxServerMembers(30019),

    /** Maximum number of game SKUs per application reached (1). */
    MaxGameSKUs(30021),

    /** Maximum number of gifts has been reached for SKU. */
    MaxGiftsForSku(30022),

    /** Maximum number of teams reached (30). */
    MaxTeams(30023),

    /** Maximum number of companies reached (20).*/
    MaxCompanies(30025),

    /** Maximum number of premium subscriptions reached. */
    MaxPremiumSubscriptions(30026),

    /** Maximum number of user notes reached (1500). */
    MaxUserNotes(30027),

    /** ⚠️ NOT_ENOUGH_GUILD_MEMBERS. */
    NotEnoughMembers(30029),

    /** Maximum number of server categories has been reached (5). */
    MaxServerCategories(30030),

    /** Guild already has a template. */
    GuildAlreadyHadTemplate(30031),

    /** Maximum number of application commands reached. */
    MaxApplicationCommands(30032),

    /** Maximum number of thread participants has been reached (1000). */
    MaxThreadParticipants(30033),

    /** Maximum number of daily application command creates has been reached (200). */
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

    /** Maximum number of entries in directory channel reached (500). */
    MaxDirectoryChannelEntries(30041),

    /** Maximum number of guild widget settings updates has been reached. Try again later. */
    MaxGuildWidgetSettingsUpdates(30042),

    /** Maximum number of published tiers reached (3). */
    MaxPublishedTiers(30043),

    /** Maximum number of soundboard sounds reached (36). */
    MaxSoundboardSounds(30045),

    /** Maximum number of edits to messages older than 1 hour reached. Try again later. */
    MaxOldMessageEdits(30046),

    /** Maximum number of pinned threads in a forum channel has been reached. */
    MaxPinnedThreadsInForumChannel(30047),

    /** Maximum number of tags in a forum channel has been reached. */
    MaxTagsInForumChannel(30048),

    /** Bitrate is too high for channel of this type. */
    BitrateTooHigh(30052),

    /** Maximum total size of attachments reached (500MB). */
    MaxTotalAttachmentSize(30053),

    /** Maximum number of published application subscriptions reached (1). */
    MaxPublishedApplicationSubscriptions(30054),

    /** Maximum number of subscription group listings per type per application reached (1). */
    MaxSubscriptionGroupListings(30055),

    /** Maximum number of premium emojis reached (25). */
    MaxPremiumEmojis(30056),

    /** Maximum number of webhooks per guild reached (1000). */
    MaxGuildWebhooks(30058),

    /** Maximum number of blocked users reached (5000) */
    MaxBlockedUsers(30059),

    /** Maximum number of channel permission overwrites reached (1000). */
    MaxChannelPermissionOverwrites(30060),

    /** The channels for this guild are too large. */
    ChannelsTooLarge(30061),

    /** Maximum number of application subscription SKUs reached (20). */
    MaxApplicationSkus(30063),

    /** Maximum number of one-time-purchase SKUs reached (50). */
    MaxOneTimePurchaseSkus(30064),

    /** Maximum number of published products reached (10). */
    MaxPublishedProducts(30065),

    /** Maximum number of snapshot reached (5). */
    MaxSnapshots(30066),

    /** Reached max storage capacity of snapshots. */
    MaxSnapshotStorageCapacity(30067),

    /** Maximum number of guild integrations reached (50). */
    MaxGuildIntegrations(30068),

    /** Maximum message forwards reached. */
    MaxMessageForwards(30070),

    /** Maximum number of store listings reached for SKU (1). */
    MaxStoreListingsForSKU(30072),

    /** Maximum number of saved messages reached. */
    MaxSavedMessages(30074),

    /**	Cannot publish a server subscription while a user subscription is already published. */
    UserSubscriptionAlreadyPublished(30076),

    /** You are being rate limited. */
    RateLimited(31001),

    /**	The resource is being rate limited. */
    ResourceRateLimited(31002),

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

    /** That Discord Tag is already taken. */
    TagAlreadyTaken(40008),

    /** You already have a verified phone. */
    PhoneAlreadyVerified(40010),

    /** You must transfer ownership of any owned guilds before deleting your account. */
    MustTransferGuildsBeforeDeletion(40011),

    /** Connection has been revoked. */
    ConnectionRevoked(40012),

    /** This account must be claimed. */
    AccountMustBeClaimed(40013),

    /** Activity expired. */
    ActivityExpireD(40014),

    /** You must transfer ownership of any owned guilds before disabling your account. */
    MustTransferGuildsBeforeDisabling(40015),

    /** Service is currently unavailable. */
    ServiceUnavailable(40016),

    /** Only consumable SKUs can be consumed. */
    OnlyConsumableSkusCanBeConsumed(40018),

    /** You can only delete sandbox entitlements. */
    CanOnlyDeleteSandboxEntitlements(40019),

    /** You can't update your privacy settings at this time. */
    CannotUpdatePrivacySettings(40021),

    /** Team member already exists. */
    MemberAlreadyExists(40024),

    /** Team owner cannot be deleted. */
    TeamOwnerCannotBeDeleted(40025),

    /** All team members must have a verified email address. */
    MembersMustHaveVerifiedEmail(40026),

    /** You are already a member of this team. */
    AlreadyMemberOfTeam(40027),

    /** You must transfer ownership of any owned teams before deleting your account. */
    MustTransferTeamsBeforeDeleting(40028),

    /** A company with that name already exists. */
    CompanyNameTaken(40029),

    /** Target user is not connected to voice. */
    UserNotInVoice(40032),

    /** This message has already been crossposted. */
    AlreadyCrossposted(40033),

    /** User has disabled contact sync. */
    ContactSyncDisabled(40034),

    /** User identity verification processing. */
    IdentityVerificationProcessing(40035),

    /** User identity verification already succeeded. */
    IdentityVerificationSucceeded(40036),

    /** Application verification ineligible. */
    ApplicationVerificationIneligible(40037),

    /** Application verification already submitted. */
    ApplicationVerificationSubmitted(40038),

    /** Application verification already approved. */
    ApplicationVerificationApproved(40039),

    /** An application command with that name already exists. */
    ApplicationCommandNameExists(40041),

    /** Application interaction failed to send. */
    InteractionFailedToSend(40043),

    /** Entry already exists in this directory channel. */
    DirectoryChannelEntryExists(40044),

    /** There is no outbound promotion code to claim. */
    NoOutboundPromotionCode(40046),

    /** Application must be verified to request intents. */
    VerificationRequiredForIntentsRequest(40053),

    /** Recommendations not available. */
    RecommendationsUnavailable(40054),

    /** Cannot send a message in a forum channel. */
    CannotSendMessageInForumChannel(40058),

    /** Interaction has already been acknowledged. */
    InteractionAlreadyAcknowledged(40060),

    /** Tag names must be unique. */
    TagNamesMustBeUnique(40061),

    /** Service resource is being rate limited. */
    ResourceIsRateLimited(40062),

    /** The application needs to be verified. */
    ApplicationMustBeVerified(40064),

    /** There are no tags available that can be set by non-moderators. */
    NoSettableTagsAvailable(40066),

    /** A tag is required to create a forum post in this channel. */
    TagRequired(40067),

    /** ⚠️ USER_QUARANTINE. */
    UserQuarantined(40068),

    /** Invites are currently paused for this server. Please try again later. */
    InvitesPauseD(40069),

    /** Users with trial subscriptions are not eligible for claiming this promotion code. */
    UsersWithTrialCannotClaimCode(40071),

    /** We could not verify your phone number. Please try again with the number listed on your account. */
    CannotVerifyPhoneNumber(40073),

    /** An entitlement has already been granted for this resource. */
    EntitlementAlreadyGranted(40074),

    /** New member action already completed. */
    NewMemberActionCompleted(40075),

    /** Find Friends is temporarily unavailable. */
    FindFriendsUnavailable(40077),

    /** Invalid recipient for friend invite. */
    InvalidFriendInviteRecipient(40081),

    /** Invalid code for friend invite. */
    InvalidFriendInviteCode(40082),

    /** Cannot kick the Clyde AI Bot, he can only be banned. */
    CannotKickClyde(40088),

    /** Verified applications cannot be embedded. */
    VerifiedApplicationCannotEmbed(40090),

    /** Server is ineligible to become a Guild. */
    ServerCannotBecomeGuild(40092),

    /** This interaction has hit the maximum number of follow up messages. */
    MaxFollowupMessages(40094),

    /** Applications with the Social Layer Integration flag enabled cannot be deleted. */
    CannotDeleteAppsWithSocialLayerIntegration(40101),

    /** Application has reached user threshold, will require manual review. */
    ApplicationUserThresholdReached(40102),

    /** User is already a member of the guild. */
    AlreadyGuildMember(40108),

    /** User must be a member of the lobby. */
    MustBeALobbyMember(40109),

    /** User is not allowed to accept this invite. */
    CannotAcceptInvite(40110),

    /** 	Cloudflare access denied (code: 1020). */
    CloudflareAccessDenied(40333),

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

    /** Password does not match. */
    PasswordDoesNotMatch(50018),

    /** A message can only be pinned to the channel it was sent in. */
    CannotPinMessageFromAnotherChannel(50019),

    /** Invite code was either invalid or taken. */
    InviteCodeInvalidOrTaken(50020),

    /** Cannot execute action on a system message. */
    CannotExecuteOnSystemMessage(50021),

    /** Invalid Phone number. */
    InvalidPhoneNumber(50022),

    /** Invalid Client ID. */
    InvalidClientId(50023),

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

    /** Users without verified emails cannot be added to application whitelist. */
    UnverifiedUserCannotJoinWhitelist(50029),

    /** You cannot invite yourself to application whitelist. */
    CannotInviteSelfToWhitelist(50030),

    /** You cannot invite a user who was already whitelisted, or is already a member. */
    CannotInviteWhitelistedUser(50031),

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

    /** Verification Code is invalid. */
    InvalidVerificationCode(50037),

    /** You cannot perform this action on yourself. */
    CannotPerformActionOnSelf(50038),

    /** Invalid Activity Action. */
    InvalidActivityAction(50039),

    /** Invalid OAuth2 redirect_uri. */
    InvalidOAuth2Uri(50040),

    /** Invalid API version provided. */
    InvalidAPIVersion(50041),

    /** Invalid Billing State. */
    InvalidBillingState(50042),

    /** Data Harvest is pending, another Data Harvest cannot be started. */
    DataHarvestAlreadyPending(50043),

    /** Data Harvest can only be requested every 30 days. */
    DataHarvestCanOnlyBeRequestedEvery30Days(50044),

    /** File uploaded exceeds the maximum size. */
    FileTooLarge(50045),

    /** Invalid Asset. */
    InvalidAsset(50046),

    /** 50048	Invalid Payment Source. */
    InvalidPaymentSource(50048),

    /** 50049	Invalid IP Address. Only IPv4 Addresses are supported. */
    InvalidIPAddress(50049),

    /** 50050	This gift has been redeemed already. */
    GiftRedeemedAlready(50050),

    /** 50051	You already own this SKU. */
    AlreadyOwnSKU(50051),

    /** Cannot self-redeem this gift. */
    CannotSelfRedeemGift(50054),

    /** Invalid Guild. */
    InvalidGuild(50055),

    /** Cannot delete asset that is currently in use on a store page. */
    CannotDeleteStorePageAsset(50056),

    /** Invalid SKU. */
    InvalidSku(50057),

    /** Cannot apply licence to an application that is already activated. */
    CannotApplyLicenceToActivatedApplication(50058),

    /** Application cannot be submitted for approval. Please refer to the developer checklist. */
    ApplicationCannotBeSubmitted(50059),

    /** You do not have permission to send this sticker. */
    NoPermissionToSendSticker(50060),

    /** A branch needs to be associated with the channel to use this endpoint. */
    BranchMustBeAssociatedWithChannel(50061),

    /** Cannot unsubscribe from a server again. */
    CannotUnsubscribeFromServerAgain(50064),

    /** Invalid request origin. */
    InvalidRequestOrigin(50067),

    /** Invalid message type. */
    InvalidMessageType(50068),

    /** Must wait for premium server subscription cooldown to expire. */
    MustWaitForPremiumCooldown(50069),

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

    /** Event cannot be started for this channel. */
    EventCannotStartForChannel(50089),

    /** The entity type of the event is different from the entity you are trying to start the event for. */
    EntityOfEventDifferentFromEventEntity(50091),

    /** You have already owned this SKU. */
    AlreadyOwnedSKU(50092),

    /** Invalid directory entry type. */
    InvalidDirectoryEntryType(50094),

    /** This server is not available in your location. */
    ServerNotAvailableInLocation(50095),

    /** Cannot accept your own friend invite. */
    CannotAcceptOwnFriendInvite(50096),

    /** This server needs monetization enabled in order to perform this action. */
    ServerNeedsMonetizationEnabled(50097),

    /** This entity can only be edited through the server settings UI. */
    EntityCanOnlyBeEditedInUI(50099),

    /** This server needs more boosts to perform this action. */
    ServerNeedsMoreBoosts(50101),

    /** Cannot update price tier on a published role listing. */
    CannotUpdatePublishedRolePriceTier(50102),

    /** Malformed user settings payload. */
    MalformedUserSettings(50104),

    /** User Settings failed validation: {reason}. */
    InvalidUserSettings(50105),

    /** You do not have access to the requested activity. */
    NoAccessToActivity(50106),

    /** This server needs more boosts to launch this activity. */
    ServerNeedsMoreBoostsForActivity(50107),

    /** ⚠️ INVALID_ACTIVITY_LAUNCH_CONCURRENT_ACTIVITIES. */
    CannotLaunchConcurrentActivities(50108),

    /** The request body contains invalid JSON. */
    InvalidJsonInRequestBody(50109),

    /** 50110	The provided file is invalid. */
    InvalidFile(50110),
    /** 50111	Invalid guild join request application status query. */
    InvalidGuildJoinRequest(50111),
    /** 50112	Cannot delete a subscription group that has listings. */
    CannotDeleteSubscriptionGroup(50112),
    /** 50113	Cannot create more than 1 subscription group. */
    CannotCreateMoreThanOneSubscriptionGroup(50113),
    /** 50119	This guild is not allowed to use monetization features. */
    GuildCannotUseMonetization(50119),
    /** 50121	This server is not eligible to request monetization. */
    ServerNotEligibleToRequestMonetization(50121),
    /** 50123	The provided file type is invalid. */
    InvalidFileType(50123),
    /** 50124	The provided file duration exceeds maximum of 5.2 seconds. */
    FileDurationTooLong(50124),
    /** 50129	You must have Nitro to launch this activity. */
    MustHaveNitroForActivity(50129),

    /** Owner cannot be pending member. */
    OwnerCannotBePendingMember(50131),

    /** Ownership cannot be transferred to a bot user. */
    OwnershipCannotBeTransferredToBot(50132),

    /** User is already owner. */
    AlreadyOwner(50133),

    /** User cannot access private channel. */
    CannotAccessPrivateChannel(50136),

    /** Failed to resize the asset below the maximum size: 262144. */
    FailedToResizeAssetBelowMaximumSize(50138),

    /** User could not be found. */
    UserNotFound(50139),

    /** User is not staff. */
    UserNotStaff(50140),

    /** Cannot mix subscription and non subscription roles for an emoji. */
    CannotMixSubscriptionAndNonSubscriptionRoles(50144),

    /** Cannot convert between premium emoji and normal emoji. */
    CannotConvertBetweenPremiumAndNormalEmoji(50145),

    /** Uploaded file not found. */
    UnknownUpload(50146),

    /** Invalid connection. */
    InvalidConnection(50147),

    /** Cannot launch Activity in AFK Channel. */
    CannotLaunchInAFKChannel(50148),

    /** Invalid Role Configuration. */
    InvalidRoleConfiguration(50150),

    /** The specified emoji is invalid. */
    InvalidEmojiSpecified(50151),

    /** This server is not eligible for store pages. */
    ServerIneligibleForStorePages(50152),

    /** This server is not allowed to create an enable request. */
    ServerNotAllowedToCreateRequest(50155),

    /** Cannot delete a subscription listing with subscriptions. */
    CannotDeleteSubscribedSubscription(50157),

    /** Onboarding responses are not valid. */
    InvalidOnboardingResponses(50158),

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

    /** New owner is ineligible for server subscription requirement. */
    NewOwnerIneligibleForSubscription(50164),

    /** Cannot launch Age-Gated Activity. */
    CannotLaunchAgeGatedActivity(50165),

    /** Only discoverable servers can publish a landing page. */
    OnlyDiscoverableServersCanPublishLanding(50166),

    /** Cannot send soundboard sound when user is server muted, deafened, or suppressed. */
    CannotSendSoundWhenUserIsSilenced(50167),

    /** User must be in voice channel to send voice channel effect. */
    MustBeInVoiceChannelToSendEffect(50168),

    /** Cannot update other guild fields when disabling invites. */
    CannotUpdateGuildFieldsWhenDisablingInvites(50169),

    /** Cannot specify channel_id and guild_id. */
    CannotSpecifyChannelAndGuildId(50170),

    /** This action cannot be performed because at least one tier exists. */
    ActionCannotBePerformedBecauseTierExists(50171),

    /** You cannot send voice messages in this channel. */
    CannotSendVoiceMessagesInThisChannel(50173),

    /** Not a valid clip. */
    InvalidClip(50174),

    /** The user account must first be verified. */
    UserAccountMustBeVerified(50178),

    /** Only Media channel posts can have preview. */
    OnlyMediaChannelPostCanHavePreview(50179),

    /** Cannot set hide media download option flag for non-media channels. */
    CannotHideMediaDownloadOptionForNonMediaChannels(50182),

    /** Only Community Servers can create permanent invite links. */
    OnlyCommunityServersCanCreatePermanentInvites(50183),

    /** User is not eligible for sending remix. */
    UserNotEligibleToSendRemix(50184),

    /** Archive files are not supported: {filename}. */
    ArchiveFilesUnsupported(50186),

    /** Unable to validate domain.. */
    UnableToValidateDomain(50187),

    /** Invalid guild feature. */
    InvalidGuildFeature(50188),

    /** Application is unable to monetize. */
    ApplicationCannotMonetize(50191),

    /** The provided file does not have a valid duration.. */
    InvalidFileDuration(50192),

    /** This promotion is invalid.. */
    InvalidPromotion(50193),

    /** This gift code belongs to someone else.. */
    GiftCodeBelongsToSomeoneElse(50194),

    /** Guilds cannot be specified for user installation. */
    GuildsCannotBeSpecifiedForUserInstall(50196),

    /** Invalid scopes provided for user installation. */
    InvalidScopesForUserInstallation(50197),

    /** Installation type not supported for this application. */
    UnsupportedInstallationType(50199),

    /** In-app appeals are not supported for this violation type.. */
    AppealsUnsupportedForViolationType(50205),

    /** Maximum number of user-installed applications reached (200). */
    MaxUserInstalledAppsReached(50206),

    /** You cannot launch this activity in a server with more than 25 members. */
    CannotLaunchActivityWithMoreThan25Members(50209),

    /** Activity location is invalid. */
    InvalidActivityLocation(50220),

    /** Activity ID is invalid. */
    InvalidActivityID(50225),

    /** Cannot adopt tag for this guild. */
    CannotAdoptTagForGuild(50228),

    /** Invalid user type. */
    InvalidUserType(50229),

    /** Activities cannot be launched on user’s current client platform. */
    ActivitiesCannotBeLaunchedOnPlatform(50230),

    /** Activity failed to launch due to internal error. */
    ActivityFailedInternalError(50232),

    /** Invalid channel for lobby channel linking. */
    InvalidLobbyChannelLinked(50235),

    /** Channel is already linked to another lobby. */
    LobbyChannelLinked(50237),

    /** Rules and public updates channels are only available for community guilds. */
    RulesChannelsOnlyAvailableInCommunityGuilds(50239),

    /** You cannot remove this app's Entry Point command in a bulk update operation. Please include the Entry Point command in your update request or delete it separately.. */
    CannotRemoveAppsEntryPoint(50240),

    /** User does not have the necessary lobby permissions to perform this action. */
    UserLobbyPermissionsLack(50241),

    /** Cannot set reminder in the past. */
    CannotSetReminderInThePast(50242),

    /** Due date must be in less than 5 years. */
    DueDateMustBeLessThan5Years(50243),

    /** Recipient does not allow messages from game friends in Discord. */
    DoesNotAllowMessagesFromGameFriends(50251),

    /** Provisional accounts cannot recieve messages while offline. */
    ProvisionalAccountsCannotReceiveOfflineMessages(50252),

    /** Ephemeral messages cannot be sent to offline users. */
    EphemeralMessagesCannotBeSentToOfflineUsers(50253),

    /** Invalid voice filter module version. */
    InvalidVoiceFilterVersion(50255),

    /** Application is not a child application. */
    ApplicationIsNotChildApplication(50260),

    /** Role is too large to be displayed separately from online members. */
    RoleToLargeToDisplaySeperately(50262),

    /** Invalid Tenor media format. */
    InvalidTenorFormat(50266),

    /** Lobby is not linked to a channel. */
    LobbyNotLinked(50267),

    /** You do not have permission to send this sticker. */
    StickerPermissionLack(50600),

    /** This account is already enrolled in two-factor authentication. */
    AlreadyEnrolledIn2FA(60001),

    /** This account is not enrolled in two-factor authentication. */
    NotEnrolledIn2FA(60002),

    /** Two factor is required for this operation. */
    Require2FA(60003),

    /** Must be a verified account. */
    MustBeVerified(60004),

    /** Invalid two-factor secret. */
    Invalid2FASecret(60005),

    /** Invalid two-factor auth ticket. */
    Invalid2FATicket(60006),

    /** Invalid two-factor code / Security key authentication failed. */
    Invalid2FACode(60008),

    /** Invalid two-factor session. */
    Invalid2FASession(60009),

    /** This account is not enrolled in SMS authentication. */
    NotEnrolledInSMSAuth(60010),

    /** Invalid key. */
    InvalidKey(60011),

    /** SMS Two-factor authentication cannot be enabled on this account. */
    SMS2FACannotBeEnabled(60012),

    /** Multi-factor authentication is required for administrators of servers with published Server Shop listings. */
    MFARequiredFrAdmins(60015),

    /** User is not eligible for MFA Email Verification. */
    UserIneligibleForMFA(60019),

    /** Credential not discoverable or invalid. */
    CredentialNotDiscoverable(60021),

    /** Unable to send sms message. */
    UnableToSendSMS(70003),

    /** This phone number was recently used on a different account. */
    PhoneNumberUsedRecently(70004),

    /** Please use a valid mobile phone number, not a VoIP or landline number. */
    MobilePhoneNumberInvalid(70005),

    /** You need to verify your phone number in order to perform this action. */
    PhoneNumberVerificationRequired(70007),

    /** An existing Discord account is already using this number. Please remove it before it can be used with a new account. */
    ExistingAccountUsesNumber(70008),

    /** Your Discord password reset link was sent to your email. Please check your email to reset your password. */
    PasswordResetLinkSent(70009),

    /** This phone number is unable to be associated with this account. To troubleshoot please go here: https://dis.gd/phone-errors. */
    PhoneNumberCannotBeAssociated(70011),

    /** Incoming friend requests disabled. */
    FriendRequestsDisabled(80000),

    /** Friend request blocked. */
    FriendRequestBlock(80001),

    /** Bots cannot have friends. */
    BotsCannotHaveFriends(80002),

    /** Cannot send friend request to self.*/
    CannotSendFriendRequestToSelf(80003),

    /** No users with DiscordTag exist. */
    NoUsersWithDiscordTag(80004),

    /** You do not have an incoming friend request from that user. */
    NoIncomingFriendRequest(80005),

    /** You need to be friends in order to make this change. */
    YouNeedToBeFriendsToChange(80006),

    /** You are already friends with that user. */
    AlreadyFriends(80007),

    /** You must include a discriminator. */
    MustIncludeDiscriminator(80008),

    /** Users have no common application authorization. */
    NoCommonApplicationAuth(80009),

    /** A user is no longer authorized with this application. */
    UserIsNoLongerAuthorized(80011),

    /** You must confirm the friend request to add this user. */
    MustConfirmFriendRequest(80013),

    /** Reaction was blocked. */
    ReactionBlocked(90001),

    /** User cannot use burst reactions. */
    CannotUseBurstReactions(90002),

    /** No available burst currency for user. */
    BurstCurrencyUnavailable(90003),

    /** Invalid reaction type. */
    InvalidReactionType(90004),

    /** Invalid update for Message Request. */
    InvalidMessageRequestUpdate(90100),

    /** ⚠️ UNKNOWN_BILLING_PROFILE. */
    UnknownBillingProfile(100001),

    /** Invalid payment source. */
    InvalidPaymentSource2(100002),

    /** Invalid subscription. */
    InvalidSubscription(100003),

    /** Already subscribed. */
    AlreadySubscribed(100004),

    /** Invalid plan. */
    InvalidPlan(100005),

    /** Payment source required. */
    PaymentSourceRequired(100006),

    /** Already canceled. */
    AlreadyCancelled(100007),

    /** Invalid payment. */
    InvalidPayment(100008),

    /** Already refunded. */
    AlreadyRefunded(100009),

    /** Billing address is invalid. */
    InvalidBillingAddress(100010),

    /** Already purchased. */
    AlreadyPurchased(100011),

    /** You already have a purchase in progress for this item. */
    PurchaseInProgress(100012),

    /** Valid dependent SKU entitlement is required. */
    ValidEntitlementRequired(100015),

    /** This purchase request is invalid. */
    PurchaseRequestInvalid(100017),

    /** A payment is required. */
    PaymentRequired(100018),

    /** User is ineligible for trial. */
    UserIneligibleForTrial(100019),

    /** Invalid Apple Receipt. */
    InvalidAppleReceipt(100021),

    /** ⚠️ GIFTING_CANT_REDEEM_SUBSCRIPTION_MANAGED. */
    CannotGiftManagedSubscription(100022),

    /** ⚠️ INVALID_GIFT_REDEMPTION_SUBSCRIPTION_INCOMPATIBLE. */
    InvalidGiftRedemptionSubscription(100023),

    /** ⚠️ INVALID_GIFT_REDEMPTION_INVOICE_OPEN. */
    InvalidGiftRedemptionWithInvoiceOpen(100024),

    /** ⚠️ GIFTING_CANT_REDEEM_INVOICE_OPEN. */
    CannotRedeemGiftWithInvoiceOpen(100025),

    /** The purchase amount is below the minimum charge amount. */
    PurchaseAmountBelowMinimumCharge(100026),

    /** Invoice amount cannot be negative. */
    InvoiceAmountCannotBeNegative(100027),

    /** Authentication required. */
    AuthenticationRequired(100029),

    /** There is already a payment in progress. */
    PaymentAlreadyInProgress(100031),

    /** Invalid subscription item. */
    InvalidSubscriptionItem(100034),

    /** This trial subscription cannot be modified. */
    TrialSubscriptionCannotBeModified(100035),

    /** Subscription items are required. */
    SubscriptionItemsRequired(100037),

    /** Cannot preview this subscription update. */
    CannotPreviewSubscriptionUpdated(100038),

    /** Invalid subscription_items. */
    InvalidSubscriptionItems(100040),

    /** Subscription renewal is in progress. */
    SubscriptionRenewalInProgress(100042),

    /** Invalid price tier order. */
    InvalidPriceTierOrder(100046),

    /** Confirmation required. */
    ConfirmationRequired(100047),

    /** Could not process this refund. Please contact support.. */
    CouldNotProcessRefund(100048),

    /** Invalid currency for payment. */
    InvalidCurrencyForPayment(100051),

    /** Invalid currency for subscription plan. */
    InvalidCurrencyForSubscription(100052),

    /** Ineligible for subscription. */
    IneligibleForSubscription(100053),

    /** The card was declined. */
    CardDeclined(100054),

    /** Pending purchase could not be verified. */
    PendingPurchaseNotVerified(100055),

    /** This client needs to be authorized for purchases. We've sent you an email. Click the link on the email and then retry the purchase.. */
    ClientNeedsAuthorizationForPurchases(100056),

    /** This payment method cannot be used. */
    PaymentMethodCannotBeUsed(100058),

    /** Open invoice not found. */
    OpenInvoiceNotFound(100059),

    /** Non refundable payment source. */
    NonRefundablePaymentSource(100060),

    /** Subscription metadata is invalid. */
    InvalidSubscriptionMetadata(100062),

    /** Currency cannot be changed without a payment source. */
    CurrencyCannotBeChangedWithoutPaymentSource(100066),

    /** Invalid operation. */
    InvalidOperation(100069),

    /** ⚠️ BILLING_APPLE_SERVER_API_ERROR. */
    AppleServerAPIError(100070),

    /** User is not eligible for creating referrals. */
    UserIneligibleForCreatingReferrals(100071),

    /** Error while generating PDF. */
    ErrorGeneratingPDF(100076),

    /** ⚠️ BILLING_TRIAL_REDEMPTION_DISABLED. */
    CannotRedeemTrial(100078),

    /** Pause is temporarily unavailable. Please try again later. */
    PauseTemporarilyUnavailable(100079),

    /** Pause is already pending. */
    PauseAlreadyPending(100080),

    /** Subscriptions not eligible for pause. */
    SubscriptionsNotEligibleForPause(100081),

    /** 100082	Invalid pause interval. */
    InvalidPauseInterval(100082),

    /** ⚠️ BILLING_ALREADY_PAUSED. */
    BillingPausedAlready(100083),

    /** ⚠️ BILLING_CANNOT_CHARGE_ZERO_AMOUNT. */
    CannotChargeZeroAmount(100084),

    /** Discount not available for redemption. */
    DiscountUnavailableForRedemption(100086),

    /** Entitlement already fulfilled. */
    EntitlementAlreadyFulfilled(100089),

    /** Invalid update for paused or pause pending subscription. */
    InvalidUpdateForPausedSubscription(100094),

    /** Pause subscription is not available for this plan. */
    SubscriptionPauseUnavailable(100095),

    /** Bundle already purchased. */
    BundlePurchased(100096),

    /** Bundle already partially owned. */
    BundlePartiallyOwned(100097),

    /** Discord has stopped accepting payments through Sofort. Please add another payment method. */
    SofortPaymentsNotAccepted(100106),

    /** IIndex not yet available. */
    IndexNotAvailableYet(110000),

    /** Application not yet available. Try again later. */
    ApplicationNotAvailable(110001),

    /** ⚠️ LISTING_ALREADY_JOINED. */
    AlreadyJoinedListing(120000),

    /** ⚠️ LISTING_TOO_MANY_MEMBERS. */
    TooManyMembersForListing(120001),

    /** ⚠️ LISTING_JOIN_BLOCKED. */
    ListingJoinBlocked(120002),

    /** API resource is currently overloaded. Try again a little later. */
    APIResourceOverloaded(130000),

    /** Maximum number of achievements reached. (1000) */
    MaxAchievementsReached(140000),

    /** Server does not pass partner requirements. */
    PartnerRequirementsNotMet(150001),

    /** You must revoke your existing request to join before re-applying. */
    MustRevokeExistingRequestBeforeReApplying(150002),

    /** Cannot find a pending application for this user. */
    CannotFindApplicationForUser(150003),

    /** You already have a pending Partner application. */
    PartnerApplicationAlreadyExists(150004),

    /** The Stage is already open. */
    StageAlreadyOpen(150006),

    /** You cannot acknowledge this join request. */
    CannotAcknowledgeJoinRequest(150008),

    /** This user is already a member, join request is already closed. */
    UserIsAlreadyMember(150009),

    /** Server must raise verification level in order to perform this action. */
    ServerMustRaiseVerification(150011),

    /** Join request not found. */
    JoinRequestNotFound(150016),

    /** You do not have permission to see this join request. */
    NoPermissionToViewJoinRequest(150018),

    /** You cannot create or join an interview for this join request. */
    CannotCreateOrJoinInterviewForJoinRequest(150019),

    /** Join request interviews are not available for this guild. */
    JoinRequestInterviewsUnavailable(150020),

    /** Join request interview is full. */
    JoinRequestInterviewFull(150022),

    /** User is not eligible to join this server. */
    UserIneligibleToJoin(150023),

    /** You must wait another 7 day(s) before applying to this server again. */
    MustWait7DaysBeforeReApplying(150024),

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

    /** Message could not be found. */
    MessageNotFound(160008),

    /** Cannot reference a message without permission to read message history. */
    CannotReferenceMessageWithoutPermission(160009),

    /** NSFW channel message reference not allowed. */
    NSFWMessageReferenceNotAllowed(160010),

    /** Forward messages cannot have additional content. */
    ForwardMessagesCannotHaveAdditionalContent(160011),

    /** Forward message not allowed for monetized channel. */
    ForwardMessageNotAllowedForMonetizedChannel(160012),

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

    /** Poggermode has been temporarily disabled. */
    PoggermodeTemporarilyDisabled(170008),

    /** Cannot update a finished event. */
    CannotUpdateFinishedEvent(180000),

    /** Exactly one guild_id query parameter is required. */
    GuildIdRequired(180001),

    /** Failed to create stage needed for stage event. */
    FailedToCreateStage(180002),

    /** You must join the server to take this action. */
    MustJoinServer(180003),
    /** This route requires a recurring event. */
    RecurringEventRequired(180004),
    /** Recurring event exceptions must have a modified event field from the event series. */
    RecurringEventMustHaveModifiedEventField(180005),
    /** Cannot RSVP to a finished event. */
    CannotRSVPFinishedEvent(180006),
    /** We are temporarily not accepting Verified Server Applications while we tidy up things on our end. */
    VerifiedServerApplicationsTemporarilyUnavailable(181000),

    /** All apps must have a privacy policy. */
    MustHavePrivacyPolicy(190001),

    /** All apps must have a terms of service. */
    MustHaveTOS(190002),

    /** This application is not currently in the minimum number of servers required for intent verification. Please try again once in 75 servers. */
    InsufficientServersForIntentVerification(190003),

    /** Message was blocked by automatic moderation. */
    MessageBlockedByAutomaticModeration(200000),

    /** Title was blocked by automatic moderation. */
    TitleBlockedByAutomaticModeration(200001),

    /** Regex validation service unavailable. Please try again later. */
    RegexValidationServiceUnavailable(200002),

    /** You cannot join this server because your username contains content blocked by this sevrer. */
    UsernameContainsBlockedContent(200005),

    /** Monetization request has not been approved yet. */
    MonetizationRequestNotApproved(210000),

    /** The terms for monetization request have already been acked. */
    MonetizationRequestTermsAcked(210001),

    /** Cannot access application. */
    CannotAccessApplication(210002),

    /** The terms for monetization have not been accepted. */
    MonetizationRequestTermsNotAccepted(210003),

    /** ⚠️ TWO_FA_NOT_ENABLED. */
    TwoFANotEnabled(210011),

    /** Your monetization requirements are in an unexpected state. Please contact support. */
    MonetizationRequirementsUnexpected(210019),

    /** You are running old code. Please restart your app. */
    OldCodePleaseRestart(210020),

    /** Cannot publish a product without a benefit. */
    CannotPublishWithoutBenefit(210021),

    /** ⚠️ CREATOR_MONETIZATION_PAYMENT_TEAM_REQUIRED. */
    MonetizationPaymentTeamRequired(210026),

    /** Must complete setup and verification of payment account. */
    MustCompleteSetupOfPaymentAccount(210027),

    /** Payout account is not located in a currently-supported country. */
    PayoutAccountInUnsupportedCountry(210028),

    /** Webhooks posted to forum channels must have a thread_name or thread_id. */
    WebhookMissingThreadNameOrThreadId(220001),

    /** Webhooks posted to forum channels cannot have both a thread_name and thread_id. */
    WebhookCannotHaveThreadNameAndThreadId(220002),

    /** Webhooks can only create threads in forum channels. */
    WebhooksCanOnlyCreateThreadsInForumChannels(220003),

    /** Webhook services cannot be used in forum channels. */
    WebhookServicesCannotBeUsedInForumChannels(220004),

    /** Items in this channel cannot be featured. */
    ItemsInChannelCannotBeFeatured(230000),

    /** Message blocked by harmful links filter. */
    MessageBlockedByHarmfulLinksFilter(240000),

    /** User is not enrolled in the given quest. */
    UserNotOnQuest(260000),

    /** User has not completed the given drop. */
    UserNotCompletedDrop(260001),

    /** Unable to claim code for the given quest. */
    UnableToClaimQuestCode(260002),

    /** User has already claimed code for the given drop. */
    UserAlreadyClaimedDropCode(260003),

    /** Quest does not support codes for this platform. */
    QuestDoesNotSupportCodesOnPlatform(260004),

    /** User has already claimed a reward for the given quest. */
    UserAlreadyClaimedQuestCode(260010),

    /* 270000 - 270007 undocumented */

    /** Application does not meet activity requirements. */
    ActivityRequirementsNotMet(270008),

    /** This provider does not support two-way linking. */
    TwoWayLinkingUnsupported(280000),

    /** This provider does not support device code flow two-way linking. */
    DeviceCodeFlowTwoWayLinkingUnsupported(280001),

    /** Missing two-way user code. */
    MissingTwoWayUserCode(280002),

    /** User not authorized to create provider. */
    UserNotAuthorizedToCreateProvider(280003),

    /** User is not eligible for parent tools. */
    UserIneligibleForParentTools(290000),

    /** No link currently exists between users. */
    NoLinkBetweenUsers(290001),

    /** Too many links exist for one of the requested users. */
    TooManyLinksForRequestedUsers(290002),

    /** User link status cannot transition from current state to desired state. */
    UserLinkStateCannotTransition(290003),

    /** Teens cannot request an activity view for other users in family center. */
    TeensCannotRequestActivityView(290004),

    /** A link request already exists between the two users. */
    LinkAlreadyExists(290005),

    /** A linking code has not been generated for this use. */
    LinkingCodeNotGenerated(290006),

    /** The linking code is invalid or has expired. */
    LinkingCodeInvalid(290007),

    /** Clyde consent is required. */
    ClydeConsentRequired(310000),

    /** You are sending too many messages to Clyde. Try again later. */
    TooManyClydeMessages(310002),

    /** This backstory could result in potentially unsafe or harmful responses. Please modify the backstory and try again. */
    UnsafeHarmfulResponsesFromBackstory(310003),

    /** Clyde Profile Not found. */
    ClydeNotFound(310006),

    /** Access to sending messages in guild channels has been limited for the user. */
    GuildMessageSendingLimited(340001),

    /** Access to sending DMs has been limited for the user. */
    DmSendingLimited(340002),

    /** Access to sending messages in group DMs has been limited for the user. */
    GroupDmSendingLimited(340003),

    /** Access to uploading attachments to guilds has been limited for the user. */
    GuildsAttachmentUploadingLimited(340004),

    /** Access to uploading attachments to DMs has been limited for the user. */
    DmAttachmentUploadingLimited(340005),

    /** Access to uploading attachments to group DMs has been limited for the user. */
    GroupDmAttachmentUploadingLimited(340006),

    /** Access to sending friend request has been limited for the user. */
    SendingFriendRequestsLimited(340007),

    /** Access to creating new guilds has been limited for the user. */
    CreatingNewGuildsLimited(340009),

    /** Access to sending messages in server channels has been limited for the user. */
    ServerMessageSendingLimited(340013),

    /** Access to uploading attachments to servers has been limited for the user. */
    ServerAttachmentUploadingLimited(340014),

    /** Access to joining new servers has been limited for the user. */
    JoiningNewServersLimited(340015),

    /** Access to creating new servers has been limited for the user. */
    CreatingNewServersLimited(340016),

    /** Cannot enable onboarding, requirements are not met. */
    CannotEnableOnboarding(350000),

    /** Cannot update onboarding while below requirements. */
    CannotUpdateOnboarding(350001),

    /** Cannot update responses. You must complete pre-join onboarding first. */
    CannotUpdateResponses(350002),

    /** Onboarding channels must be readable by everyone. */
    OnboardingChannelsMustBeViewable(350003),

    /** Server is ineligible for summaries. */
    ServerIneligibleForSummaries(360000),

    /** Channel is ineligible for summaries. */
    ChannelIneligibleForSummaries(360001),

    /** Cannot join because you are on a timeout. */
    CannotJoinDueToTimeout(380000),

    /** Pack not found. */
    PackNotFound(390001),

    /** Must provide a pack id. */
    MustProvidePackId(390002),

    /** Inventory not enabled. */
    InventoryNotEnabled(390003),

    /** Inventory settings not enabled. */
    InventorySettingsNotEnabled(390004),

    /** Inventory pack limit reached. */
    InventoryPackLimitedReached(390010),

    /** Inventory pack has no contents. */
    InventoryPackHasNoContents(390011),

    /** Token is invalid. */
    TokenInvalid(400000),

    /** Access to file uploads has been limited for this server. */
    AccessFileUploadingLimited(400001),

    /** Access to inviting new users through invite links has been limited for this server. */
    AccessToInvitingUsersLimited(400002),

    /** ⚠️ GUILD_GO_LIVE_LIMITED_ACCESS*/
    AccessToLiveLimited(400003),

    /** You cannot invite this user. */
    CannotInviteUser(410001),

    /** User has already claimed promotion. */
    PromotionAlreadyClaimed(420002),

    /** ⚠️ PARTNER_PROMOTIONS_MAX_CLAIMS. */
    MaxPartnerPromotionClaimsReached(420003),

    /** Gift already claimed. */
    GiftAlreadyClaimed(420004),

    /** Previous purchase error. */
    PreviousPurchaseError(420005),

    /** New subscription required. */
    NewSubscriptionRequired(420006),

    /** Unknown gift. */
    UnknownGift(420007),

    /** Failed to ban user. */
    FailedToBanUser(500000),

    /** Poll voting blocked. */
    PollVotingBlocked(520000),

    /** Poll expired. */
    PollExpired(520001),

    /** Cannot create a poll in this type of channel. */
    CannotCreateAPollInChannelType(520002),

    /** Cannot edit a poll message. */
    CannotEditPollMessage(520003),

    /** Cannot use one or more emoji included with this poll. */
    CannotUseOneOrMoreEmojiIncludedInPoll(520004),

    /** Cannot expire a message that is not a poll. */
    CannotExpireMessageThatIsNotPoll(520006),

    /** Poll has already expired. */
    PollAlreadyExpired(520007),

    /** ⚠️ DSA_RSL_REPORT_NOT_FOUND. */
    RSLReportNotFound(521001),

    /** ⚠️ DSA_RSL_ALREADY_REQUESTED. */
    RSLAlreadyRequested(521002),

    /** ⚠️ DSA_RSL_LIMITED_TIME. */
    LimitedTimeRSL(521003),

    /** ⚠️ DSA_RSL_REPORT_INELIGIBLE. */
    RSLReportIneligible(521004),

    /** ⚠️ DSA_APPEAL_REQUEST_DEFLECTION. */
    AppealRequestDeflection(522001),

    /** Internal error occurred while processing the appeal. */
    InternalErrorDuringAppealProcessing(522002),

    /**  Your Discord application has not been granted the permission to use provisional accounts. */
    ApplicationNotGrantedForProvisionalAccounts(530000),

    /** Token is past expiration. */
    TokenExpired(530001),

    /** Token issuer is incorrect. */
    TokenIssuerIncorrect(530002),

    /** Token audience is incorrect. */
    TokenAudienceIncorrect(530003),

    /** Token was issued too long ago. */
    TokenTooOld(530004),

    /** Discord failed to generate a unique username within the allotted time. This is not a terminal error, and should resolve itself upon a retry. */
    FailedToGenerateUniqueUsername(530006),

    /** Invalid client. */
    InvalidClient(530007),

    /** User account is non-provisional and should be authed through OAuth2. */
    UserAccountNonProvisionalShouldBeAuthed(530010),

    /** OIDC JWKS is not valid: {reason}. */
    InvalidJWKS(530012),

    /** OIDC JWT id token is invalid. */
    InvalidJWTIdToken(530020),

    /** Token project_id doesn't match the application's configured Unity project ID. */
    TokenProjectIdDoesNotMatchUnityProjectID(530022),

    /** Public key must be present. */
    PublicKeyRequired(560000),

    /** Public key was not in expected format. */
    MalformedPublicKey(560001),

    /** Public key self-signature was invalid. */
    InvalidPublicKeySignature(560003),

    /** Public key is not unique. */
    PublicKeyNotUnique(560004),

    /** Content inventory entry cannot be shared to this channel. */
    ContentInventoryCannotBeSharedToChannel(560006),

    /** Channels linked to lobbies cannot be age-restricted. */
    ChannelsLinkedToLobbiesCannotBeAgeRestricted(570001),

    /** Insufficient balance. */
    InsufficientBalance(590001),

    /** This SKU does not have a virtual currency price. */
    SKUHasNoVirtualCurrencyPrice(590005),

    /** Invalid token. */
    InvalidToken(620000),

    /** ⚠️ ACCOUNT_REVERT_EMAIL_ALREADY_TAKEN. */
    RevertEmailAlreadyTaken(620001),

    /** ⚠️ ACCOUNT_REVERT_ACCOUNT_NOT_FOUND. */
    RevertAccountNotFound(620002),

    /** Invalid search query. */
    InvalidSearchQuery(630001),

    /** User does not have the necessary permission. */
    UserHasNoPermissions(650003),

    /** Subscription is ineligible for this campaign. */
    SubscriptionIneligibleForCampaign(660001),

    /** Cannot deactivate entitlement. */
    CannotDeactivateEntitlement(670002),

    /** Entitlement purchase limit reached. */
    EntitlementPurchaseLimitReached(670003),

    /** Dependent entitlement not found. */
    DependentEntitlementNotFound(670004),

    /** Insufficient boosts. */
    InsufficientBoosts(670005),

    /** Missing guild feature. */
    MissingGuildFeature(670006),

    /** Invalid role color. */
    InvalidRoleColor(670008),

    /** Report not found. */
    ReportNotFound(690001),

    /** Too many server tag updates. Please try again later. */
    TooManyServerTagUpdates(690003),

    /** Activity link not found. */
    ActivityLinkNotFound(5250001),
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
