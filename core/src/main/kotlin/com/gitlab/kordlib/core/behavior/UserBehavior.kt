package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.DmChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.rest.json.request.DMCreateRequest
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.RestClient

/**
 * The behavior of a [Discord User](https://discordapp.com/developers/docs/resources/user)
 */
interface UserBehavior : Entity, Strategizable {

    val mention: String get() = "<@${id.value}>"

    /**
     * Requests to get the this behavior as a [Member] in the [Guild] with the [guildId] through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the member wasn't present.
     */
    suspend fun asMember(guildId: Snowflake): Member = strategy.supply(kord).getMember(guildId = guildId, userId = id)

    /**
     * Requests to get this behavior as a [Member] in the [Guild] with the [guildId] through the [strategy],
     * returns null if the member isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun asMemberOrNull(guildId: Snowflake): Member? = strategy.supply(kord).getMemberOrNull(guildId = guildId, userId = id)


    /**
     * Requests to get the this behavior as a [User] through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    suspend fun asUser(): User = strategy.supply(kord).getUser(id)

    /**
     * Requests to get this behavior as a [User] through the [strategy],
     * returns null if the user isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun asUserOrNull(): User? = strategy.supply(kord).getUserOrNull(id)


    /**
     * Requests to get or create a [DmChannel] between this bot and the user.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * @throws [RestRequestException] if something went wrong during the request.
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
    override fun withStrategy(strategy: EntitySupplyStrategy): UserBehavior = UserBehavior(id, kord, strategy)

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy): UserBehavior = object : UserBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy
        }
    }

}
