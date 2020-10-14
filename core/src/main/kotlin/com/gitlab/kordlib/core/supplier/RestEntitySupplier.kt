package com.gitlab.kordlib.core.supplier

import com.gitlab.kordlib.common.entity.DiscordGuildMember
import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.catchNotFound
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.paginateBackwards
import com.gitlab.kordlib.core.paginateForwards
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.service.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
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
class RestEntitySupplier(val kord: Kord) : EntitySupplier {

    private val auditLog: AuditLogService get() = kord.rest.auditLog
    private val channel: ChannelService get() = kord.rest.channel
    private val emoji: EmojiService get() = kord.rest.emoji
    private val guild: GuildService get() = kord.rest.guild
    private val invite: InviteService get() = kord.rest.invite
    private val user: UserService get() = kord.rest.user
    private val voice: VoiceService get() = kord.rest.voice
    private val webhook: WebhookService get() = kord.rest.webhook
    private val application: ApplicationService get() = kord.rest.application

    override val guilds: Flow<Guild>
        get() = paginateForwards(idSelector = DiscordPartialGuild::id, batchSize = 100) { position -> user.getCurrentUserGuilds(position, 100) }
                .map {
                    val guild = guild.getGuild(it.id)
                    val data = GuildData.from(guild)
                    Guild(data, kord)
                }

    override val regions: Flow<Region>
        get() = flow {
            voice.getVoiceRegions().forEach {
                val data = RegionData.from(null, it)
                emit(Region(data, kord))
            }
        }

    override suspend fun getChannelOrNull(id: Snowflake): Channel? = catchNotFound { Channel.from(channel.getChannel(id.value).toData(), kord) }

    override fun getGuildChannels(guildId: Snowflake): Flow<GuildChannel> = flow {
        for (channelData in guild.getGuildChannels(guildId.value))
            emit(Channel.from(ChannelData.from(channelData), kord) as GuildChannel)
    }

    override fun getChannelPins(channelId: Snowflake): Flow<Message> = flow {
        for (messageData in channel.getChannelPins(channelId.value))
            emit(Message(MessageData.from(messageData), kord))
    }

    override suspend fun getGuildOrNull(id: Snowflake): Guild? = catchNotFound { Guild(guild.getGuild(id.value).toData(), kord) }

    /**
     * Returns the preview of the guild matching the [id]. The bot does not need to present in this guild
     * for this to complete successfully.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the preview was not found.
     */
    suspend fun getGuildPreview(id: Snowflake): GuildPreview = getGuildPreviewOrNull(id)
            ?: EntityNotFoundException.entityNotFound("Guild preview", id)

    /**
     * Returns the preview of the guild matching the [id]. The bot does not need to present in this guild
     * for this to complete successfully. Returns null if the preview was not found.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun getGuildPreviewOrNull(id: Snowflake): GuildPreview? = catchNotFound {
        val discordPreview = guild.getGuildPreview(id.value)
        return GuildPreview(GuildPreviewData.from(discordPreview), kord)
    }

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? = catchNotFound {
        val member = guild.getGuildMember(guildId = guildId.value, userId = userId.value)
        val memberData = member.toData(guildId = guildId.value, userId = userId.value)
        val userData = member.user!!.toData()
        return Member(memberData, userData, kord)
    }

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? = catchNotFound {
        Message(channel.getMessage(channelId = channelId.value, messageId = messageId.value).toData(), kord)
    }

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateForwards(messageId, batchSize, idSelector = { it.id }) { position ->
            kord.rest.channel.getMessages(channelId.value, position, batchSize)
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
            kord.rest.channel.getMessages(channelId.value, position, batchSize)
        }.map {
            val data = MessageData.from(it)
            Message(data, kord)
        }

        return if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    }

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int) = flow {
        val responses = kord.rest.channel.getMessages(channelId.value, Position.Around(messageId.value))

        for (response in responses) {
            val data = MessageData.from(response)
            emit(Message(data, kord))
        }
    }

    override suspend fun getSelfOrNull(): User? = catchNotFound {
        User(user.getCurrentUser().toData(), kord)
    }

    override suspend fun getUserOrNull(id: Snowflake): User? = catchNotFound { User(user.getUser(id.value).toData(), kord) }

    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? = catchNotFound {
        val response = guild.getGuildRoles(guildId.value)
                .firstOrNull { it.id == roleId.value } ?: return@catchNotFound null

        return Role(RoleData.from(guildId.value, response), kord)
    }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake) = catchNotFound {
        val response = guild.getGuildBan(guildId.value, userId.value)
        val data = BanData.from(guildId.value, response)
        Ban(data, kord)

    }

    override fun getGuildRoles(guildId: Snowflake): Flow<Role> = flow {
        for (roleData in guild.getGuildRoles(guildId.value))
            emit(Role(RoleData.from(guildId.value, roleData), kord))
    }

    override fun getGuildBans(guildId: Snowflake): Flow<Ban> = flow {
        for (banData in guild.getGuildBans(guildId.value))
            emit(Ban(BanData.from(guildId.value, banData), kord))
    }

    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(1000, limit)

        val flow = paginateForwards(idSelector = { it.user!!.id }, batchSize = batchSize) { position ->
            kord.rest.guild.getGuildMembers(guildId = guildId.value, position = position, limit = batchSize)
        }.map {
            val userData = it.user!!.toData()
            val memberData = it.toData(guildId = guildId.value, userId = it.user!!.id)
            Member(memberData, userData, kord)
        }


        return if (limit != Int.MAX_VALUE) flow.take(limit)
        else flow
    }


    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> = flow {
        for (region in guild.getGuildVoiceRegions(guildId.value)) {
            val data = RegionData.from(guildId.value, region)
            emit(Region(data, kord))
        }
    }

    fun getReactors(channelId: Snowflake, messageId: Snowflake, emoji: ReactionEmoji): Flow<User> =
            paginateForwards(batchSize = 100, idSelector = { it.id }) { position ->
                kord.rest.channel.getReactions(
                        channelId = channelId.value,
                        messageId = messageId.value,
                        emoji = emoji.urlFormat,
                        limit = 100,
                        position = position
                )
            }.map {
                val data = UserData.from(it)
                User(data, kord)
            }

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake) = catchNotFound {
        val data = EmojiData.from(guildId.value, emojiId.value, emoji.getEmoji(guildId.value, emojiId.value))
        GuildEmoji(data, kord)
    }

    override fun getEmojis(guildId: Snowflake) = flow {
        for (emoji in emoji.getEmojis(guildId.value)) {
            val data = EmojiData.from(guildId = guildId.value, id = emoji.id!!, entity = emoji)
            emit(GuildEmoji(data, kord))
        }
    }

    override fun getCurrentUserGuilds(limit: Int): Flow<Guild> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateForwards(batchSize = batchSize, idSelector = { it.id.value }) { position ->
            user.getCurrentUserGuilds(position, batchSize).map { Guild(guild.getGuild(it.id).toData(), kord) }
        }

        return if (limit != Int.MAX_VALUE) flow.take(limit)
        else flow
    }

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> = flow {
        for (webhook in webhook.getChannelWebhooks(channelId.value)) {
            val data = WebhookData.from(webhook)
            emit(Webhook(data, kord))
        }
    }

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> = flow {
        for (webhook in webhook.getGuildWebhooks(guildId.value)) {
            val data = WebhookData.from(webhook)
            emit(Webhook(data, kord))
        }
    }

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? = catchNotFound {
        val data = WebhookData.from(webhook.getWebhook(id.value))
        return Webhook(data, kord)
    }

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? = catchNotFound {
        val data = WebhookData.from(webhook.getWebhookWithToken(id.value, token))
        return Webhook(data, kord)
    }

    suspend fun getInviteOrNull(code: String, withCounts: Boolean): Invite? = catchNotFound {
        val response = invite.getInvite(code, withCounts)
        return Invite(InviteData.from(response), kord)
    }

    suspend fun getInvite(code: String, withCounts: Boolean = true): Invite =
            getInviteOrNull(code, withCounts) ?: EntityNotFoundException.inviteNotFound(code)

    /**
     * Requests to get the information of the current application.
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     * @throws RestRequestException when the request failed.
     */
    suspend fun getApplicationInfo(): ApplicationInfo {
        val response = application.getCurrentApplicationInfo()
        return ApplicationInfo(ApplicationInfoData.from(response), kord)
    }


    override fun toString(): String {
        return "RestEntitySupplier(kord=$kord, rest=$kord.rest)"
    }

}
