package dev.kord.core.supplier

import dev.kord.common.entity.ChannelType.Unknown
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.exception.EntityNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * An abstraction that allows for requesting Discord entities.
 *
 * @see RestEntitySupplier
 * @see CacheEntitySupplier
 */
interface EntitySupplier {

    /**
     * Requests all [guilds][Guild] this bot is known to be part of.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val guilds: Flow<Guild>

    /**
     * Requests all [regions][Region] known to this bot.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val regions: Flow<Region>

    /**
     * Requests the [Guild] with the given [id], returns `null` when the guild isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     */
    suspend fun getGuildOrNull(id: Snowflake): Guild?

    /**
     * Requests the [Guild] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     * @throws EntityNotFoundException if the guild is null.
     */
    suspend fun getGuild(id: Snowflake): Guild = getGuildOrNull(id) ?: EntityNotFoundException.guildNotFound(id)

    suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview?

    suspend fun getGuildPreview(guildId: Snowflake): GuildPreview =
        getGuildPreviewOrNull(guildId) ?: EntityNotFoundException.entityNotFound("Guild Preview", guildId)

    /**
     * Requests to get the widget of this guild through the [strategy],
     * returns null if the [GuildWidget] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget?


    /**
     * Requests to get the widget of this [guildId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [GuildWidget] wasn't present.
     */
    suspend fun getGuildWidget(guildId: Snowflake): GuildWidget =
        getGuildWidgetOrNull(guildId) ?: EntityNotFoundException.widgetNotFound(guildId)

    /**
     * Requests the [Channel] with the given [id], returns `null` when the channel isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     */
    suspend fun getChannelOrNull(id: Snowflake): Channel?

    /**
     * Requests the [Channel] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     * @throws EntityNotFoundException if the channel is null.
     */
    suspend fun getChannel(id: Snowflake): Channel = getChannelOrNull(id)!!

    /**
     * Requests the [channels][TopGuildChannel] of the [Guild] with the given [guildId], channels with an [Unknown] type will be filtered out of the list.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getGuildChannels(guildId: Snowflake): Flow<TopGuildChannel>

    /**
     * Requests the pinned [messages][Message] of the [Channel] with the given [channelId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getChannelPins(channelId: Snowflake): Flow<Message>

    /**
     * Requests the [Member] with the given [userId] in the [Guild] wit the given [guildId],
     * returns `null` when the member isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the member.
     */
    suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member?

    /**
     * Requests the [Member] with the given [userId] in the [Guild] wit the given [guildId].
     *
     * @throws RequestException if something went wrong while retrieving the member.
     * @throws EntityNotFoundException if the member was null.
     */
    suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member =
        getMemberOrNull(guildId, userId)
            ?: EntityNotFoundException.memberNotFound(guildId = guildId, userId = userId)

    /**
     * Requests the [Message] with the given [messageId] in the [MessageChannel] with the given [channelId],
     * returns `null` when the message isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the message.
     */
    suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message?

    /**
     * Requests the [Message] with the given [messageId] in the [MessageChannel] with the given [channelId].
     *
     * @throws RequestException if something went wrong while retrieving the message.
     * @throws EntityNotFoundException if the message is null.
     */
    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message =
        getMessageOrNull(channelId, messageId) ?: EntityNotFoundException.messageNotFound(channelId, messageId)

    /**
     * Requests a flow of messages created after the [Message] with the [messageId]
     * in the [channel][MessageChannel] with the [channelId].
     *
     * The flow may use paginated requests to supply messages, [limit] will limit the maximum number of messages
     * supplied and may optimize the batch size accordingly. A value of [Int.MAX_VALUE] means no limit.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if a [limit] < 1 was supplied.
     */
    fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message>

    /**
     * Requests a flow of messages created before the [Message] with the [messageId]
     * in the [channel][MessageChannel] with the [channelId].
     *
     * The flow may use paginated requests to supply messages, [limit] will limit the maximum number of messages
     * supplied and may optimize the batch size accordingly. A value of [Int.MAX_VALUE] means no limit.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if a [limit] < 1 was supplied.
     */
    fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message>

    /**
     * Requests a flow of messages created around the [Message] with the [messageId]
     * in the [channel][MessageChannel] with the [channelId].
     *
     * Unlike [getMessagesAfter] and [getMessagesBefore], this flow can return **a maximum of 100 messages**.
     * As such, the accepted range of [limit] is reduced to 1..100.
     *
     * Supplied messages will be equally distributed
     * before and after the [messageId]. The remaining message for an odd [limit] is undefined and may appear on either
     * side.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if the [limit] is outside the range of 1..100.
     */
    fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int = 100): Flow<Message>

    /**
     * Requests the [User] this bot represents, returns null when the user isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the user.
     */
    suspend fun getSelfOrNull(): User?

    /**
     * Requests the [User] this bot represents.
     *
     * @throws RequestException if something went wrong while retrieving the user.
     * @throws EntityNotFoundException if the user was null.
     */
    suspend fun getSelf(): User = getSelfOrNull() ?: EntityNotFoundException.selfNotFound()

    /**
     * Requests the [User] with the given [id], returns null when the user isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the user.
     */
    suspend fun getUserOrNull(id: Snowflake): User?

    /**
     * Requests the [User] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the user.
     * @throws EntityNotFoundException if the user was null.
     */
    suspend fun getUser(id: Snowflake): User = getUserOrNull(id) ?: EntityNotFoundException.userNotFound(id)

    /**
     * Requests the [Role] with the given [roleId] in the [Guild] wit the given [guildId],
     * returns null when the role isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the role.
     */
    suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role?

    /**
     * Requests the [Role] with the given [roleId] in the [Guild] wit the given [guildId].
     *
     * @throws RequestException if something went wrong while retrieving the role.
     * @throws EntityNotFoundException if the role was null.
     */
    suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role =
        getRoleOrNull(guildId, roleId) ?: EntityNotFoundException.roleNotFound(guildId, roleId)

    /**
     * Requests the [roles][Role] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getGuildRoles(guildId: Snowflake): Flow<Role>

    /**
     * Requests the [Ban] for the user the given [userId] in the [Guild] wit the given [guildId],
     * returns null when the ban isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the ban.
     */
    suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban?

    /**
     * Requests the [Ban] for the user the given [userId] in the [Guild] wit the given [guildId],
     * returns null when the ban isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the ban.
     * @throws EntityNotFoundException if the ban was null.
     */
    suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): Ban = getGuildBanOrNull(guildId, userId)
        ?: EntityNotFoundException.banNotFound(guildId, userId)

    /**
     * Requests the [bans][Ban] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getGuildBans(guildId: Snowflake): Flow<Ban>

    /**
     * Requests the [members][Member] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getGuildMembers(guildId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Member>

    /**
     * Requests the [regions][Region] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region>

    /**
     * Requests the [GuildEmoji] with the [emojiId] in the [Guild] wit the given [guildId],
     * returns null when the emoji isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the emoji.
     */
    suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji?

    /**
     * Requests the [GuildEmoji] with the [emojiId] in the [Guild] wit the given [guildId].
     *
     * @throws RequestException if something went wrong while retrieving the emoji.
     * @throws EntityNotFoundException if the emoji was null.
     */
    suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): GuildEmoji =
        getEmojiOrNull(guildId, emojiId) ?: EntityNotFoundException.emojiNotFound(guildId, emojiId)

    /**
     * Requests the [guild emojis][GuildEmoji] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getEmojis(guildId: Snowflake): Flow<GuildEmoji>

    /**
     * Requests the [guild emojis][GuildEmoji] of the [Guild] with the given [guildId].
     *
     *  The flow may use paginated requests to supply guilds, [limit] will limit the maximum number of guilds
     * supplied and may optimize the batch size accordingly. A value of [Int.MAX_VALUE] means no limit.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if a [limit] < 1 was supplied.
     */
    fun getCurrentUserGuilds(limit: Int = Int.MAX_VALUE): Flow<Guild>

    /**
     * Requests the [webhooks][Webhook] of the [MessageChannel] with the given [channelId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook>

    /**
     * Requests the [webhooks][Webhook] of the [Guild] with the given [guildId].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook>

    /**
     * Requests the [Webhook] with the given [id], returns `null` when the webhook isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the webhook.
     */
    suspend fun getWebhookOrNull(id: Snowflake): Webhook?

    /**
     * Requests the [Webhook] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the webhook.
     * @throws EntityNotFoundException if the webhook was null.
     */
    suspend fun getWebhook(id: Snowflake): Webhook =
        getWebhookOrNull(id) ?: EntityNotFoundException.webhookNotFound(id)

    /**
     * Requests the [Webhook] with the given [id] using the [token] for authentication,
     * returns null when the webhook isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the webhook.
     */
    suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook?

    /**
     * Requests the [Webhook] with the given [id] using the [token] for authentication.
     *
     * @throws RequestException if something went wrong while retrieving the webhook.
     * @throws EntityNotFoundException if the webhook was null.
     */
    suspend fun getWebhookWithToken(id: Snowflake, token: String): Webhook =
        getWebhookWithTokenOrNull(id, token) ?: EntityNotFoundException.webhookNotFound(id)

    /**
     * Requests the [Template] with the given [code].
     * returns null when the webhook isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the template.
     */


    suspend fun getTemplateOrNull(code: String): Template?

    /**
     * Requests the [Template] with the given [code].
     * returns null when the webhook isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the template.
     * @throws EntityNotFoundException if template was null.
     */
    suspend fun getTemplate(code: String): Template =
        getTemplateOrNull(code) ?: EntityNotFoundException.templateNotFound(code)

    fun getTemplates(guildId: Snowflake): Flow<Template>

    suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance?

    suspend fun getStageInstance(channelId: Snowflake): StageInstance =
        getStageInstanceOrNull(channelId) ?: EntityNotFoundException.stageInstanceNotFound(channelId)

    fun getThreadMembers(channelId: Snowflake): Flow<ThreadMember>

    fun getActiveThreads(guildId: Snowflake): Flow<ThreadChannel>

    fun getPublicArchivedThreads(channelId: Snowflake, before: Instant, limit: Int): Flow<ThreadChannel>

    fun getPrivateArchivedThreads(channelId: Snowflake, before: Instant, limit: Int): Flow<ThreadChannel>

    fun getJoinedPrivateArchivedThreads(channelId: Snowflake, before: Snowflake, limit: Int): Flow<ThreadChannel>
}


/**
 * Requests the [Channel] with the given [id] as type [T], returns null if the
 * channel isn't present or if the channel is not of type [T].
 *
 * @throws RequestException if something went wrong while retrieving the channel.
 */
suspend inline fun <reified T : Channel> EntitySupplier.getChannelOfOrNull(id: Snowflake): T? =
    getChannelOrNull(id) as? T

/**
 * Requests the [Channel] with the given [id] as type [T].
 *
 * @throws RequestException if something went wrong while retrieving the channel.
 * @throws EntityNotFoundException if the channel is null.
 * @throws ClassCastException if the returned Channel is not of type [T].
 */
suspend inline fun <reified T : Channel> EntitySupplier.getChannelOf(id: Snowflake): T =
    (getChannelOrNull(id) ?: EntityNotFoundException.channelNotFound<T>(id)) as T