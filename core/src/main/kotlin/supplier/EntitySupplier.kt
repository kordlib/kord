package dev.kord.core.supplier

import dev.kord.common.entity.ChannelType.Unknown
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.entity.*
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.entity.application.GuildApplicationCommand
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.entity.interaction.followup.FollowupMessage
import dev.kord.core.exception.EntityNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * An abstraction that allows for requesting Discord entities.
 *
 * @see RestEntitySupplier
 * @see CacheEntitySupplier
 */
public interface EntitySupplier {

    /**
     * Requests all [guilds][Guild] this bot is known to be part of.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val guilds: Flow<Guild>

    /**
     * Requests all [regions][Region] known to this bot.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val regions: Flow<Region>

    /**
     * Requests the [Guild] with the given [id], returns `null` when the guild isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     */
    public suspend fun getGuildOrNull(id: Snowflake): Guild?

    /**
     * Requests the [Guild] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     * @throws EntityNotFoundException if the guild is null.
     */
    public suspend fun getGuild(id: Snowflake): Guild = getGuildOrNull(id) ?: EntityNotFoundException.guildNotFound(id)

    /**
     * Requests the preview of the guild matching the [guildId].
     * If the bot is not in the guild, then the guild must be lurkable.
     * Returns `null` if the preview was not found.
     *
     * @throws RequestException if something went wrong during the request.
     */
    public suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview?

    /**
     * Requests the preview of the guild matching the [guildId].
     * If the bot is not in the guild, then the guild must be lurkable.
     *
     * @throws RequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the preview was not found.
     */
    public suspend fun getGuildPreview(guildId: Snowflake): GuildPreview =
        getGuildPreviewOrNull(guildId) ?: EntityNotFoundException.entityNotFound("Guild Preview", guildId)

    /**
     * Requests to get the widget of a [Guild] with the given [id][guildId],
     * returns null if the [GuildWidget] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget?


    /**
     * Requests to get the widget of a [Guild] with the given [id][guildId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [GuildWidget] wasn't present.
     */
    public suspend fun getGuildWidget(guildId: Snowflake): GuildWidget =
        getGuildWidgetOrNull(guildId) ?: EntityNotFoundException.widgetNotFound(guildId)

    /**
     * Requests the [Channel] with the given [id], returns `null` when the channel isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     */
    public suspend fun getChannelOrNull(id: Snowflake): Channel?

    /**
     * Requests the [Channel] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     * @throws EntityNotFoundException if the channel is null.
     */
    public suspend fun getChannel(id: Snowflake): Channel =
        getChannelOrNull(id) ?: EntityNotFoundException.channelNotFound<Channel>(id)

    /**
     * Requests the [channels][TopGuildChannel] of the [Guild] with the given [guildId], channels with an [Unknown] type will be filtered out of the list.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getGuildChannels(guildId: Snowflake): Flow<TopGuildChannel>

    /**
     * Requests the pinned [messages][Message] of the [Channel] with the given [channelId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getChannelPins(channelId: Snowflake): Flow<Message>

    /**
     * Requests the [Member] with the given [userId] in the [Guild] wit the given [guildId],
     * returns `null` when the member isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the member.
     */
    public suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member?

    /**
     * Requests the [Member] with the given [userId] in the [Guild] wit the given [guildId].
     *
     * @throws RequestException if something went wrong while retrieving the member.
     * @throws EntityNotFoundException if the member was null.
     */
    public suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member =
        getMemberOrNull(guildId, userId)
            ?: EntityNotFoundException.memberNotFound(guildId = guildId, userId = userId)

    /**
     * Requests the [Message] with the given [messageId] in the [MessageChannel] with the given [channelId],
     * returns `null` when the message isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the message.
     */
    public suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message?

    /**
     * Requests the [Message] with the given [messageId] in the [MessageChannel] with the given [channelId].
     *
     * @throws RequestException if something went wrong while retrieving the message.
     * @throws EntityNotFoundException if the message is null.
     */
    public suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message =
        getMessageOrNull(channelId, messageId) ?: EntityNotFoundException.messageNotFound(channelId, messageId)

    /**
     * Requests a flow of messages created after the [Message] with the [messageId]
     * in the [channel][MessageChannel] with the [channelId].
     *
     * The flow may use paginated requests to supply messages, [limit] will limit the maximum number of messages
     * supplied and may optimize the batch size accordingly. `null` means no limit.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if a [limit] < 1 was supplied.
     */
    public fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int? = null): Flow<Message>

    /**
     * Requests a flow of messages created before the [Message] with the [messageId]
     * in the [channel][MessageChannel] with the [channelId].
     *
     * The flow may use paginated requests to supply messages, [limit] will limit the maximum number of messages
     * supplied and may optimize the batch size accordingly. `null` means no limit.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if a [limit] < 1 was supplied.
     */
    public fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int? = null): Flow<Message>

    /**
     * Requests a flow of messages created around the [Message] with the [messageId]
     * in the [channel][MessageChannel] with the [channelId].
     *
     * Unlike [getMessagesAfter] and [getMessagesBefore], this flow can return **a maximum of 100 messages**.
     * As such, the accepted range of [limit] is reduced to 1..100.
     *
     * Supplied messages will be equally distributed
     * before and after the [messageId]. The remaining message for an odd [limit] is undefined and may appear on either
     * side or no side at all.
     *
     * If a [Message] with the given [messageId] exists, the flow might also contain it, so it **could have one more
     * element than the given [limit]**.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if the [limit] is outside the range of 1..100.
     */
    public fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int = 100): Flow<Message>

    /**
     * Requests the [User] this bot represents, returns null when the user isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the user.
     */
    public suspend fun getSelfOrNull(): User?

    /**
     * Requests the [User] this bot represents.
     *
     * @throws RequestException if something went wrong while retrieving the user.
     * @throws EntityNotFoundException if the user was null.
     */
    public suspend fun getSelf(): User = getSelfOrNull() ?: EntityNotFoundException.selfNotFound()

    /**
     * Requests the [User] with the given [id], returns null when the user isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the user.
     */
    public suspend fun getUserOrNull(id: Snowflake): User?

    /**
     * Requests the [User] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the user.
     * @throws EntityNotFoundException if the user was null.
     */
    public suspend fun getUser(id: Snowflake): User = getUserOrNull(id) ?: EntityNotFoundException.userNotFound(id)

    /**
     * Requests the [Role] with the given [roleId] in the [Guild] wit the given [guildId],
     * returns null when the role isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the role.
     */
    public suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role?

    /**
     * Requests the [Role] with the given [roleId] in the [Guild] wit the given [guildId].
     *
     * @throws RequestException if something went wrong while retrieving the role.
     * @throws EntityNotFoundException if the role was null.
     */
    public suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role =
        getRoleOrNull(guildId, roleId) ?: EntityNotFoundException.roleNotFound(guildId, roleId)

    /**
     * Requests the [roles][Role] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getGuildRoles(guildId: Snowflake): Flow<Role>

    /**
     * Requests the [Ban] for the user the given [userId] in the [Guild] wit the given [guildId],
     * returns null when the ban isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the ban.
     */
    public suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban?

    /**
     * Requests the [Ban] for the user the given [userId] in the [Guild] wit the given [guildId],
     * returns null when the ban isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the ban.
     * @throws EntityNotFoundException if the ban was null.
     */
    public suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): Ban = getGuildBanOrNull(guildId, userId)
        ?: EntityNotFoundException.banNotFound(guildId, userId)

    /**
     * Requests the [bans][Ban] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getGuildBans(guildId: Snowflake, limit: Int? = null): Flow<Ban>

    /**
     * Requests the [members][Member] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getGuildMembers(guildId: Snowflake, limit: Int? = null): Flow<Member>

    /**
     * Requests the [regions][Region] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region>

    /**
     * Requests the [GuildEmoji] with the [emojiId] in the [Guild] wit the given [guildId],
     * returns null when the emoji isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the emoji.
     */
    public suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji?

    /**
     * Requests the [GuildEmoji] with the [emojiId] in the [Guild] wit the given [guildId].
     *
     * @throws RequestException if something went wrong while retrieving the emoji.
     * @throws EntityNotFoundException if the emoji was null.
     */
    public suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): GuildEmoji =
        getEmojiOrNull(guildId, emojiId) ?: EntityNotFoundException.emojiNotFound(guildId, emojiId)

    /**
     * Requests the [guild emojis][GuildEmoji] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getEmojis(guildId: Snowflake): Flow<GuildEmoji>

    /**
     * Requests [guilds][Guild] this bot is known to be part of.
     *
     * The flow may use paginated requests to supply guilds, [limit] will limit the maximum number of guilds
     * supplied and may optimize the batch size accordingly. `null` means no limit.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if a [limit] < 1 was supplied.
     */
    public fun getCurrentUserGuilds(limit: Int? = null): Flow<Guild>

    /**
     * Requests the [webhooks][Webhook] of the [MessageChannel] with the given [channelId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook>

    /**
     * Requests the [webhooks][Webhook] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook>

    /**
     * Requests the [Webhook] with the given [id], returns `null` when the webhook isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the webhook.
     */
    public suspend fun getWebhookOrNull(id: Snowflake): Webhook?

    /**
     * Requests the [Webhook] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the webhook.
     * @throws EntityNotFoundException if the webhook was null.
     */
    public suspend fun getWebhook(id: Snowflake): Webhook =
        getWebhookOrNull(id) ?: EntityNotFoundException.webhookNotFound(id)

    /**
     * Requests the [Webhook] with the given [id] using the [token] for authentication,
     * returns null when the webhook isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the webhook.
     */
    public suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook?

    /**
     * Requests the [Webhook] with the given [id] using the [token] for authentication.
     *
     * @throws RequestException if something went wrong while retrieving the webhook.
     * @throws EntityNotFoundException if the webhook was null.
     */
    public suspend fun getWebhookWithToken(id: Snowflake, token: String): Webhook =
        getWebhookWithTokenOrNull(id, token) ?: EntityNotFoundException.webhookNotFound(id)

    /**
     * Requests the [Message] with the given [messageId] previously sent from a [Webhook] with the given [webhookId]
     * using the [token] for authentication, returns `null` when the message isn't present.
     *
     * If the message is in a thread, [threadId] must be specified.
     *
     * @throws RequestException if something went wrong while retrieving the message.
     */
    public suspend fun getWebhookMessageOrNull(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake? = null,
    ): Message?

    /**
     * Requests the [Message] with the given [messageId] previously sent from a [Webhook] with the given [webhookId]
     * using the [token] for authentication.
     *
     * If the message is in a thread, [threadId] must be specified.
     *
     * @throws RequestException if something went wrong while retrieving the message.
     * @throws EntityNotFoundException if the message is null.
     */
    public suspend fun getWebhookMessage(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake? = null,
    ): Message = getWebhookMessageOrNull(webhookId, token, messageId, threadId)
        ?: EntityNotFoundException.webhookMessageNotFound(webhookId, token, messageId, threadId)

    /**
     * Requests the [Template] with the given [code].
     * returns null when the template isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the template.
     */
    public suspend fun getTemplateOrNull(code: String): Template?

    /**
     * Requests the [Template] with the given [code].
     * returns null when the webhook isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the template.
     * @throws EntityNotFoundException if template was null.
     */
    public suspend fun getTemplate(code: String): Template =
        getTemplateOrNull(code) ?: EntityNotFoundException.templateNotFound(code)

    public fun getTemplates(guildId: Snowflake): Flow<Template>

    public suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance?

    public suspend fun getStageInstance(channelId: Snowflake): StageInstance =
        getStageInstanceOrNull(channelId) ?: EntityNotFoundException.stageInstanceNotFound(channelId)

    public fun getThreadMembers(channelId: Snowflake): Flow<ThreadMember>

    public fun getActiveThreads(guildId: Snowflake): Flow<ThreadChannel>

    public fun getPublicArchivedThreads(
        channelId: Snowflake,
        before: Instant? = null,
        limit: Int? = null,
    ): Flow<ThreadChannel>

    public fun getPrivateArchivedThreads(
        channelId: Snowflake,
        before: Instant? = null,
        limit: Int? = null,
    ): Flow<ThreadChannel>

    public fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Snowflake? = null,
        limit: Int? = null,
    ): Flow<ThreadChannel>

    public fun getGuildApplicationCommands(applicationId: Snowflake, guildId: Snowflake, withLocalizations: Boolean? = null): Flow<GuildApplicationCommand>

    public suspend fun getGuildApplicationCommandOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): GuildApplicationCommand?

    public suspend fun getGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): GuildApplicationCommand =

        getGuildApplicationCommandOrNull(applicationId, guildId, commandId)
            ?: EntityNotFoundException.applicationCommandNotFound<GuildApplicationCommand>(
                commandId
            )

    public suspend fun getGlobalApplicationCommandOrNull(
        applicationId: Snowflake,
        commandId: Snowflake
    ): GlobalApplicationCommand?

    public suspend fun getGlobalApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake
    ): GlobalApplicationCommand =
        getGlobalApplicationCommandOrNull(applicationId, commandId)
            ?: EntityNotFoundException.applicationCommandNotFound<GlobalApplicationCommand>(commandId)

    public fun getGlobalApplicationCommands(applicationId: Snowflake, withLocalizations: Boolean? = null): Flow<GlobalApplicationCommand>


    public suspend fun getApplicationCommandPermissionsOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): ApplicationCommandPermissions?


    public suspend fun getApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): ApplicationCommandPermissions = getApplicationCommandPermissionsOrNull(applicationId, guildId, commandId)
        ?: EntityNotFoundException.applicationCommandPermissionsNotFound(commandId)


    public fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
    ): Flow<ApplicationCommandPermissions>

    /**
     * Requests a followup message for an interaction response.
     * Returns `null` if the followup message isn't present.
     *
     * @throws RequestException if something went wrong during the request.
     */
    public suspend fun getFollowupMessageOrNull(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): FollowupMessage?

    /**
     * Requests a followup message for an interaction response.
     *
     * @throws RequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the followup message is null.
     */
    public suspend fun getFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): FollowupMessage =
        getFollowupMessageOrNull(applicationId, interactionToken, messageId)
            ?: EntityNotFoundException.followupMessageNotFound(interactionToken, messageId)

    public fun getGuildScheduledEvents(guildId: Snowflake): Flow<GuildScheduledEvent>

    public suspend fun getGuildScheduledEventOrNull(guildId: Snowflake, eventId: Snowflake): GuildScheduledEvent?

    public suspend fun getGuildScheduledEvent(guildId: Snowflake, eventId: Snowflake): GuildScheduledEvent =
        getGuildScheduledEventOrNull(guildId, eventId) ?: EntityNotFoundException.guildScheduledEventNotFound(eventId)


    public fun getGuildScheduledEventUsers(guildId: Snowflake, eventId: Snowflake, limit: Int? = null): Flow<User> =
        getGuildScheduledEventUsersAfter(guildId, eventId, after = Snowflake.min, limit)

    public fun getGuildScheduledEventUsersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int? = null,
    ): Flow<User>

    public fun getGuildScheduledEventUsersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int? = null,
    ): Flow<User>


    public fun getGuildScheduledEventMembers(guildId: Snowflake, eventId: Snowflake, limit: Int? = null): Flow<Member> =
        getGuildScheduledEventMembersAfter(guildId, eventId, after = Snowflake.min, limit)

    public fun getGuildScheduledEventMembersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int? = null,
    ): Flow<Member>

    public fun getGuildScheduledEventMembersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int? = null,
    ): Flow<Member>


    public suspend fun getStickerOrNull(id: Snowflake): Sticker?

    public suspend fun getSticker(id: Snowflake): Sticker =
        getStickerOrNull(id) ?: EntityNotFoundException.stickerNotFound(id)

    public suspend fun getGuildStickerOrNull(guildId: Snowflake, id: Snowflake): GuildSticker?

    public suspend fun getGuildSticker(guildId: Snowflake, id: Snowflake): GuildSticker =
        getGuildStickerOrNull(guildId, id) ?: EntityNotFoundException.stickerNotFound(id)

    public fun getNitroStickerPacks(): Flow<StickerPack>

    public fun getGuildStickers(guildId: Snowflake): Flow<GuildSticker>
}


/**
 * Requests the [Channel] with the given [id] as type [T], returns null if the
 * channel isn't present or if the channel is not of type [T].
 *
 * @throws RequestException if something went wrong while retrieving the channel.
 */
public suspend inline fun <reified T : Channel> EntitySupplier.getChannelOfOrNull(id: Snowflake): T? =
    getChannelOrNull(id) as? T

/**
 * Requests the [Channel] with the given [id] as type [T].
 *
 * @throws RequestException if something went wrong while retrieving the channel.
 * @throws EntityNotFoundException if the channel is null.
 * @throws ClassCastException if the returned Channel is not of type [T].
 */
public suspend inline fun <reified T : Channel> EntitySupplier.getChannelOf(id: Snowflake): T =
    (getChannelOrNull(id) ?: EntityNotFoundException.channelNotFound<T>(id)) as T


/**
 * Requests the [GlobalApplicationCommand] with the given [id] as type [T].
 *
 * @throws RequestException if something went wrong while retrieving the global application.
 * @throws EntityNotFoundException if the global application is null.
 * @throws ClassCastException if the returned GlobalApplication is not of type [T].
 */
public suspend inline fun <reified T : GlobalApplicationCommand> EntitySupplier.getGlobalApplicationOf(
    applicationId: Snowflake,
    id: Snowflake
): T = getGlobalApplicationOfOrNull(applicationId, id) ?: EntityNotFoundException.applicationCommandNotFound<T>(id)


/**
 * Requests the [GlobalApplicationCommand] with the given [id] as type [T], returns null if the
 * command application isn't present or if the channel is not of type [T].
 *
 * @throws RequestException if something went wrong while retrieving the application command.
 */
public suspend inline fun <reified T : GlobalApplicationCommand> EntitySupplier.getGlobalApplicationOfOrNull(
    applicationId: Snowflake,
    id: Snowflake
): T? = getGlobalApplicationCommandOrNull(applicationId, id) as? T


/**
 * Requests the [GuildApplicationCommand] with the given [id] as type [T], returns null if the
 * command application isn't present or if the channel is not of type [T].
 *
 * @throws RequestException if something went wrong while retrieving the application command.
 */
public suspend inline fun <reified T : GuildApplicationCommand> EntitySupplier.getGuildApplicationCommandOfOrNull(
    applicationId: Snowflake,
    guildId: Snowflake,
    id: Snowflake
): T? = getGuildApplicationCommandOrNull(applicationId, guildId, id) as? T


/**
 * Requests the [GuildApplicationCommand] with the given [id] as type [T].
 *
 * @throws RequestException if something went wrong while retrieving the guild application.
 * @throws EntityNotFoundException if the guild application is null.
 * @throws ClassCastException if the returned GuildApplication is not of type [T].
 */
public suspend inline fun <reified T : GuildApplicationCommand> EntitySupplier.getGuildApplicationCommandOf(
    applicationId: Snowflake,
    guildId: Snowflake,
    id: Snowflake
): T =
    (getGuildApplicationCommandOrNull(applicationId, guildId, id)
        ?: EntityNotFoundException.applicationCommandNotFound<T>(id)) as T


/**
 * Requests the [GlobalApplicationCommand] with the given [id] as type [T].
 *
 * @throws RequestException if something went wrong while retrieving the global application.
 * @throws EntityNotFoundException if the global application is null.
 * @throws ClassCastException if the returned GlobalApplication is not of type [T].
 */
public suspend inline fun <reified T : GlobalApplicationCommand> EntitySupplier.getGlobalApplicationCommandOf(
    applicationId: Snowflake,
    id: Snowflake
): T =
    (getGlobalApplicationCommandOrNull(applicationId, id)
        ?: EntityNotFoundException.applicationCommandNotFound<T>(id)) as T


/**
 * Requests the [GuildApplicationCommand] with the given [id] as type [T], returns null if the
 * command application isn't present or if the channel is not of type [T].
 *
 * @throws RequestException if something went wrong while retrieving the application command.
 */
public suspend inline fun <reified T : GlobalApplicationCommand> EntitySupplier.getGlobalApplicationCommandOfOrNull(
    applicationId: Snowflake,
    id: Snowflake
): T? = getGlobalApplicationCommandOrNull(applicationId, id) as? T
