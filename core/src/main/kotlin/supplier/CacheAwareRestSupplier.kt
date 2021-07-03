package dev.kord.core.supplier

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.flow.*
/**
 * [EntitySupplier] that uses a [RestEntitySupplier] to resolve entities.
 *
 * Resolved entities will always be stored in [cache] if it wasn't null or empty for flows.
 */
class CacheAwareRestEntitySupplier(val restSupplier: RestEntitySupplier,
                                   val cache: DataCache,
                                   val kord: Kord) : EntitySupplier {


    override val guilds: Flow<Guild>
        get() = flow {
            storeAndEmit(restSupplier.guilds) { it.data }
        }
    override val regions: Flow<Region>
        get() = flow {
            storeAndEmit(restSupplier.regions) { it.data }
        }

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
        return flow {
            storeAndEmit(restSupplier.getGuildChannels(guildId)) { it.data }
        }
    }

    override fun getChannelPins(channelId: Snowflake): Flow<Message> {
        return flow {
            storeAndEmit(restSupplier.getChannelPins(channelId)) { it.data }
        }
    }

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? {
        return storeAndReturn(restSupplier.getMemberOrNull(guildId, userId)) { it.data }
    }

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? {
        return storeAndReturn(restSupplier.getMessageOrNull(channelId, messageId)) { it.data }
    }

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        return flow {
            storeAndEmit(restSupplier.getMessagesAfter(messageId, channelId, limit)) { it.data }
        }
    }

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        return flow {
            storeAndEmit(restSupplier.getMessagesBefore(messageId, channelId, limit)) { it.data }
        }
    }

    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        return flow {
            storeAndEmit(restSupplier.getMessagesAround(messageId, channelId, limit)) { it.data }
        }
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
        return flow {
            storeAndEmit(restSupplier.getGuildRoles(guildId)) { it.data }
        }
    }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? {
        return storeAndReturn(getGuildBanOrNull(guildId, userId)) { it.data }
    }

    override fun getGuildBans(guildId: Snowflake): Flow<Ban> {
        return flow {
            storeAndEmit(restSupplier.getGuildBans(guildId)) { it.data }
        }
    }

    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> {
        return flow {
            storeAndEmit(restSupplier.getGuildMembers(guildId, limit)) { it.data }
        }
    }

    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> {
        return flow {
            storeAndEmit(restSupplier.getGuildVoiceRegions(guildId)) { it.data }
        }
    }

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? {
        return storeAndReturn(restSupplier.getEmojiOrNull(guildId, emojiId)) { it.data }
    }

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> {
        return flow {
            storeAndEmit(restSupplier.getEmojis(guildId)) { it.data }
        }
    }

    override fun getCurrentUserGuilds(limit: Int): Flow<Guild> {
        return flow {
            storeAndEmit(restSupplier.getCurrentUserGuilds(limit)) { it.data }
        }
    }

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> {
        return flow {
            storeAndEmit(restSupplier.getChannelWebhooks(channelId)) { it.data }
        }
    }

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> {
        return flow {
            storeAndEmit(restSupplier.getGuildWebhooks(guildId)) { it.data }
        }
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
        return flow {
            storeAndEmit(restSupplier.getTemplates(guildId)) { it.data }
        }
    }

    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? {
        return storeAndReturn(restSupplier.getStageInstanceOrNull(channelId)) { it.data }
    }

    private suspend inline fun <T, R> Flow<T>.mapAndStore(crossinline transform: (T) -> R) {
        val data = this.map { transform(it) }
        kord.cache.put(data)
    }

    private suspend inline fun <T, R> FlowCollector<T>.storeAndEmit(source: Flow<T>, crossinline transform: (T) -> R) {
        source.mapAndStore { transform(it) }
        emitAll(source)
    }

    private suspend inline fun <T, reified R : Any> storeAndReturn(value: T?, transform: (T) -> R): T? {
        if (value == null) return null
        cache.put(transform(value))
        return value
    }
}