package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.DmChannel
import com.gitlab.kordlib.rest.json.request.DMCreateRequest

/**
 * The behavior of a [Discord User](https://discordapp.com/developers/docs/resources/user)
 */
interface UserBehavior : Entity, Strategizable {

    val mention: String get() = "<@${id.value}>"

    /**
     * Requests this user as a member of the [guild][guildId].
     * Returns null when the user is not a member of the guild.
     */
    suspend fun asMember(guildId: Snowflake): Member = strategy.supply(kord).getMember(guildId = guildId, userId = id)

    suspend fun asMemberOrNull(guildId: Snowflake): Member? = strategy.supply(kord).getMemberOrNull(guildId = guildId, userId = id)

    /**
     * Requests to get the this behavior as a [User].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    suspend fun asUser(): User = strategy.supply(kord).getUser(id)

    suspend fun asUserOrNull(): User? = strategy.supply(kord).getUserOrNull(id)


    /**
     * Requests to get or create a [DmChannel] between this bot and the user.
     */
    suspend fun getDmChannel(): DmChannel {
        val response = kord.rest.user.createDM(DMCreateRequest(id.value))
        val data = ChannelData.from(response)

        return Channel.from(data, kord) as DmChannel
    }

    /**
     * returns a new [UserBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    fun withStrategy(strategy: EntitySupplyStrategy) = UserBehavior(id, kord, strategy)

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy): UserBehavior = object : UserBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy
        }
    }

}

