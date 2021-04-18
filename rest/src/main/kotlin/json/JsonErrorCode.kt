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
     * Unknown redistributable.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownRedistributable(10036),

    /**
     * Unknown guild template.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownGuildTemplate(10057),

    /**
     * Unknown discovery category.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    UnknownDiscoveryCategory(10059),

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
     * Maximum number of guild discovery subcategories has been reached (5).
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    MaxGuildDiscoverySubCategories(30030),

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
     * Invalid OAuth2 access token provided.
     *
     * [JSON Error Codes](https://github.com/discord/discord-api-docs/blob/master/docs/topics/Opcodes_and_Status_Codes.md#json-error-codes)
     */
    InvalidOAuth2AccessToken(50025),

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
    APIResourceOverloaded(130000);

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