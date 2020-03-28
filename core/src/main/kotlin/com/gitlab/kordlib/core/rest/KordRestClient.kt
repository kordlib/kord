package com.gitlab.kordlib.core.rest

import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.*
import com.gitlab.kordlib.core.cache.data.ApplicationInfoData
import com.gitlab.kordlib.core.cache.data.GuildData
import com.gitlab.kordlib.core.cache.data.RegionData
import com.gitlab.kordlib.core.cache.data.toData
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.rest.request.RequestException
import com.gitlab.kordlib.rest.service.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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
        }.map { RegionData.from(it) }.map { Region(it, kord) }

    /**
     * Requests to get the channel with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the channel with the given [id], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getChannel(id: Snowflake): Channel? = catchNotFound { Channel.from(channel.getChannel(id.value).toData(), kord) }

    /**
     * Requests to get the guild with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the guild with the given [id], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getGuild(id: Snowflake): Guild? = catchNotFound { Guild(guild.getGuild(id.value).toData(), kord) }

    /**
     * Requests to get the member with the given [userId] in the [guildId].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the member with the given [userId], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member? = catchNotFound {
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
    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message? = catchNotFound {
        Message(channel.getMessage(channelId = channelId.value, messageId = messageId.value).toData(), kord)
    }

    /**
     * Requests to get the user linked to the current [ClientResources.token].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     * @throws RequestException when the request failed.
     */
    override suspend fun getSelf(): User = User(user.getCurrentUser().toData(), kord)

    /**
     * Requests to get the user with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the user with the given [id], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getUser(id: Snowflake): User? = catchNotFound { User(user.getUser(id.value).toData(), kord) }

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