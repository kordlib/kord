package com.gitlab.kordlib.core

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.common.entity.PartialGuild
import com.gitlab.kordlib.core.`object`.Pagination
import com.gitlab.kordlib.core.`object`.builder.guild.GuildCreateBuilder
import com.gitlab.kordlib.core.`object`.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.rest.service.RestClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class Kord {

    val configuration: ClientConfiguration = TODO()
    val cache: DataCache = TODO()
    val gateway: Gateway = TODO()
    val rest: RestClient = TODO()
    val selfId: Snowflake? = TODO()
    @Suppress("EXPERIMENTAL_API_USAGE")
    val unsafe: Unsafe = Unsafe(this)

    suspend inline fun createGuild(builder: GuildCreateBuilder.() -> Unit): Guild {
        val request = GuildCreateBuilder().apply(builder).toRequest()

        val response = rest.guild.createGuild(request)
        val data = GuildData.from(response)

        return Guild(data, this)
    }

    suspend fun getChannel(id: Snowflake): Channel? {
        val data = getChannelData(id) ?: return null

        return Channel.from(data, this)
    }

    suspend fun getGuild(guildId: Snowflake): Guild? {
        val data = getGuildData(guildId) ?: return null

        return Guild(data, this)
    }

    suspend fun getGuilds(): Flow<Guild> {
        fun inShard(id: String): Boolean {
            return id.toLong().shr(22) % configuration.shardIndex.toLong() == configuration.shardCount.toLong()
        }

        val cached = cache.find<GuildData>().asFlow().filter { inShard(it.id) }.map { Guild(it, this) }

        //backup if we're not caching
        val request = Pagination
                .after(100, PartialGuild::id) { position, size -> rest.user.getCurrentUserGuilds(position, size) }
                .filter { inShard(it.id) }
                .map { rest.guild.getGuild(it.id) }
                .map { GuildData.from(it) }
                .map { Guild(it, this) }

        return cached.switchIfEmpty(request)
    }

    suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member? {
        val memberData = getMemberData(guildId, userId) ?: return null
        val userData = getUserData(userId) ?: return null

        return Member(memberData, userData, this)
    }

    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message? {
        val data = getMessageData(channelId, messageId) ?: return null

        return Message(data, this)
    }

    suspend fun getRegions(): Flow<Region> =
            rest.voice.getVoiceRegions().asFlow().map { RegionData.from(it) }.map { Region(it, this) }

    suspend fun getRole(guildId: Snowflake, id: Snowflake): Role? {
        val data = getRoleData(guildId, id) ?: return null

        return Role(data, this)
    }

    suspend fun getSelf(): User? {
        val selfId = selfId ?: return null

        val cached = cache.find<UserData> { UserData::id eq selfId.value }.singleOrNull()

        return User(cached ?: UserData.from(rest.user.getCurrentUser()), this)
    }

    suspend fun getUser(userId: Snowflake): User? {
        val data = getUserData(userId) ?: return null

        return User(data, this)
    }

    suspend fun getUsers(): Flow<User> =
            cache.find<UserData>().asFlow().map { User(it, this) }

    suspend fun editPresence(presence: Presence) {
        gateway.send(presence.asUpdate())
    }

    override fun equals(other: Any?): Boolean {
        val kord = other as? Kord ?: return false

        return configuration.token == kord.configuration.token
    }

    internal suspend fun getChannelData(id: Snowflake): ChannelData? {
        val cached = cache.find<ChannelData> { ChannelData::id eq id.value }.singleOrNull()

        return cached ?: catchNotFound { rest.channel.getChannel(id.value).let { ChannelData.from(it) } }
    }

    internal suspend fun getGuildData(id: Snowflake): GuildData? {
        val cached = cache.find<GuildData> { GuildData::id eq id.value }.singleOrNull()

        return cached ?: catchNotFound { rest.guild.getGuild(id.value).let { GuildData.from(it) } }
    }

    internal suspend fun getMemberData(guildId: Snowflake, id: Snowflake): MemberData? {
        val cached = cache.find<MemberData> { MemberData::userId eq id.value }.singleOrNull()

        return cached ?: catchNotFound {
            val response = rest.guild.getGuildMember(guildId = guildId.value, userId = id.value)
            MemberData.from(userId = id.value, guildId = guildId.value, entity = response)
        }
    }

    internal suspend fun getMessageData(channelId: Snowflake, id: Snowflake): MessageData? {
        val cached = cache.find<MessageData> {
            MessageData::id eq id.value
            MessageData::channelId eq channelId.value
        }.singleOrNull()

        return cached ?: catchNotFound {
            val response = rest.channel.getMessage(channelId.value, id.value)
            MessageData.from(response)
        }
    }

    internal suspend fun getRoleData(guildId: Snowflake, id: Snowflake): RoleData? {
        val cached = cache.find<RoleData> {
            RoleData::id eq id.value
            RoleData::guildId eq guildId.value
        }.singleOrNull()

        return cached ?: catchNotFound {
            val response = rest.guild.getGuildRoles(guildId.value)
                    .firstOrNull { it.id == id.value } ?: return@catchNotFound null

            RoleData.from(guildId.value, response)
        }
    }

    internal suspend fun getUserData(id: Snowflake): UserData? {
        val cached = cache.find<UserData> { UserData::id eq id.value }.singleOrNull()

        return cached ?: catchNotFound { rest.user.getUser(id.value).let { UserData.from(it) } }
    }

}