package dev.kord.core.supplier

import dev.kord.common.entity.DiscordAuditLogEntry
import dev.kord.common.entity.DiscordPartialGuild
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
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.rest.builder.auditlog.AuditLogGetRequestBuilder
import dev.kord.rest.json.request.AuditLogGetRequest
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
 * to Discord, entities will always be up to date at the moment of the call.
 */
public class RestEntitySupplier(public val kord: Kord) : EntitySupplier {

    private val auditLog: AuditLogService get() = kord.rest.auditLog
    private val channel: ChannelService get() = kord.rest.channel
    private val emoji: EmojiService get() = kord.rest.emoji
    private val guild: GuildService get() = kord.rest.guild
    private val invite: InviteService get() = kord.rest.invite
    private val user: UserService get() = kord.rest.user
    private val voice: VoiceService get() = kord.rest.voice
    private val webhook: WebhookService get() = kord.rest.webhook
    private val application: ApplicationService get() = kord.rest.application
    private val template: TemplateService = kord.rest.template
    private val interaction: InteractionService = kord.rest.interaction

    override val guilds: Flow<Guild>
        get() = paginateForwards(
            idSelector = DiscordPartialGuild::id,
            batchSize = 100
        ) { position -> user.getCurrentUserGuilds(position, 100) }
            .map {
                val guild = guild.getGuild(it.id)
                val data = GuildData.from(guild)
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

    /**
     * Returns the preview of the guild matching the [guildId]. The bot does not need to present in this guild
     * for this to complete successfully.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the preview was not found.
     */
    override suspend fun getGuildPreview(guildId: Snowflake): GuildPreview =
        getGuildPreviewOrNull(guildId) ?: EntityNotFoundException.entityNotFound("Guild preview", guildId)

    /**
     * Returns the preview of the guild matching the [guildId]. The bot does not need to present in this guild
     * for this to complete successfully. Returns null if the preview was not found.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
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

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateForwards(messageId, batchSize, idSelector = { it.id }) { position ->
            kord.rest.channel.getMessages(channelId, position, batchSize)
        }.map {
            val data = MessageData.from(it)
            Message(data, kord)
        }

        return if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    }

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateBackwards(messageId, batchSize, idSelector = { it.id }) { position ->
            kord.rest.channel.getMessages(channelId, position, batchSize)
        }.map {
            val data = MessageData.from(it)
            Message(data, kord)
        }

        return if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    }

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> = flow {
        val responses = kord.rest.channel.getMessages(channelId, Position.Around(messageId))

        for (response in responses) {
            val data = MessageData.from(response)
            emit(Message(data, kord))
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

    override fun getGuildBans(guildId: Snowflake): Flow<Ban> = flow {
        for (banData in guild.getGuildBans(guildId))
            emit(Ban(BanData.from(guildId, banData), kord))
    }

    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(1000, limit)

        val flow = paginateForwards(idSelector = { it.user.value!!.id }, batchSize = batchSize) { position ->
            kord.rest.guild.getGuildMembers(guildId = guildId, position = position, limit = batchSize)
        }.map {
            val userData = it.user.value!!.toData()
            val memberData = it.toData(guildId = guildId, userId = it.user.value!!.id)
            Member(memberData, userData, kord)
        }


        return if (limit != Int.MAX_VALUE) flow.take(limit)
        else flow
    }


    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> = flow {
        for (region in guild.getGuildVoiceRegions(guildId)) {
            val data = RegionData.from(guildId.optionalSnowflake(), region)
            emit(Region(data, kord))
        }
    }

    public fun getReactors(channelId: Snowflake, messageId: Snowflake, emoji: ReactionEmoji): Flow<User> =
        paginateForwards(batchSize = 100, idSelector = { it.id }) { position ->
            kord.rest.channel.getReactions(
                channelId = channelId,
                messageId = messageId,
                emoji = emoji.urlFormat,
                limit = 100,
                position = position
            )
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

    override fun getCurrentUserGuilds(limit: Int): Flow<Guild> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateForwards(batchSize = batchSize, idSelector = { it.id }) { position ->
            user.getCurrentUserGuilds(position, batchSize).map { Guild(guild.getGuild(it.id).toData(), kord) }
        }

        return if (limit != Int.MAX_VALUE) flow.take(limit)
        else flow
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

    public suspend fun getInviteOrNull(code: String, withCounts: Boolean): Invite? = catchNotFound {
        val response = invite.getInvite(code, withCounts)
        Invite(InviteData.from(response), kord)
    }

    public suspend fun getInvite(code: String, withCounts: Boolean = true): Invite =
        getInviteOrNull(code, withCounts) ?: EntityNotFoundException.inviteNotFound(code)

    /**
     * Requests to get the information of the current application.
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     * @throws RestRequestException when the request failed.
     */
    public suspend fun getApplicationInfo(): ApplicationInfo {
        val response = application.getCurrentApplicationInfo()
        return ApplicationInfo(ApplicationInfoData.from(response), kord)
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


    public fun getAuditLogEntries(
        guildId: Snowflake,
        request: AuditLogGetRequest = AuditLogGetRequest()
    ): Flow<DiscordAuditLogEntry> = paginateBackwards(batchSize = 100, idSelector = DiscordAuditLogEntry::id) {
        auditLog.getAuditLogs(guildId, request.copy(before = it.value)).auditLogEntries
    }

    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? = catchNotFound {
        val instance = kord.rest.stageInstance.getStageInstance(channelId)
        val data = StageInstanceData.from(instance)

        StageInstance(data, kord, this)
    }

    override fun getThreadMembers(channelId: Snowflake): Flow<ThreadMember> = flow {
        kord.rest.channel.listThreadMembers(channelId).onEach {
            val data = ThreadMemberData.from(it)
            emit(ThreadMember(data, kord))
        }
    }

    override fun getActiveThreads(guildId: Snowflake): Flow<ThreadChannel> = flow {
        kord.rest.guild.listActiveThreads(guildId).threads.onEach {
            val data = ChannelData.from(it)
            val channel = Channel.from(data, kord)
            if (channel is ThreadChannel) emit(channel)
        }
    }

    override fun getPublicArchivedThreads(channelId: Snowflake, before: Instant, limit: Int): Flow<ThreadChannel> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }

        val batchSize = min(100, limit)

        val flow = paginateThreads(100, before) {
            kord.rest.channel.listPublicArchivedThreads(
                channelId,
                ListThreadsByTimestampRequest(before, batchSize)
            ).threads.mapNotNull {
                val data = ChannelData.from(it)
                Channel.from(data, kord) as? ThreadChannel
            }
        }
        return if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    }

    override fun getPrivateArchivedThreads(channelId: Snowflake, before: Instant, limit: Int): Flow<ThreadChannel> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }

        val batchSize = min(100, limit)

        val flow = paginateThreads(100, before) {
            kord.rest.channel.listPrivateArchivedThreads(
                channelId,
                ListThreadsByTimestampRequest(before, batchSize)
            ).threads.mapNotNull {
                val data = ChannelData.from(it)
                Channel.from(data, kord) as? ThreadChannel
            }
        }
        return if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    }

    override fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Snowflake,
        limit: Int
    ): Flow<ThreadChannel> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }

        val batchSize = min(100, limit)

        val flow = paginateBackwards(batchSize = batchSize, idSelector = { it.id }) {
            kord.rest.channel.listJoinedPrivateArchivedThreads(
                channelId,
                ListThreadsBySnowflakeRequest(before, batchSize)
            ).threads.mapNotNull {
                val data = ChannelData.from(it)
                Channel.from(data, kord) as? ThreadChannel
            }
        }
        return if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    }

    override fun getGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake
    ): Flow<GuildApplicationCommand> = flow {
        for (command in interaction.getGuildApplicationCommands(applicationId, guildId)) {
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

    override fun getGlobalApplicationCommands(applicationId: Snowflake): Flow<GlobalApplicationCommand> = flow {
        for (command in interaction.getGlobalApplicationCommands(applicationId)) {
            val data = ApplicationCommandData.from(command)
            emit(GlobalApplicationCommand(data, interaction))
        }
    }

    public suspend fun getOriginalInteractionOrNull(applicationId: Snowflake, token: String): Message? = catchNotFound {
        val response = interaction.getInteractionResponse(applicationId, token)
        val data = MessageData.from(response)
        Message(data, kord)
    }


    public suspend fun getOriginalInteraction(applicationId: Snowflake, token: String): Message {
        return getOriginalInteractionOrNull(applicationId, token) ?: EntityNotFoundException.interactionNotFound(token)
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
        kord.rest.guild.listScheduledEvents(guildId).forEach {
            val data = GuildScheduledEventData.from(it)

            emit(GuildScheduledEvent(data, kord))
        }
    }

    override suspend fun getGuildScheduledEventOrNull(guildId: Snowflake, eventId: Snowflake): GuildScheduledEvent? =
        catchNotFound {
            val event = kord.rest.guild.getScheduledEvent(guildId, eventId)
            val data = GuildScheduledEventData.from(event)

            GuildScheduledEvent(data, kord)
        }

    override suspend fun getStickerOrNull(id: Snowflake): Sticker? = catchNotFound {
        val response = kord.rest.sticker.getSticker(id)
        val data = StickerData.from(response)
        Sticker(data, kord)
    }

    override suspend fun getGuildStickerOrNull(guildId: Snowflake, id: Snowflake): GuildSticker? = catchNotFound {
        val response = kord.rest.sticker.getGuildSticker(guildId, id)
        val data = StickerData.from(response)
        GuildSticker(data, kord)
    }

    override fun getNitroStickerPacks(): Flow<StickerPack> = flow {
        val responses = kord.rest.sticker.getNitroStickerPacks()

        responses.forEach { response ->
            val data = StickerPackData.from(response)
            emit(StickerPack(data, kord))
        }
    }

    override fun getGuildStickers(guildId: Snowflake): Flow<GuildSticker> = flow {
        val responses = kord.rest.sticker.getGuildStickers(guildId)

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

    override fun toString(): String {
        return "RestEntitySupplier(rest=${kord.rest})"
    }

}
