package dev.kord.core.supplier

import dev.kord.common.entity.DiscordAuditLogEntry
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.*
import dev.kord.core.cache.data.*
import dev.kord.core.entity.*
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.entity.application.GuildApplicationCommand
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.entity.interaction.followup.FollowupMessage
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.rest.builder.auditlog.AuditLogGetRequestBuilder
import dev.kord.rest.json.request.AuditLogGetRequest
import dev.kord.rest.json.request.GuildScheduledEventUsersResponse
import dev.kord.rest.json.request.ListThreadsBySnowflakeRequest
import dev.kord.rest.json.request.ListThreadsByTimestampRequest
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.route.Position
import dev.kord.rest.service.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.min

/**
 * [EntitySupplier] that uses a [RestClient] to resolve entities.
 *
 * Error codes besides 429(Too Many Requests) will throw a [RestRequestException],
 * 404(Not Found) will be caught by the `xOrNull` variant and return null instead.
 *
 * This supplier will always be able to resolve entities if they exist according
 * to Discord, entities will always be up-to-date at the moment of the call.
 */
public class RestEntitySupplier(public val kord: Kord) : EntitySupplier {

    private inline val auditLog: AuditLogService get() = kord.rest.auditLog
    private inline val channel: ChannelService get() = kord.rest.channel
    private inline val emoji: EmojiService get() = kord.rest.emoji
    private inline val guild: GuildService get() = kord.rest.guild
    private inline val invite: InviteService get() = kord.rest.invite
    private inline val user: UserService get() = kord.rest.user
    private inline val voice: VoiceService get() = kord.rest.voice
    private inline val webhook: WebhookService get() = kord.rest.webhook
    private inline val application: ApplicationService get() = kord.rest.application
    private inline val template: TemplateService get() = kord.rest.template
    private inline val interaction: InteractionService get() = kord.rest.interaction
    private inline val stageInstance: StageInstanceService get() = kord.rest.stageInstance
    private inline val sticker: StickerService get() = kord.rest.sticker

    // max batchSize/limit: see https://discord.com/developers/docs/resources/user#get-current-user-guilds
    override val guilds: Flow<Guild>
        get() = paginateForwards(batchSize = 200, idSelector = { it.id }) { after ->
            user.getCurrentUserGuilds(position = after, limit = 200)
        }.map {
            val data = guild.getGuild(it.id).toData()
            Guild(data, kord)
        }

    override val regions: Flow<Region>
        get() = flow {
            voice.getVoiceRegions().forEach {
                val data = RegionData.from(OptionalSnowflake.Missing, it)
                emit(Region(data, kord))
            }
        }

    override suspend fun getChannelOrNull(id: Snowflake): Channel? =
        catchNotFound { Channel.from(channel.getChannel(id).toData(), kord) }

    override fun getGuildChannels(guildId: Snowflake): Flow<TopGuildChannel> = flow {
        for (channelData in guild.getGuildChannels(guildId))
            emit(Channel.from(ChannelData.from(channelData), kord))
    }.filterIsInstance()

    override fun getChannelPins(channelId: Snowflake): Flow<Message> = flow {
        for (messageData in channel.getChannelPins(channelId))
            emit(Message(MessageData.from(messageData), kord))
    }

    override suspend fun getGuildOrNull(id: Snowflake): Guild? =
        catchNotFound { Guild(guild.getGuild(id).toData(), kord) }

    override suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview? = catchNotFound {
        val discordPreview = guild.getGuildPreview(guildId)
        GuildPreview(GuildPreviewData.from(discordPreview), kord)
    }

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? = catchNotFound {
        val member = guild.getGuildMember(guildId = guildId, userId = userId)
        val memberData = member.toData(guildId = guildId, userId = userId)
        val userData = member.user.value!!.toData()
        Member(memberData, userData, kord)
    }

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? = catchNotFound {
        Message(channel.getMessage(channelId = channelId, messageId = messageId).toData(), kord)
    }

    // maxBatchSize: see https://discord.com/developers/docs/resources/channel#get-channel-messages
    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> =
        limitedPagination(limit, maxBatchSize = 100) { batchSize ->
            paginateForwards(batchSize, start = messageId, idSelector = { it.id }) { after ->
                channel.getMessages(channelId, position = after, limit = batchSize)
            }
        }.map {
            val data = MessageData.from(it)
            Message(data, kord)
        }

    // maxBatchSize: see https://discord.com/developers/docs/resources/channel#get-channel-messages
    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int?): Flow<Message> =
        limitedPagination(limit, maxBatchSize = 100) { batchSize ->
            paginateBackwards(batchSize, start = messageId, idSelector = { it.id }) { before ->
                channel.getMessages(channelId, position = before, limit = batchSize)
            }
        }.map {
            val data = MessageData.from(it)
            Message(data, kord)
        }

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit in 1..100) { "Expected limit to be in 1..100, but was $limit" }
        return flow {
            val responses = channel.getMessages(channelId, Position.Around(messageId), limit)
            for (response in responses) {
                val data = MessageData.from(response)
                emit(Message(data, kord))
            }
        }
    }

    override suspend fun getSelfOrNull(): User? = catchNotFound {
        User(user.getCurrentUser().toData(), kord)
    }

    override suspend fun getUserOrNull(id: Snowflake): User? = catchNotFound { User(user.getUser(id).toData(), kord) }

    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? = catchNotFound {
        val response = guild.getGuildRoles(guildId)
            .firstOrNull { it.id == roleId } ?: return@catchNotFound null

        Role(RoleData.from(guildId, response), kord)
    }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? = catchNotFound {
        val response = guild.getGuildBan(guildId, userId)
        val data = BanData.from(guildId, response)
        Ban(data, kord)
    }

    override fun getGuildRoles(guildId: Snowflake): Flow<Role> = flow {
        for (roleData in guild.getGuildRoles(guildId))
            emit(Role(RoleData.from(guildId, roleData), kord))
    }

    // maxBatchSize: see https://discord.com/developers/docs/resources/guild#get-guild-bans
    override fun getGuildBans(guildId: Snowflake, limit: Int?): Flow<Ban> =
        limitedPagination(limit, maxBatchSize = 1000) { batchSize ->
            paginateForwards(batchSize, idSelector = { it.user.id }) { after ->
                guild.getGuildBans(guildId, position = after, limit = batchSize)
            }
        }.map {
            val data = BanData.from(guildId, it)
            Ban(data, kord)
        }

    // maxBatchSize: see https://discord.com/developers/docs/resources/guild#list-guild-members
    override fun getGuildMembers(guildId: Snowflake, limit: Int?): Flow<Member> =
        limitedPagination(limit, maxBatchSize = 1000) { batchSize ->
            paginateForwards(batchSize, idSelector = { it.user.value!!.id }) { after ->
                guild.getGuildMembers(guildId, after, limit = batchSize)
            }
        }.map {
            val userData = it.user.value!!.toData()
            val memberData = it.toData(guildId = guildId, userId = it.user.value!!.id)
            Member(memberData, userData, kord)
        }


    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> = flow {
        for (region in guild.getGuildVoiceRegions(guildId)) {
            val data = RegionData.from(guildId.optionalSnowflake(), region)
            emit(Region(data, kord))
        }
    }

    public fun getReactors(channelId: Snowflake, messageId: Snowflake, emoji: ReactionEmoji): Flow<User> =
        // max batchSize/limit: see https://discord.com/developers/docs/resources/channel#get-reactions
        paginateForwards(batchSize = 100, idSelector = { it.id }) { after ->
            channel.getReactions(channelId, messageId, emoji = emoji.urlFormat, after, limit = 100)
        }.map {
            val data = UserData.from(it)
            User(data, kord)
        }

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? = catchNotFound {
        val data = EmojiData.from(guildId, emojiId, emoji.getEmoji(guildId, emojiId))
        GuildEmoji(data, kord)
    }

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> = flow {
        for (emoji in emoji.getEmojis(guildId)) {
            val data = EmojiData.from(guildId = guildId, id = emoji.id!!, entity = emoji)
            emit(GuildEmoji(data, kord))
        }
    }

    // maxBatchSize: see https://discord.com/developers/docs/resources/user#get-current-user-guilds
    override fun getCurrentUserGuilds(limit: Int?): Flow<Guild> =
        limitedPagination(limit, maxBatchSize = 200) { batchSize ->
            paginateForwards(batchSize, idSelector = { it.id }) { after ->
                user.getCurrentUserGuilds(position = after, limit = batchSize)
            }
        }.map {
            val data = guild.getGuild(it.id).toData()
            Guild(data, kord)
        }

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> = flow {
        for (webhook in webhook.getChannelWebhooks(channelId)) {
            val data = WebhookData.from(webhook)
            emit(Webhook(data, kord))
        }
    }

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> = flow {
        for (webhook in webhook.getGuildWebhooks(guildId)) {
            val data = WebhookData.from(webhook)
            emit(Webhook(data, kord))
        }
    }

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? = catchNotFound {
        val data = WebhookData.from(webhook.getWebhook(id))
        Webhook(data, kord)
    }

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? = catchNotFound {
        val data = WebhookData.from(webhook.getWebhookWithToken(id, token))
        Webhook(data, kord)
    }

    override suspend fun getWebhookMessageOrNull(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake?,
    ): Message? = catchNotFound {
        val response = webhook.getWebhookMessage(webhookId, token, messageId, threadId)
        val data = MessageData.from(response)
        Message(data, kord)
    }

    public suspend fun getInviteOrNull(
        code: String,
        withCounts: Boolean = true,
        withExpiration: Boolean = true,
        scheduledEventId: Snowflake? = null,
    ): Invite? = catchNotFound {
        val response = invite.getInvite(code, withCounts, withExpiration, scheduledEventId)
        Invite(InviteData.from(response), kord)
    }

    public suspend fun getInvite(
        code: String,
        withCounts: Boolean = true,
        withExpiration: Boolean = true,
        scheduledEventId: Snowflake? = null,
    ): Invite = getInviteOrNull(code, withCounts, withExpiration, scheduledEventId)
        ?: EntityNotFoundException.inviteNotFound(code)

    /**
     * Requests to get the information of the current application.
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     * @throws RestRequestException when the request failed.
     */
    public suspend fun getApplicationInfo(): Application {
        val response = application.getCurrentApplicationInfo()
        return Application(ApplicationData.from(response), kord)
    }

    override suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget? = catchNotFound {
        val response = guild.getGuildWidget(guildId)
        GuildWidget(GuildWidgetData.from(response), guildId, kord)
    }

    override suspend fun getTemplateOrNull(code: String): Template? = catchNotFound {
        val response = template.getGuildTemplate(code)
        Template(response.toData(), kord)
    }

    override fun getTemplates(guildId: Snowflake): Flow<Template> = flow {
        for (template in template.getGuildTemplates(guildId)) {
            val data = template.toData()
            emit(Template(data, kord))
        }
    }


    public inline fun getAuditLogEntries(
        guildId: Snowflake,
        builder: AuditLogGetRequestBuilder.() -> Unit
    ): Flow<DiscordAuditLogEntry> {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return getAuditLogEntries(guildId, AuditLogGetRequestBuilder().apply(builder).toRequest())
    }

    public suspend fun getGuildWelcomeScreenOrNull(guildId: Snowflake): WelcomeScreen? = catchNotFound {
        val response = guild.getGuildWelcomeScreen(guildId)
        WelcomeScreen(WelcomeScreenData.from(response), kord)
    }

    public suspend fun getGuildWelcomeScreen(guildId: Snowflake): WelcomeScreen =
        getGuildWelcomeScreenOrNull(guildId) ?: EntityNotFoundException.welcomeScreenNotFound(guildId)


    // maxBatchSize: see https://discord.com/developers/docs/resources/audit-log#get-guild-audit-log
    public fun getAuditLogEntries(
        guildId: Snowflake,
        request: AuditLogGetRequest = AuditLogGetRequest(),
    ): Flow<DiscordAuditLogEntry> = limitedPagination(request.limit, maxBatchSize = 100) { batchSize ->
        paginateBackwards(batchSize, idSelector = { it.id }) { beforePosition ->
            val r = request.copy(before = beforePosition.value, limit = batchSize)
            auditLog.getAuditLogs(guildId, request = r).auditLogEntries
        }
    }

    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? = catchNotFound {
        val instance = stageInstance.getStageInstance(channelId)
        val data = StageInstanceData.from(instance)

        StageInstance(data, kord, this)
    }

    override fun getThreadMembers(channelId: Snowflake): Flow<ThreadMember> = flow {
        channel.listThreadMembers(channelId).onEach {
            val data = ThreadMemberData.from(it)
            emit(ThreadMember(data, kord))
        }
    }

    override fun getActiveThreads(guildId: Snowflake): Flow<ThreadChannel> = flow {
        guild.listActiveThreads(guildId).threads.onEach {
            val data = ChannelData.from(it)
            val channel = Channel.from(data, kord)
            if (channel is ThreadChannel) emit(channel)
        }
    }

    // no maxBatchSize documented (but api errors say it's 100): see https://discord.com/developers/docs/resources/channel#list-public-archived-threads
    override fun getPublicArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> =
        limitedPagination(limit, maxBatchSize = 100) { batchSize ->
            paginateThreads(batchSize, start = before) { beforeTimestamp ->
                val request = ListThreadsByTimestampRequest(before = beforeTimestamp, limit = batchSize)
                channel.listPublicArchivedThreads(channelId, request).threads.mapNotNull {
                    val data = ChannelData.from(it)
                    Channel.from(data, kord) as? ThreadChannel
                }
            }
        }

    // no maxBatchSize documented (but api errors say it's 100): see https://discord.com/developers/docs/resources/channel#list-private-archived-threads
    override fun getPrivateArchivedThreads(channelId: Snowflake, before: Instant?, limit: Int?): Flow<ThreadChannel> =
        limitedPagination(limit, maxBatchSize = 100) { batchSize ->
            paginateThreads(batchSize, start = before) { beforeTimestamp ->
                val request = ListThreadsByTimestampRequest(before = beforeTimestamp, limit = batchSize)
                channel.listPrivateArchivedThreads(channelId, request).threads.mapNotNull {
                    val data = ChannelData.from(it)
                    Channel.from(data, kord) as? ThreadChannel
                }
            }
        }

    // no maxBatchSize documented (but api errors say it's 100): see https://discord.com/developers/docs/resources/channel#list-joined-private-archived-threads
    override fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Snowflake?,
        limit: Int?,
    ): Flow<ThreadChannel> = limitedPagination(limit, maxBatchSize = 100) { batchSize ->
        paginateBackwards(batchSize, start = before ?: Snowflake.max, idSelector = { it.id }) { beforePosition ->
            val request = ListThreadsBySnowflakeRequest(before = beforePosition.value, limit = batchSize)
            channel.listJoinedPrivateArchivedThreads(channelId, request).threads
        }
    }.mapNotNull {
        val data = ChannelData.from(it)
        Channel.from(data, kord) as? ThreadChannel
    }

    override fun getGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        withLocalizations: Boolean?
    ): Flow<GuildApplicationCommand> = flow {
        for (command in interaction.getGuildApplicationCommands(applicationId, guildId, withLocalizations)) {
            val data = ApplicationCommandData.from(command)
            emit(GuildApplicationCommand(data, interaction))
        }
    }


    override suspend fun getGlobalApplicationCommandOrNull(
        applicationId: Snowflake,
        commandId: Snowflake
    ): GlobalApplicationCommand? = catchNotFound {
        val response = interaction.getGlobalCommand(applicationId, commandId)
        val data = ApplicationCommandData.from(response)
        GlobalApplicationCommand(data, interaction)
    }

    override fun getGlobalApplicationCommands(applicationId: Snowflake, withLocalizations: Boolean?): Flow<GlobalApplicationCommand> = flow {
        for (command in interaction.getGlobalApplicationCommands(applicationId, withLocalizations)) {
            val data = ApplicationCommandData.from(command)
            emit(GlobalApplicationCommand(data, interaction))
        }
    }

    /**
     * Requests the initial interaction response, returns `null` if the initial interaction response isn't present.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun getOriginalInteractionOrNull(applicationId: Snowflake, token: String): Message? = catchNotFound {
        val response = interaction.getInteractionResponse(applicationId, token)
        val data = MessageData.from(response)
        Message(data, kord)
    }

    /**
     * Requests the initial interaction response.
     *
     * @throws RestRequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the initial interaction response is null.
     */
    public suspend fun getOriginalInteraction(applicationId: Snowflake, token: String): Message =
        getOriginalInteractionOrNull(applicationId, token) ?: EntityNotFoundException.interactionNotFound(token)

    override suspend fun getFollowupMessageOrNull(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): FollowupMessage? = catchNotFound {
        val response = interaction.getFollowupMessage(applicationId, interactionToken, messageId)
        val data = MessageData.from(response)
        val message = Message(data, kord)
        FollowupMessage(message, applicationId, interactionToken, kord)
    }

    override fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
    ): Flow<ApplicationCommandPermissions> = flow {
        interaction.getGuildApplicationCommandPermissions(applicationId, guildId)
            .forEach {
                val data = GuildApplicationCommandPermissionsData.from(it)
                emit(ApplicationCommandPermissions(data))
            }
    }

    override fun getGuildScheduledEvents(guildId: Snowflake): Flow<GuildScheduledEvent> = flow {
        guild.listScheduledEvents(guildId).forEach {
            val data = GuildScheduledEventData.from(it)

            emit(GuildScheduledEvent(data, kord))
        }
    }

    override suspend fun getGuildScheduledEventOrNull(guildId: Snowflake, eventId: Snowflake): GuildScheduledEvent? =
        catchNotFound {
            val event = guild.getScheduledEvent(guildId, eventId)
            val data = GuildScheduledEventData.from(event)

            GuildScheduledEvent(data, kord)
        }

    override fun getGuildScheduledEventUsersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int?,
    ): Flow<User> = getGuildScheduledEventUsersBefore(guildId, eventId, before, withMember = false, limit).map {
        val data = UserData.from(it.user)
        User(data, kord)
    }

    override fun getGuildScheduledEventUsersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int?,
    ): Flow<User> = getGuildScheduledEventUsersAfter(guildId, eventId, after, withMember = false, limit).map {
        val data = UserData.from(it.user)
        User(data, kord)
    }

    override fun getGuildScheduledEventMembersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        limit: Int?,
    ): Flow<Member> = getGuildScheduledEventUsersBefore(guildId, eventId, before, withMember = true, limit).map {
        val userData = UserData.from(it.user)
        val memberData = it.member.value!!.toData(userData.id, guildId)
        Member(memberData, userData, kord)
    }

    override fun getGuildScheduledEventMembersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        limit: Int?,
    ): Flow<Member> = getGuildScheduledEventUsersAfter(guildId, eventId, after, withMember = true, limit).map {
        val userData = UserData.from(it.user)
        val memberData = it.member.value!!.toData(userData.id, guildId)
        Member(memberData, userData, kord)
    }

    // maxBatchSize: see https://discord.com/developers/docs/resources/guild-scheduled-event#get-guild-scheduled-event-users
    private fun getGuildScheduledEventUsersBefore(
        guildId: Snowflake,
        eventId: Snowflake,
        before: Snowflake,
        withMember: Boolean,
        limit: Int?,
    ): Flow<GuildScheduledEventUsersResponse> = limitedPagination(limit, maxBatchSize = 100) { batchSize ->
        paginateBackwards(batchSize, start = before, idSelector = { it.user.id }) { beforePosition ->
            guild.getScheduledEventUsers(guildId, eventId, beforePosition, withMember, limit = batchSize)
        }
    }

    // maxBatchSize: see https://discord.com/developers/docs/resources/guild-scheduled-event#get-guild-scheduled-event-users
    private fun getGuildScheduledEventUsersAfter(
        guildId: Snowflake,
        eventId: Snowflake,
        after: Snowflake,
        withMember: Boolean,
        limit: Int?,
    ): Flow<GuildScheduledEventUsersResponse> = limitedPagination(limit, maxBatchSize = 100) { batchSize ->
        paginateForwards(batchSize, start = after, idSelector = { it.user.id }) { afterPosition ->
            guild.getScheduledEventUsers(guildId, eventId, afterPosition, withMember, limit = batchSize)
        }
    }

    override suspend fun getStickerOrNull(id: Snowflake): Sticker? = catchNotFound {
        val response = sticker.getSticker(id)
        val data = StickerData.from(response)
        Sticker(data, kord)
    }

    override suspend fun getGuildStickerOrNull(guildId: Snowflake, id: Snowflake): GuildSticker? = catchNotFound {
        val response = sticker.getGuildSticker(guildId, id)
        val data = StickerData.from(response)
        GuildSticker(data, kord)
    }

    override fun getNitroStickerPacks(): Flow<StickerPack> = flow {
        val responses = sticker.getNitroStickerPacks()

        responses.forEach { response ->
            val data = StickerPackData.from(response)
            emit(StickerPack(data, kord))
        }
    }

    override fun getGuildStickers(guildId: Snowflake): Flow<GuildSticker> = flow {
        val responses = sticker.getGuildStickers(guildId)

        responses.forEach { response ->
            val data = StickerData.from(response)
            emit(GuildSticker(data, kord))
        }
    }

    override suspend fun getApplicationCommandPermissionsOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): ApplicationCommandPermissions {
        val permissions = interaction.getApplicationCommandPermissions(applicationId, guildId, commandId)
        val data = GuildApplicationCommandPermissionsData.from(permissions)

        return ApplicationCommandPermissions(data)
    }


    override suspend fun getGuildApplicationCommandOrNull(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake
    ): GuildApplicationCommand? = catchNotFound {
        val response = interaction.getGuildCommand(applicationId, guildId, commandId)
        val data = ApplicationCommandData.from(response)
        GuildApplicationCommand(data, interaction)
    }


    override fun toString(): String = "RestEntitySupplier(rest=${kord.rest})"
}


private fun checkLimitAndGetBatchSize(limit: Int?, maxBatchSize: Int): Int {
    require(limit == null || limit > 0) { "At least 1 item should be requested, but got $limit." }
    return if (limit == null) maxBatchSize else min(limit, maxBatchSize)
}

private fun <T> Flow<T>.limitPagination(limit: Int?): Flow<T> = if (limit == null) this else take(limit)

private inline fun <T> limitedPagination(
    limit: Int?,
    maxBatchSize: Int,
    paginationCreator: (batchSize: Int) -> Flow<T>,
): Flow<T> {
    val batchSize = checkLimitAndGetBatchSize(limit, maxBatchSize)
    val flow = paginationCreator(batchSize)
    return flow.limitPagination(limit)
}
