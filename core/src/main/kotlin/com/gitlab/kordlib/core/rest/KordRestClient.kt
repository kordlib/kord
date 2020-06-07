package com.gitlab.kordlib.core.rest

import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.*
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.service.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlin.math.min

class KordRestClient(val kord: Kord, val client: RestClient) : EntitySupplier {

    val auditLog: AuditLogService get() = client.auditLog
    val channel: ChannelService get() = client.channel
    val emoji: EmojiService get() = client.emoji
    val guild: GuildService get() = client.guild
    val invite: InviteService get() = client.invite
    val user: UserService get() = client.user
    val voice: VoiceService get() = client.voice
    val webhook: WebhookService get() = client.webhook
    val application: ApplicationService get() = client.application

    /**
     * Requests to get the guilds available to the current application.
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     */
    override val guilds: Flow<Guild>
        get() = paginateForwards(idSelector = DiscordPartialGuild::id, batchSize = 100) { position -> user.getCurrentUserGuilds(position, 100) }
                .map { guild.getGuild(it.id) }
                .map { GuildData.from(it) }
                .map { Guild(it, kord) }

    /**
     * Requests to get the regions available to the current application.
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     */
    override val regions: Flow<Region>
        get() = flow {
            client.voice.getVoiceRegions().forEach { emit(it) }
        }.map { RegionData.from(null, it) }.map { Region(it, kord) }

    /**
     * Requests to get the channel with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the channel with the given [id], or null if the request returns a 404.
     * @throws RestRequestException when the request failed.
     */
    override suspend fun getChannelOrNull(id: Snowflake): Channel? = catchNotFound { Channel.from(channel.getChannel(id.value).toData(), kord) }

    override fun getGuildChannels(guildId: Snowflake): Flow<GuildChannel> = flow {
        for (channelData in guild.getGuildChannels(guildId.value))
            emit(Channel.from(ChannelData.from(channelData), kord) as GuildChannel)
    }


    override fun getChannelPins(channelId: Snowflake): Flow<Message> = flow {
        for (messageData in channel.getChannelPins(channelId.value))
            emit(Message(MessageData.from(messageData), kord))
    }


    /**
     * Requests to get the guild with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the guild with the given [id], or null if the request returns a 404.
     * @throws RestRequestException when the request failed.
     */
    override suspend fun getGuildOrNull(id: Snowflake): Guild? = catchNotFound { Guild(guild.getGuild(id.value).toData(), kord) }

    /**
     * Requests to get the member with the given [userId] in the [guildId].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the member with the given [userId], or null if the request returns a 404.
     * @throws RestRequestException when the request failed.
     */
    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? = catchNotFound {
        val memberData = guild.getGuildMember(guildId = guildId.value, userId = userId.value).toData(guildId = guildId.value, userId = userId.value)
        val userData = user.getUser(userId.value).toData()
        return Member(memberData, userData, kord)
    }

    /**
     * Requests to get the message with the given [messageId] in the [channelId].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the message with the given [messageId], or null if the request returns a 404.
     * @throws RestRequestException when the request failed.
     */
    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? = catchNotFound {
        Message(channel.getMessage(channelId = channelId.value, messageId = messageId.value).toData(), kord)
    }


    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateForwards(messageId, batchSize, idSelector = { it.id }) { position ->
            kord.rest.channel.getMessages(channelId.value, position, batchSize)
        }.map { MessageData.from(it) }.map { Message(it, kord) }

        return if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    }


    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateBackwards(messageId, batchSize, idSelector = { it.id }) { position ->
            kord.rest.channel.getMessages(channelId.value, position, batchSize)
        }.map { MessageData.from(it) }.map { Message(it, kord) }

        return if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    }

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int) = flow {
        val responses = kord.rest.channel.getMessages(channelId.value, Position.Around(messageId.value))

        for (response in responses) {
            val data = MessageData.from(response)
            emit(Message(data, kord))
        }


    }

    /**
     * Requests to get the user linked to the current [ClientResources.token].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     * @throws RestRequestException when the request failed.
     */
    override suspend fun getSelfOrNull(): User? = catchNotFound {
        User(user.getCurrentUser().toData(), kord)
    }

    /**
     * Requests to get the user with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the user with the given [id], or null if the request returns a 404.
     * @throws RestRequestException when the request failed.
     */
    override suspend fun getUserOrNull(id: Snowflake): User? = catchNotFound { User(user.getUser(id.value).toData(), kord) }


    /**
     * Requests to get the role with the given [roleId] in the given [guildId].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * Note that this will effectively request all roles at once and then filter on the given id
     *
     * @return the role with the given [roleId], or null if the request returns a 404.
     * @throws RestRequestException when the request failed.
     */
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


    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> = flow {
        for (memberData in guild.getGuildMembers(guildId.value))
            emit(Member(memberData.toData(memberData.user!!.id, guildId.value), memberData.user!!.toData(), kord))
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
                        emoji = emoji.formatted,
                        limit = 100,
                        position = position
                )
            }.map { UserData.from(it) }.map { User(it, kord) }

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
}
