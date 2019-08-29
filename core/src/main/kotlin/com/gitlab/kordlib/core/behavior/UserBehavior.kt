package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.DmChannel
import com.gitlab.kordlib.rest.json.request.DMCreateRequest

/**
 * The behavior of a [Discord User](https://discordapp.com/developers/docs/resources/user)
 */
interface UserBehavior : Entity {

    val mention: String get() = "<@${id.value}>"

    /**
     * Requests this user as a member of the [guild][guildId].
     */
    suspend fun asMember(guildId: Snowflake): Member? = kord.getMember(guildId, id)

    /**
     * Requests to get this behavior as a [User].
     */
    suspend fun asUser(): User = kord.getUser(id)!!

    /**
     * Requests to get or create a [DmChannel] between this bot and the user.
     */
    suspend fun getDmChannel(): DmChannel {
        val response = kord.rest.user.createDM(DMCreateRequest(id.value))
        val data = ChannelData.from(response)

        return Channel.from(data, kord) as DmChannel
    }

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord): UserBehavior = object : UserBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}