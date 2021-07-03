package dev.kord.core.supplier

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.GuildChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * [EntitySupplier] that uses a [RestEntitySupplier] to resolve entities.
 *
 * Resolved entities will always be stored in [cache] if it wasn't null or empty for flows.
 */
class CacheAwareRestEntitySupplier(
    val restSupplier: RestEntitySupplier,
    val cache: DataCache,
    val kord: Kord
) : EntitySupplier {


    override val guilds: Flow<Guild>
        get() = storeAndEmit(restSupplier.guilds) { it.data }

    override val regions: Flow<Region>
        get() = storeAndEmit(restSupplier.regions) { it.data }


    override suspend fun getGuildOrNull(id: Snowflake): Guild? {
        return storeAndReturn(restSupplier.getGuildOrNull(id)) { it.data }
    }

    override suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview? {
        return storeAndReturn(restSupplier.getGuildPreviewOrNull(guildId)) { it.data }
    }

    override suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget? {
        return storeAndReturn(restSupplier.getGuildWidgetOrNull(guildId)) { it.data }
    }

    override suspend fun getChannelOrNull(id: Snowflake): Channel? {
        return storeAndReturn(restSupplier.getChannelOrNull(id)) { it.data }
    }

    override fun getGuildChannels(guildId: Snowflake): Flow<GuildChannel> {
        return storeAndEmit(restSupplier.getGuildChannels(guildId)) { it.data }

    }

    override fun getChannelPins(channelId: Snowflake): Flow<Message> {
        return storeAndEmit(restSupplier.getChannelPins(channelId)) { it.data }

    }

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? {
        return storeAndReturn(restSupplier.getMemberOrNull(guildId, userId)) { it.data }
    }

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? {
        return storeAndReturn(restSupplier.getMessageOrNull(channelId, messageId)) { it.data }
    }

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        return storeAndEmit(restSupplier.getMessagesAfter(messageId, channelId, limit)) { it.data }
    }

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        return storeAndEmit(restSupplier.getMessagesBefore(messageId, channelId, limit)) { it.data }
    }

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        return storeAndEmit(restSupplier.getMessagesAround(messageId, channelId, limit)) { it.data }
    }

    override suspend fun getSelfOrNull(): User? {
        return storeAndReturn(restSupplier.getSelfOrNull()) { it.data }
    }

    override suspend fun getUserOrNull(id: Snowflake): User? {
        return storeAndReturn(restSupplier.getUserOrNull(id)) { it.data }
    }

    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? {
        return storeAndReturn(restSupplier.getRoleOrNull(guildId, roleId)) { it.data }
    }

    override fun getGuildRoles(guildId: Snowflake): Flow<Role> {
        return storeAndEmit(restSupplier.getGuildRoles(guildId)) { it.data }
    }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? {
        return storeAndReturn(getGuildBanOrNull(guildId, userId)) { it.data }
    }

    override fun getGuildBans(guildId: Snowflake): Flow<Ban> {
        return storeAndEmit(restSupplier.getGuildBans(guildId)) { it.data }
    }

    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> {
        return storeAndEmit(restSupplier.getGuildMembers(guildId, limit)) { it.data }
    }

    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> {
        return storeAndEmit(restSupplier.getGuildVoiceRegions(guildId)) { it.data }
    }

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? {
        return storeAndReturn(restSupplier.getEmojiOrNull(guildId, emojiId)) { it.data }
    }

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> {
        return storeAndEmit(restSupplier.getEmojis(guildId)) { it.data }

    }

    override fun getCurrentUserGuilds(limit: Int): Flow<Guild> {
        return storeAndEmit(restSupplier.getCurrentUserGuilds(limit)) { it.data }

    }

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> {
        return storeAndEmit(restSupplier.getChannelWebhooks(channelId)) { it.data }
    }

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> {
        return storeAndEmit(restSupplier.getGuildWebhooks(guildId)) { it.data }
    }

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? {
        return storeAndReturn(restSupplier.getWebhookOrNull(id)) { it.data }
    }

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? {
        return storeAndReturn(restSupplier.getWebhookWithTokenOrNull(id, token)) { it.data }
    }

    override suspend fun getTemplateOrNull(code: String): Template? {
        return storeAndReturn(restSupplier.getTemplateOrNull(code)) { it.data }
    }

    override fun getTemplates(guildId: Snowflake): Flow<Template> {
        return storeAndEmit(restSupplier.getTemplates(guildId)) { it.data }
    }

    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? {
        return storeAndReturn(restSupplier.getStageInstanceOrNull(channelId)) { it.data }
    }

    private suspend inline fun <T, R> Flow<T>.mapAndStore(crossinline transform: (T) -> R) {
        val data = this.map { transform(it) }
        kord.cache.put(data)
    }

    private inline fun <T, R> storeAndEmit(source: Flow<T>, crossinline transform: (T) -> R): Flow<T> {
        return flow {
            source.mapAndStore { transform(it) }
            emitAll(source)
        }
    }

    private suspend inline fun <T, reified R : Any> storeAndReturn(value: T?, transform: (T) -> R): T? {
        if (value == null) return null
        cache.put(transform(value))
        return value
    }
}