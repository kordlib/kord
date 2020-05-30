package com.gitlab.kordlib.core.rest

import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.*
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.rest.request.RequestException
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.service.*
import kotlinx.coroutines.flow.*
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
        }.map { RegionData.from(null,it) }.map { Region(it, kord) }

    /**
     * Requests to get the channel with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the channel with the given [id], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getChannelOrNull(id: Snowflake): Channel? = catchNotFound { Channel.from(channel.getChannel(id.value).toData(), kord) }
    override suspend fun getGuild(id: Snowflake): Guild = getGuildOrNull(id)!!

    override suspend fun getChannel(id: Snowflake): Channel = getChannelOrNull(id)!!

    override fun getGuildChannels(guildId: Snowflake): Flow<GuildChannel> = catchNotFound {
        flow {
            for (channelData in guild.getGuildChannels(guildId.value))
                emit(Channel.from(ChannelData.from(channelData), kord) as GuildChannel)
        }
    } ?: emptyFlow()

    override fun getChannelPins(channelId: Snowflake): Flow<Message> = catchNotFound {
        flow {
            for (messageData in channel.getChannelPins(channelId.value))
                emit(Message(MessageData.from(messageData), kord))
        }
    } ?: emptyFlow()


    /**
     * Requests to get the guild with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the guild with the given [id], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getGuildOrNull(id: Snowflake): Guild? = catchNotFound { Guild(guild.getGuild(id.value).toData(), kord) }

    /**
     * Requests to get the member with the given [userId] in the [guildId].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the member with the given [userId], or null if the request returns a 404.
     * @throws RequestException when the request failed.
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
     * @throws RequestException when the request failed.
     */
    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? = catchNotFound {
        Message(channel.getMessage(channelId = channelId.value, messageId = messageId.value).toData(), kord)
    }

    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member = getMemberOrNull(guildId, userId)!!

    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message = getMessageOrNull(channelId, messageId)!!
    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int) = catchNotFound {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateForwards(messageId, batchSize, idSelector = { it.id }) { position ->
            kord.rest.channel.getMessages(channelId.value, position, batchSize)
        }.map { MessageData.from(it) }.map { Message(it, kord) }

        if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    } ?: emptyFlow()


    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int) = catchNotFound {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateBackwards(messageId, batchSize, idSelector = { it.id }) { position ->
            kord.rest.channel.getMessages(channelId.value, position, batchSize)
        }.map { MessageData.from(it) }.map { Message(it, kord) }

        if (limit != Int.MAX_VALUE) flow.take(limit) else flow
    } ?: emptyFlow()

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int) = catchNotFound {
        flow {
            val responses = kord.rest.channel.getMessages(channelId.value, Position.Around(messageId.value))

            for (response in responses) {
                val data = MessageData.from(response)
                emit(Message(data, kord))
            }

        }
    } ?: emptyFlow()

    /**
     * Requests to get the user linked to the current [ClientResources.token].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     * @throws RequestException when the request failed.
     */
    override suspend fun getSelfOrNull(): User = User(user.getCurrentUser().toData(), kord)

    /**
     * Requests to get the user with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the user with the given [id], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getUserOrNull(id: Snowflake): User? = catchNotFound { User(user.getUser(id.value).toData(), kord) }
    override suspend fun getCurrentUserOrNull(): User? = catchNotFound {
        User(user.getCurrentUser().toData(), kord)
    }

    override suspend fun getSelf(): User = getSelfOrNull()!!

    override suspend fun getUser(id: Snowflake): User = getUserOrNull(id)!!

    override suspend fun getCurrentUser(): User  = getCurrentUserOrNull()!!

    /**
     * Requests to get the role with the given [roleId] in the given [guildId].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * Note that this will effectively request all roles at once and then filter on the given id
     *
     * @return the role with the given [roleId], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? = catchNotFound {
        val response = guild.getGuildRoles(guildId.value)
                .firstOrNull { it.id == roleId.value } ?: return@catchNotFound null

        return Role(RoleData.from(guildId.value, response), kord)
    }

    override suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role {
        TODO("Not yet implemented")
    }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake) = catchNotFound {
        val response = guild.getGuildBan(guildId.value, userId.value)
        val data = BanData.from(guildId.value, response)
        Ban(data, kord)

    }

    override suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): Ban {
        TODO("Not yet implemented")
    }

    override fun getGuildRoles(guildId: Snowflake): Flow<Role> = catchNotFound {
        flow {
            for (roleData in guild.getGuildRoles(guildId.value))
                emit(Role(RoleData.from(guildId.value, roleData), kord))

        }
    } ?: emptyFlow()

    override fun getGuildBans(guildId: Snowflake): Flow<Ban> = catchNotFound {
        flow {
            for (banData in guild.getGuildBans(guildId.value))
                emit(Ban(BanData.from(guildId.value,banData), kord))
        }
    } ?: emptyFlow()

    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> = catchNotFound {
        flow {
            for (memberData in guild.getGuildMembers(guildId.value))
                emit(Member(memberData.toData(memberData.user!!.id, guildId.value), memberData.user!!.toData(), kord))
        }
    } ?: emptyFlow()

    override  fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> = catchNotFound {
        flow {
            for (region in guild.getGuildVoiceRegions(guildId.value)) {
                val data = RegionData.from(guildId.value, region)
                emit(Region(data, kord))
            }
        }
    } ?: emptyFlow()

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

    override suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): GuildEmoji {
        TODO("Not yet implemented")
    }

    override fun getEmojis(guildId: Snowflake) = catchNotFound {
        flow {
            for (emoji in emoji.getEmojis(guildId.value)) {
                val data = EmojiData.from(guildId = guildId.value, id = emoji.id!!, entity = emoji)
                emit(GuildEmoji(data, kord))
            }
        }
    } ?: emptyFlow()

    override fun getCurrentUserGuilds(limit: Int): Flow<Guild> = catchNotFound {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        val batchSize = min(100, limit)

        val flow = paginateForwards(batchSize = batchSize, idSelector = { it.id.value }) { position ->
            user.getCurrentUserGuilds(position, batchSize).map { Guild(guild.getGuild(it.id).toData(), kord) }

        }

        return if (limit != Int.MAX_VALUE) flow.take(limit)
        else flow
    } ?: emptyFlow()

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> = catchNotFound {
        flow {
            for (webhook in webhook.getChannelWebhooks(channelId.value)) {
                val data = WebhookData.from(webhook)
                emit(Webhook(data, kord))
            }
        }
    } ?: emptyFlow()

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> = catchNotFound {
        flow {
            for (webhook in webhook.getGuildWebhooks(guildId.value)) {
                val data = WebhookData.from(webhook)
                emit(Webhook(data, kord))
            }
        }
    } ?: emptyFlow()


    override suspend fun getWebhookOrNull(webhookId: Snowflake): Webhook? = catchNotFound {
        val data = WebhookData.from(webhook.getWebhook(webhookId.value))
        return Webhook(data, kord)
    }

    override suspend fun getWebhookWithTokenOrNull(webhookId: Snowflake, token: String): Webhook? = catchNotFound {
        val data = WebhookData.from(webhook.getWebhookWithToken(webhookId.value, token))
        return Webhook(data, kord)
    }

    override suspend fun getWebhook(webhookId: Snowflake): Webhook {
        TODO("Not yet implemented")
    }

    override suspend fun getWebhookWithToken(webhookId: Snowflake, token: String): Webhook {
        TODO("Not yet implemented")
    }


    /**
     * Requests to get the information of the current application.
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     * @throws RequestException when the request failed.
     */
    suspend fun getApplicationInfo(): ApplicationInfo {
        val response = application.getCurrentApplicationInfo()
        return ApplicationInfo(ApplicationInfoData.from(response), kord)
    }

}
