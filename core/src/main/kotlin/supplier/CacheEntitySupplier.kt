package dev.kord.core.supplier

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.query
import dev.kord.common.entity.ChannelType

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.any
import dev.kord.core.cache.data.*
import dev.kord.core.cache.idEq
import dev.kord.core.cache.idGt
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadUser
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.gateway.Gateway
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

/**
 * [EntitySupplier] that uses a [DataCache] to resolve entities.
 *
 * Getting existing entities should not throw any [RequestException] unless
 * specified by the configured cache.
 *
 * The supplier might not be able to return entities independent of their actual
 * existence, their presence depends on incoming events from the [Gateway] and
 * how the cache is set up to store entities.
 *
 * Returned flows without entities will not throw an [EntityNotFoundException]
 * if none are presented like other `getX` functions. Instead, the flow will be empty.
 */
class CacheEntitySupplier(private val kord: Kord) : EntitySupplier {
    /**
     *
     * The Cache this [CacheEntitySupplier] operates on.
     *
     * short-hand for [Kord.cache]
     *
     */
    private val cache: DataCache = kord.cache

    /**
     *  Returns a [Flow] of [Channel]s fetched from cache.
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val channels: Flow<Channel>
        get() = cache.query<ChannelData>().asFlow().map { Channel.from(it, kord) }

    /**
     *  fetches all cached [Guild]s
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    override val guilds: Flow<Guild>
        get() = cache.query<GuildData>().asFlow().map { Guild(it, kord) }

    /**
     *  fetches all cached [Region]s
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    override val regions: Flow<Region>
        get() = cache.query<RegionData>().asFlow().map { Region(it, kord) }

    /**
     *  fetches all cached [Role]s
     */
    val roles: Flow<Role>
        get() = cache.query<RoleData>().asFlow().map { Role(it, kord) }

    /**
     *  fetches all cached [User]s
     */
    val users: Flow<User>
        get() = cache.query<UserData>().asFlow().map { User(it, kord) }

    /**
     *  fetches all cached [Member]s
     */
    @OptIn(FlowPreview::class)
    val members: Flow<Member>
        get() = cache.query<MemberData>().asFlow().mapNotNull {
            val userData =
                cache.query<UserData> { idEq(UserData::id, it.userId) }.singleOrNull() ?: return@mapNotNull null
            Member(it, userData, kord)
        }

    suspend fun getRole(id: Snowflake): Role? {
        val data = cache.query<RoleData> { idEq(RoleData::id, id) }.singleOrNull() ?: return null

        return Role(data, kord)
    }

    override suspend fun getGuildPreviewOrNull(guildId: Snowflake): GuildPreview? {
        val data = cache.query<GuildPreviewData> { idEq(GuildPreviewData::id, guildId) }.singleOrNull() ?: return null

        return GuildPreview(data, kord)
    }

    override suspend fun getGuildWidgetOrNull(guildId: Snowflake): GuildWidget? = null

    override suspend fun getChannelOrNull(id: Snowflake): Channel? {
        val data = cache.query<ChannelData> { idEq(ChannelData::id, id) }.singleOrNull() ?: return null
        return Channel.from(data, kord)
    }

    override fun getGuildChannels(guildId: Snowflake): Flow<GuildChannel> = cache.query<ChannelData> {
        idEq(ChannelData::guildId, guildId)
    }.asFlow().map { Channel.from(it, kord) }.filterIsInstance()

    override fun getChannelPins(channelId: Snowflake): Flow<Message> = cache.query<MessageData> {
        idEq(MessageData::channelId, channelId)
        idEq(MessageData::pinned, true)
    }.asFlow().map { Message(it, kord) }

    override suspend fun getGuildOrNull(id: Snowflake): Guild? {
        val data = cache.query<GuildData> { idEq(GuildData::id, id) }.singleOrNull() ?: return null
        return Guild(data, kord)
    }

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? {
        val userData = cache.query<UserData> { idEq(UserData::id, userId) }.singleOrNull() ?: return null

        val memberData = cache.query<MemberData> {
            idEq(MemberData::userId, userId)
            idEq(MemberData::guildId, guildId)
        }.singleOrNull() ?: return null

        return Member(memberData, userData, kord)
    }

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? {
        val data = cache.query<MessageData> { idEq(MessageData::id, messageId) }.singleOrNull()
            ?: return null

        return Message(data, kord)
    }

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        return cache.query<MessageData> {
            idEq(MessageData::channelId, channelId)
            MessageData::id gt messageId
        }.asFlow().map { Message(it, kord) }.take(limit)
    }

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        return cache.query<MessageData> {
            idEq(MessageData::channelId, channelId)
            idGt(MessageData::id, messageId)
        }.asFlow().map { Message(it, kord) }.take(limit)
    }


    override fun getMessagesAround(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit in 1..100) { "Expected limit to be in 1..100, but was $limit" }
        return flow {
            emitAll(getMessagesBefore(messageId, channelId, limit / 2))
            emitAll(getMessagesAfter(messageId, channelId, limit / 2))
        }
    }

    override suspend fun getSelfOrNull(): User? = getUserOrNull(kord.selfId)

    override suspend fun getRoleOrNull(guildId: Snowflake, roleId: Snowflake): Role? {
        val data = cache.query<RoleData> {
            idEq(RoleData::id, roleId)
            idEq(RoleData::guildId, guildId)
        }.singleOrNull() ?: return null

        return Role(data, kord)
    }

    override fun getGuildRoles(guildId: Snowflake): Flow<Role> = cache.query<RoleData> {
        idEq(RoleData::guildId, guildId)
    }.asFlow().map { Role(it, kord) }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? {
        val data = cache.query<BanData> {
            idEq(BanData::userId, userId)
            idEq(BanData::guildId, guildId)
        }.singleOrNull() ?: return null
        return Ban(data, kord)
    }

    override fun getGuildBans(guildId: Snowflake): Flow<Ban> = cache.query<BanData> {
        idEq(BanData::guildId, guildId)
    }.asFlow().map { Ban(it, kord) }

    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        return cache.query<MemberData> { idEq(MemberData::guildId, guildId) }.asFlow().mapNotNull {
            val userData =
                cache.query<UserData> { idEq(UserData::id, it.userId) }.singleOrNull() ?: return@mapNotNull null
            Member(it, userData, kord)
        }
    }

    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> = cache.query<RegionData> {
        idEq(RegionData::guildId, guildId)
    }.asFlow().map { Region(it, kord) }

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? {
        val data = cache.query<EmojiData> {
            idEq(EmojiData::guildId, guildId)
            idEq(EmojiData::id, emojiId)
        }.singleOrNull() ?: return null

        return GuildEmoji(data, kord)
    }

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> = cache.query<EmojiData> {
        idEq(EmojiData::guildId, guildId)
    }.asFlow().map { GuildEmoji(it, kord) }

    override fun getCurrentUserGuilds(limit: Int): Flow<Guild> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        return guilds.filter {
            members.any { it.id == kord.selfId }
        }.take(limit)
    }

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> = cache.query<WebhookData> {
        idEq(WebhookData::channelId, channelId)
    }.asFlow().map { Webhook(it, kord) }

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> = cache.query<WebhookData> {
        idEq(WebhookData::guildId, guildId)
    }.asFlow().map { Webhook(it, kord) }

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? {
        val data = cache.query<WebhookData> {
            idEq(WebhookData::id, id)
        }.singleOrNull() ?: return null

        return Webhook(data, kord)
    }

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? {
        val data = cache.query<WebhookData> {
            idEq(WebhookData::id, id)
            idEq(WebhookData::token, token)
        }.singleOrNull() ?: return null

        return Webhook(data, kord)
    }

    override suspend fun getUserOrNull(id: Snowflake): User? {
        val data = cache.query<UserData> { idEq(UserData::id, id) }.singleOrNull() ?: return null

        return User(data, kord)
    }

    override suspend fun getTemplateOrNull(code: String): Template? {
        val data = cache.query<TemplateData> {
            idEq(TemplateData::code, code)
        }.singleOrNull() ?: return null

        return Template(data, kord)
    }

    override fun getTemplates(guildId: Snowflake): Flow<Template> {
        return cache.query<TemplateData>() {
            idEq(TemplateData::sourceGuildId, guildId)
        }.asFlow().map { Template(it, kord) }
    }

    override suspend fun getStageInstanceOrNull(channelId: Snowflake): StageInstance? = null

    override fun getThreadMembers(channelId: Snowflake): Flow<ThreadUser> {
        return cache.query<ThreadUserData> {
            idEq(ThreadUserData::id, channelId)
        }.asFlow().map { ThreadUser(it, kord) }
    }

    override fun getActiveThreads(channelId: Snowflake): Flow<ThreadChannel> {
        return cache.query<ChannelData> {
            idEq(ChannelData::id, channelId)
        }.asFlow().filter {
            it.threadsMetadata.value?.archived != true
        }.mapNotNull {
            Channel.from(it, kord) as? ThreadChannel
        }
    }

    override fun getPublicArchivedThreads(channelId: Snowflake, before: Instant, limit: Int): Flow<ThreadChannel> {
        return cache.query<ChannelData> {
            idEq(ChannelData::id, channelId)
        }.asFlow().filter {
            val time = it.threadsMetadata.value?.archiveTimestamp?.toInstant()
            it.threadsMetadata.value?.archived != true
                    && time != null
                    && time < before
                    && (it.type == ChannelType.PublicGuildThread || it.type == ChannelType.PublicNewsThread)
        }.take(limit).mapNotNull { Channel.from(it, kord) as? ThreadChannel }
    }

    override fun getPrivateArchivedThreads(channelId: Snowflake, before: Instant, limit: Int): Flow<ThreadChannel> {
        return cache.query<ChannelData> {
            idEq(ChannelData::id, channelId)
        }.asFlow().filter {
            val time = it.threadsMetadata.value?.archiveTimestamp?.toInstant()
            it.threadsMetadata.value?.archived == true
                    && time != null
                    && time < before
                    && it.type == ChannelType.PrivateThread
        }.take(limit).mapNotNull { Channel.from(it, kord) as? ThreadChannel }
    }

    override fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Instant,
        limit: Int
    ): Flow<ThreadChannel> {
        return cache.query<ChannelData> {
            idEq(ChannelData::id, channelId)
        }.asFlow().filter {
            val time = it.threadsMetadata.value?.archiveTimestamp?.toInstant()
            it.threadsMetadata.value?.archived == true
                    && time != null
                    && time < before
                    && it.type == ChannelType.PrivateThread
                    && kord.selfId in getThreadMembers(channelId).mapNotNull { user -> user.data.id.value }.toList()
        }.take(limit).mapNotNull { Channel.from(it, kord) as? ThreadChannel }
    }

    override fun toString(): String {
        return "CacheEntitySupplier(cache=$cache)"
    }

}