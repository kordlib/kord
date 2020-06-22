package com.gitlab.kordlib.core.supplier

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.any
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.gateway.Gateway
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

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
     *  Returns a [Flow] of [Channel]s fetched from cache.
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val channels: Flow<Channel>
        get() = kord.cache.query<ChannelData>().asFlow().map { Channel.from(it, kord) }

    /**
     *  fetches all cached [Guild]s
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    override val guilds: Flow<Guild>
        get() = kord.cache.query<GuildData>().asFlow().map { Guild(it, kord) }

    /**
     *  fetches all cached [Region]s
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    override val regions: Flow<Region>
        get() = kord.cache.query<RegionData>().asFlow().map { Region(it, kord) }

    /**
     *  fetches all cached [Role]s
     */
    val roles: Flow<Role>
        get() = kord.cache.query<RoleData>().asFlow().map { Role(it, kord) }

    /**
     *  fetches all cached [User]s
     */
    val users: Flow<User>
        get() = kord.cache.query<UserData>().asFlow().map { User(it, kord) }

    /**
     *  fetches all cached [Member]s
     */
    @OptIn(FlowPreview::class)
    val members: Flow<Member>
        get() = kord.cache.query<UserData>().asFlow().flatMapConcat { userData ->
            kord.cache.query<MemberData> { MemberData::userId eq userData.id }
                    .asFlow().map { Member(it, userData, kord) }
        }


    suspend fun getRole(id: Snowflake): Role? {
        val data = kord.cache.query<RoleData> { RoleData::id eq id.longValue }.singleOrNull() ?: return null

        return Role(data, kord)
    }

    override suspend fun getChannelOrNull(id: Snowflake): Channel? {
        val data = kord.cache.query<ChannelData> { ChannelData::id eq id.longValue }.singleOrNull() ?: return null
        return Channel.from(data, kord)
    }

    override fun getGuildChannels(guildId: Snowflake): Flow<GuildChannel> = kord.cache.query<ChannelData> {
        ChannelData::guildId eq guildId.longValue
    }.asFlow().map { Channel.from(it, kord) as GuildChannel }

    override fun getChannelPins(channelId: Snowflake): Flow<Message> = kord.cache.query<MessageData> {
        MessageData::channelId eq channelId.longValue
        MessageData::pinned eq true
    }.asFlow().map { Message(it, kord) }

    override suspend fun getGuildOrNull(id: Snowflake): Guild? {
        val data = kord.cache.query<GuildData> { GuildData::id eq id.longValue }.singleOrNull() ?: return null
        return Guild(data, kord)
    }

    override suspend fun getMemberOrNull(guildId: Snowflake, userId: Snowflake): Member? {
        val userData = kord.cache.query<UserData> { UserData::id eq userId.longValue }.singleOrNull() ?: return null

        val memberData = kord.cache.query<MemberData> {
            MemberData::userId eq userId.longValue
            MemberData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return Member(memberData, userData, kord)
    }

    override suspend fun getMessageOrNull(channelId: Snowflake, messageId: Snowflake): Message? {
        val data = kord.cache.query<MessageData> { MessageData::id eq messageId.longValue }.singleOrNull()
                ?: return null

        return Message(data, kord)
    }

    override fun getMessagesAfter(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        return kord.cache.query<MessageData> {
            MessageData::channelId eq channelId.longValue
            MessageData::id gt messageId.longValue
        }.asFlow().map { Message(it, kord) }.take(limit)
    }

    override fun getMessagesBefore(messageId: Snowflake, channelId: Snowflake, limit: Int): Flow<Message> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        return kord.cache.query<MessageData> {
            MessageData::channelId eq channelId.longValue
            MessageData::id lt messageId.longValue
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
        val data = kord.cache.query<RoleData> {
            RoleData::id eq roleId.longValue
            RoleData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return Role(data, kord)
    }

    override fun getGuildRoles(guildId: Snowflake): Flow<Role> = kord.cache.query<RoleData> {
        RoleData::guildId eq guildId.longValue
    }.asFlow().map { Role(it, kord) }

    override suspend fun getGuildBanOrNull(guildId: Snowflake, userId: Snowflake): Ban? {
        val data = kord.cache.query<BanData> {
            BanData::userId eq userId.longValue
            BanData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null
        return Ban(data, kord)
    }

    override fun getGuildBans(guildId: Snowflake): Flow<Ban> = kord.cache.query<BanData> {
        BanData::guildId eq guildId.longValue
    }.asFlow().map { Ban(it, kord) }

    override fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> {
        return kord.cache.query<UserData>().asFlow().flatMapConcat { userData ->
            kord.cache.query<MemberData> {
                MemberData::userId eq userData.id
                MemberData::guildId eq guildId
            }.asFlow().map { Member(it, userData, kord) }
        }
    }

    override fun getGuildVoiceRegions(guildId: Snowflake): Flow<Region> = kord.cache.query<RegionData> {
        RegionData::guildId eq guildId.longValue
    }.asFlow().map { Region(it, kord) }

    override suspend fun getEmojiOrNull(guildId: Snowflake, emojiId: Snowflake): GuildEmoji? {
        val data = kord.cache.query<EmojiData> {
            EmojiData::guildId eq guildId.longValue
            EmojiData::id eq emojiId.longValue
        }.singleOrNull() ?: return null

        return GuildEmoji(data, kord)
    }

    override fun getEmojis(guildId: Snowflake): Flow<GuildEmoji> = kord.cache.query<EmojiData> {
        EmojiData::guildId eq guildId.longValue
    }.asFlow().map { GuildEmoji(it, kord) }

    override fun getCurrentUserGuilds(limit: Int): Flow<Guild> {
        require(limit > 0) { "At least 1 item should be requested, but got $limit." }
        return guilds.filter {
            members.any { it.id == kord.selfId }
        }.take(limit)
    }

    override fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> = kord.cache.query<WebhookData> {
        WebhookData::channelId eq channelId.longValue
    }.asFlow().map { Webhook(it, kord) }

    override fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> = kord.cache.query<WebhookData> {
        WebhookData::guildId eq guildId.longValue
    }.asFlow().map { Webhook(it, kord) }

    override suspend fun getWebhookOrNull(id: Snowflake): Webhook? {
        val data = kord.cache.query<WebhookData> {
            WebhookData::id eq id.longValue
        }.singleOrNull() ?: return null

        return Webhook(data, kord)
    }

    override suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String): Webhook? {
        val data = kord.cache.query<WebhookData> {
            WebhookData::id eq id.longValue
            WebhookData::token eq token
        }.singleOrNull() ?: return null

        return Webhook(data, kord)
    }

    override suspend fun getUserOrNull(id: Snowflake): User? {
        val data = kord.cache.query<UserData> { UserData::id eq id.longValue }.singleOrNull() ?: return null

        return User(data, kord)
    }


}