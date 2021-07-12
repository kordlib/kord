package dev.kord.core.supplier

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadUser
import dev.kord.core.switchIfEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * Creates supplier providing a strategy which will first operate on this supplier. When an entity
 * is not present from the first supplier it will be fetched from [other] instead. Operations that return flows
 * will only fall back to [other] when the returned flow contained no elements.
 */
infix fun EntitySupplier.withFallback(other: EntitySupplier): EntitySupplier = FallbackEntitySupplier(this, other)

private class FallbackEntitySupplier(val first: EntitySupplier, val second: EntitySupplier) : EntitySupplier {

    override val guilds: Flow<Guild>
        get() = first.guilds.switchIfEmpty(second.guilds)

    override val regions: Flow<Region>
        get() = first.regions.switchIfEmpty(second.regions)

    override suspend fun getGuildOrNull(id: Snowflake): Guild? =
        first.getGuildOrNull(id) ?: second.getGuildOrNull(id)

    override suspend fun getChannelOrNull(id: Snowflake): Channel? =
        first.getChannelOrNull(id) ?: second.getChannelOrNull(id)

    override fun getGuildChannels(guildId: Snowflake): Flow<GuildChannel> =
        first.getGuildChannels(guildId).switchIfEmpty(second.getGuildChannels(guildId))

    override fun getChannelPins(channelId: Snowflake): Flow<Message> =
        first.getChannelPins(channelId).switchIfEmpty(second.getChannelPins(channelId))

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? =
        first.getMemberOrNull(guildId, userId) ?: second.getMemberOrNull(guildId, userId)

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? =
        first.getMessageOrNull(channelId, messageId) ?: second.getMessageOrNull(channelId, messageId)

    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member =
        getMemberOrNull(guildId, userId)!!

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> =
        first.getMessagesAfter(messageId, channelId, limit)
            .switchIfEmpty(second.getMessagesAfter(messageId, channelId, limit))

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> =
        first.getMessagesBefore(messageId, channelId, limit)
            .switchIfEmpty(second.getMessagesBefore(messageId, channelId, limit))

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> =
        first.getMessagesAround(messageId, channelId, limit)
            .switchIfEmpty(second.getMessagesAround(messageId, channelId, limit))

    override suspend fun getSelfOrNull(): User? =
        first.getSelfOrNull() ?: second.getSelfOrNull()

    override suspend fun getUserOrNull(id: Snowflake): User? =
        first.getUserOrNull(id) ?: second.getUserOrNull(id)

    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? =
        first.getRoleOrNull(guildId, roleId) ?: second.getRoleOrNull(guildId, roleId)


    override fun getGuildRoles(guildId: Snowflake): Flow<Role> =
        first.getGuildRoles(guildId).switchIfEmpty(second.getGuildRoles(guildId))

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? =
        first.getGuildBanOrNull(guildId, userId) ?: second.getGuildBanOrNull(guildId, userId)

    override fun getGuildBans(guildId: Snowflake): Flow<Ban> =
        first.getGuildBans(guildId).switchIfEmpty(second.getGuildBans(guildId))

    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> =
        first.getGuildMembers(guildId, limit).switchIfEmpty(second.getGuildMembers(guildId, limit))

    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> =
        first.getGuildVoiceRegions(guildId).switchIfEmpty(second.getGuildVoiceRegions(guildId))

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? =
        first.getEmojiOrNull(guildId, emojiId) ?: second.getEmojiOrNull(guildId, emojiId)

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> =
        first.getEmojis(guildId).switchIfEmpty(second.getEmojis(guildId))

    override fun getCurrentUserGuilds(limit: Int): Flow<Guild> =
        first.getCurrentUserGuilds(limit).switchIfEmpty(second.getCurrentUserGuilds(limit))

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> =
        first.getChannelWebhooks(channelId).switchIfEmpty(second.getChannelWebhooks(channelId))

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> =
        first.getGuildWebhooks(guildId).switchIfEmpty(second.getGuildWebhooks(guildId))

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? =
        first.getWebhookOrNull(id) ?: second.getWebhookOrNull(id)

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? =
        first.getWebhookWithTokenOrNull(id, token) ?: second.getWebhookWithTokenOrNull(id, token)

    override suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview? =
        first.getGuildPreviewOrNull(guildId) ?: second.getGuildPreviewOrNull(guildId)

    override suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget? =
        first.getGuildWidgetOrNull(guildId) ?: second.getGuildWidgetOrNull(guildId)

    override suspend fun getTemplateOrNull(code: String): Template? =
        first.getTemplateOrNull(code) ?: second.getTemplateOrNull(code)


    override fun getTemplates(guildId: Snowflake): Flow<Template> =
        first.getTemplates(guildId).switchIfEmpty(second.getTemplates(guildId))

    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? =
         first.getStageInstanceOrNull(channelId) ?: second.getStageInstanceOrNull(channelId)

    override fun getThreadMembers(channelId: Snowflake): Flow<ThreadUser> {
        return first.getThreadMembers(channelId).switchIfEmpty(second.getThreadMembers(channelId))
    }

    override fun getActiveThreads(channelId: Snowflake): Flow<ThreadChannel> {
        return first.getActiveThreads(channelId).switchIfEmpty(second.getActiveThreads(channelId))
    }

    override fun getPublicArchivedThreads(channelId: Snowflake, before: Instant, limit: Int): Flow<ThreadChannel> {
        return first.getPublicArchivedThreads(channelId, before, limit)
            .switchIfEmpty(second.getPublicArchivedThreads(channelId, before, limit))
    }

    override fun getPrivateArchivedThreads(channelId: Snowflake, before: Instant, limit: Int): Flow<ThreadChannel> {
        return first.getPrivateArchivedThreads(channelId, before, limit)
            .switchIfEmpty(second.getPrivateArchivedThreads(channelId, before, limit))
    }

    override fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Instant,
        limit: Int
    ): Flow<ThreadChannel> {
        return first.getJoinedPrivateArchivedThreads(channelId, before, limit)
            .switchIfEmpty(second.getJoinedPrivateArchivedThreads(channelId, before, limit))
    }


    override fun toString(): String {
        return "FallbackEntitySupplier(first=$first, second=$second)"
    }
}

