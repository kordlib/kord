package dev.kord.rest.json

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Detailed error codes sent Discord API  in the JSON error response.
 *
 * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
 * */
@Serializable(with = JsonErrorCode.JsonErrorCodeSerializer::class)
enum class JsonErrorCode(val code: Int) {
    /**
     * Undocumented error
     */
    Unknown(-1),

    /**
     * General error (such as a malformed request body, amongst other things).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    General(0),

    /**
     * Unknown account.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownAccount(10001),

    /**
     * Unknown application.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownApplication(10002),

    /**
     * Unknown channel.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownChannel(10003),

    /**
     * Unknown guild.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownGuild(10004),

    /**
     * Unknown integration.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownIntegration(10005),

    /**
     * Unknown invite.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownInvite(10006),

    /**
     * Unknown member.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownMember(10007),

    /**
     * Unknown message.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownMessage(10008),

    /**
     * Unknown permission overwrite.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownPermissionOverwrite(10009),

    /**
     * Unknown provider.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownProvider(10010),

    /**
     * Unknown role.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownRole(10011),

    /**
     * Unknown token.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownToken(10012),

    /**
     * Unknown user.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownUser(10013),

    /**
     * Unknown emoji.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownEmoji(10014),

    /**
     * Unknown webhook.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownWebhook(10015),

    /**
     * Unknown webhook service.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownWebhookService(10016),

    /**
     * Unknown session.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownSession(10020),

    /**
     * Unknown ban.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownBan(10026),

    /**
     * Unknown SKU.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownSKU(10027),

    /**
     * Unknown store Listing.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownStoreListing(10028),

    /**
     * Unknown entitlement.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownEntitlement(10029),

    /**
     * Unknown build.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownBuild(10030),

    /**
     * Unknown lobby.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownLobby(10031),

    /**
     * Unknown branch.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownBranch(10032),

    /**
     * Unknown store directory layout.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownStoreDirectoryLayout(10033),

    /**
     * Unknown redistributable.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownRedistributable(10036),

    /**
     * Unknown gift code.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownGiftCode(10038),

    /**
     * Unknown guild template.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownGuildTemplate(10057),

    /**
     * Unknown discoverable server category.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownDiscoverableServerCategory(10059),

    /**
     * Unknown sticker.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownSticker(10060),

    /**
     * Unknown interaction.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownInteraction(10062),

    /**
     * Unknown application command.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownApplicationCommand(10063),

    /**
    * Unknown application command permissions.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownApplicationCommandPermissions(10066),

    /**
     * Unknown Stage Instance.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownStageInstance(10067),

    /**
     * Unknown Guild Member Verification Form.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownGuildMemberVerificationForm(10068),

    /**
     * Unknown Guild Welcome Screen.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownGuildWelcomeScreen(10069),

    /**
     * Bots cannot use this endpoint.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    NonBotEndpoint(20001),

    /**
     * Only bots can use this endpoint.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    BotsEndpoint(20002),

    /**
     * Explicit content cannot be sent to the desired recipient(s).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotSendExplicitContent(20009),

    /**
     * You are not authorized to perform this action on this application.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnauthorizedForAction(20012),

    /**
     * This action cannot be performed due to slow-mode rate limit.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    SlowModeRateLimit(20016),

    /**
     * Only the owner of this account can perform this action.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    OnlyOwner(20018),

    /**
     * This message cannot be edited due to announcement rate limits.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    AnnouncementRateLimit(2022),

    /**
     * The channel you are writing has hit the write rate limit.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    ChannelWriteRateLimit(20028),

    /**
     * Your Stage topic, server name, server description, or channel names contain words that are not allowed.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    DisallowedName(20031),

    /**
     * Guild premium subscription level too low.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    GuildSubscriptionTooLow(20035),

    /**
     * Maximum number of guilds reached (100).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxGuilds(30001),

    /**
     * Maximum number of friends reached (1000).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxFriends(30002),

    /**
     * Maximum number of pins reached for the channel (50).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxPins(30003),

    /**
     * Maximum number of recipients reached (10).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxRecipients(30004),

    /**
     * Maximum number of guild roles reached (250).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxGuildRoles(30005),

    /**
     * Maximum number of webhooks reached (10).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxWebhooks(30007),

    /**
     * Maximum number of emojis reached.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxEmojis(30008),

    /**
     * Maximum number of reactions reached (20).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxReactions(30010),

    /**
     * Maximum number of guild channels reached (500).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxGuildChannels(30013),

    /**
     * Maximum number of attachments in a message reached (10).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxAttachments(30015),

    /**
     * Maximum number of invites reached (1000).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxInvites(30016),

    /**
     * Maximum number of animated emojis reached.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxAnimatedEmojis(30018),

    /**
     * Maximum number of server members reached.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxServerMembers(30019),

    /**
     * Maximum number of server categories has been reached (5).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxServerCategories(30030),

    /**
     * Guild already has a template.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    GuildAlreadyHadTemplate(30031),

    /**
     * Max number of thread participants has been reached.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxThreadParticipants(30033),

    /**
     * Maximum number of bans for non-guild members have been exceeded.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxNonMemberBans(30035),

    /**
     * Maximum number of bans fetches has been reached.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxBanFetches(30037),

    /**
     * Maximum number of stickers reached.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxStickers(30039),

    /**
     * Unauthorized. Provide a valid token and try again.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    Unauthorized(40001),

    /**
     * You need to verify your account in order to perform this action.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    VerifyAccount(40002),

    /**
     * You are opening direct messages too fast.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    TooFastDM(40003),

    /**
     * Request entity too large. Try sending something smaller in size.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    RequestEntityTooLarge(40005),

    /**
     * This feature has been temporarily disabled server-side.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    TemporarilyDisabled(40006),

    /**
     * The user is banned from this guild.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UserBannedFromGuild(40007),

    /**
     * Target user is not connected to voice.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UserNotInVoice(40032),

    /**
     * This message has already been crossposted.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    AlreadyCrossposted(40041),

    /**
     * An application command with that name already exists.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    ApplicationCommandNameExists(40041),

    /**
     * Missing access.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MissingAccess(50001),

    /**
     * Invalid account type.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidAccountType(50002),

    /**
     * Cannot execute action on a DM channel.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotExecuteOnDM(50003),

    /**
     * Guild widget disabled.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    GuildWidgetDisabled(50004),

    /**
     * Cannot edit a message authored by another user.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotEditMessageByAnotherUser(50005),

    /**
     * Cannot send an empty message.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotSendEmptyMessage(50006),

    /**
     * Cannot send messages to this user.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotSendMessagesToUser(50007),

    /**
     * Cannot send messages in a voice channel.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotSendMessagesInVoiceChannel(50008),

    /**
     * Channel verification level is too high for you to gain access.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    ChannelVerificationTooHigh(50009),

    /**
     * OAuth2 application does not have a bot.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    OAuth2HasNoBot(50010),

    /**
     * OAuth2 application limit reachedOAuth2 application limit reached.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    OAuth2ApplicationLimit(50011),

    /**
     * Invalid OAuth2 state.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidOAuth2State(50012),

    /**
     * You lack permissions to perform that action.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    PermissionLack(50013),

    /**
     * Invalid authentication token provided.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidAuthToken(50014),

    /**
     * Provided too few or too many messages to delete. Must provide at least 2 and fewer than 100 messages to delete.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    TooLongNote(50015),

    /**
     * A message can only be pinned to the channel it was sent in.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    ProvidedMessageCountInsufficient(50016),

    /**
     * A message can only be pinned to the channel it was sent in.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotPinMessageFromAnotherChannel(50019),

    /**
     * Invite code was either invalid or taken.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InviteCodeInvalidOrTaken(50020),

    /**
     * Cannot execute action on a system message.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotExecuteOnSystemMessage(50021),

    /**
     * Cannot execute action on this channel type.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    WrongChannelType(50024),

    /**
     * Invalid OAuth2 access token provided.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidOAuth2AccessToken(50025),

    /**
     * Missing required OAuth2 scope.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MissingOAuthScope(50026),

    /**
     * Invalid webhook token provided.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidWebhookToken(50027),

    /**
     * Invalid role.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidRole(50028),

    /**
     * Invalid Recipient(s).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidRecipients(50033),

    /**
     * A message provided was too old to bulk delete.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    BulkDeleteOldMessage(50034),

    /**
     * Invalid form body (returned for both application/json and multipart/form-data bodies),
     * or invalid Content-Type provided.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidFormBody(50035),

    /**
     * An invite was accepted to a guild the application's bot is not in.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    NonBotGuildInviteAccepted(50036),

    /**
     * Invalid API version provided.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidAPIVersion(50041),

    /**
     * Cannot self-redeem this gift.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotSelfRedeemGift(50054),

    /**
     * Payment source required to redeem gift.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    GiftRequiresPaymentSource(50070),

    /**
     * Cannot delete a channel required for Community guilds.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    CannotDeleteRequiredCommunityChannel(50074),

    /**
     * Invalid sticker sent.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidStickerSent(50081),

    /**
     * Invalid thread notification settings.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidThreadNotificationSettings(50084),

    /**
     * `before` value is earlier than the thread creation date.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    BeforeValueBeforeThreadCreate(50085),

    /**
     * 2 Factor Authentication is required.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    Require2FA(60003),

    /**
     * Reaction was blocked.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    ReactionBlocked(90001),

    /**
     * API resource is currently overloaded. Try again a little later.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    APIResourceOverloaded(130000),

    /**
     * The Stage is already open.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    StageAlreadyOpen(150006),

    /**
     * A thread has already been created for this message.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MessageAlreadyHasThread(160004),

    /**
     * Thread is locked.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    TheadLocked(160005),

    /**
     * Maximum number of active threads reached.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxActiveThreads(160006),

    /**
     * Maximum number of active announcement threads reached.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxActiveAnnouncementThreads(160007),
    
    OperationOnAchievedThread(50083),

    InvalidThreadSettings(50084),

    InvalidThreadBefore(50085),;

    companion object JsonErrorCodeSerializer : KSerializer<JsonErrorCode> {
        override val descriptor = PrimitiveSerialDescriptor("JsonErrorCodeSerializer", PrimitiveKind.INT)


        override fun deserialize(decoder: Decoder): JsonErrorCode {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: JsonErrorCode) {
            encoder.encodeInt(value.code)
        }
    }

}
