package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import kotlinx.coroutines.flow.Flow

fun EntitySupplier.withFallback(other: EntitySupplier): EntitySupplier = FallbackEntitySupplier(this, other)

private class FallbackEntitySupplier(val first: EntitySupplier, val second: EntitySupplier) : EntitySupplier {

    override val guilds: Flow<Guild>
        get() = first.guilds.switchIfEmpty(second.guilds)

    override val regions: Flow<Region>
        get() = first.regions.switchIfEmpty(second.regions)

    override suspend fun getChannel(id: Snowflake): Channel? = first.getChannel(id) ?: second.getChannel(id)

    override fun getGuildChannels(guildId: Snowflake): Flow<GuildChannel> =
            first.getGuildChannels(guildId).switchIfEmpty(second.getGuildChannels(guildId))

    override fun getChannelPins(channelId: Snowflake): Flow<Message> =
            first.getChannelPins(channelId).switchIfEmpty(second.getChannelPins(channelId))

    override suspend fun getGuild(id: Snowflake): Guild? = first.getGuild(id) ?: second.getGuild(id)

    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member? =
            first.getMember(guildId = guildId, userId = userId) ?: second.getMember(guildId = guildId, userId = userId)

    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message? =
            first.getMessage(channelId = channelId, messageId = messageId)
                    ?: second.getMessage(channelId = channelId, messageId = messageId)

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> =
            first.getMessagesAfter(messageId, channelId, limit).switchIfEmpty(second.getMessagesAfter(messageId, channelId, limit))

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> =
            first.getMessagesBefore(messageId, channelId, limit).switchIfEmpty(second.getMessagesBefore(messageId, channelId, limit))

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> =
            first.getMessagesAround(messageId, channelId, limit).switchIfEmpty(second.getMessagesAround(messageId, channelId, limit))

    override suspend fun getSelf(): User? = first.getSelf() ?: second.getSelf()

    override suspend fun getUser(id: Snowflake): User? = first.getUser(id) ?: second.getUser(id)

    override suspend fun getCurrentUser(): User? =
            first.getCurrentUser() ?: second.getCurrentUser()

    override suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role? =
            first.getRole(guildId, roleId) ?: second.getRole(guildId, roleId)

    override fun getGuildRoles(guildId: Snowflake): Flow<Role> =
            first.getGuildRoles(guildId).switchIfEmpty(second.getGuildRoles(guildId))

    override suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): Ban? =
            first.getGuildBan(guildId, userId) ?: second.getGuildBan(guildId, userId)

    override fun getGuildBans(guildId: Snowflake): Flow<Ban> =
            first.getGuildBans(guildId).switchIfEmpty(second.getGuildBans(guildId))

    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> =
            first.getGuildMembers(guildId, limit).switchIfEmpty(second.getGuildMembers(guildId, limit))

    override  fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> =
            first.getGuildVoiceRegions(guildId).switchIfEmpty(second.getGuildVoiceRegions(guildId))

    override suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? =
            first.getEmoji(guildId, emojiId) ?: second.getEmoji(guildId, emojiId)

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> =
            first.getEmojis(guildId).switchIfEmpty(second.getEmojis(guildId))

    override fun getCurrentUserGuilds(limit: Int): Flow<Guild> =
            first.getCurrentUserGuilds(limit).switchIfEmpty(second.getCurrentUserGuilds(limit))

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> =
            first.getChannelWebhooks(channelId).switchIfEmpty(second.getChannelWebhooks(channelId))

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> =
            first.getGuildWebhooks(guildId).switchIfEmpty(second.getGuildWebhooks(guildId))

    override suspend fun getWebhook(webhookId: Snowflake): Webhook? =
            first.getWebhook(webhookId) ?: second.getWebhook(webhookId)

    override suspend fun getWebhookWithToken(webhookId: Snowflake, token: String): Webhook? =
            first.getWebhookWithToken(webhookId, token) ?: second.getWebhookWithToken(webhookId, token)

}

