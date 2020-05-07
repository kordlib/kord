package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.cache.data.InviteData
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.indexOfFirstOrNull
import com.gitlab.kordlib.rest.builder.channel.ChannelPermissionModifyBuilder
import com.gitlab.kordlib.rest.service.editRolePermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * The behavior of a Discord channel associated to a [guild].
 */
interface GuildChannelBehavior : ChannelBehavior, Strategizable {
    /**
     * The id of the guild this channel is associated to.
     */
    val guildId: Snowflake

    /**
     * The guild behavior this channel is associated to.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the invites of this channel.
     */
    val invites: Flow<Invite> get() = flow {
        val responses = kord.rest.channel.getChannelInvites(id.value)

        for (response in responses) {
            val data = InviteData.from(response)

            emit(Invite(data, kord))
        }
    }

    /**
     * Requests to get this behavior as a [GuildChannel].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    override suspend fun asChannel(): GuildChannel = super.asChannel() as GuildChannel


    /**
     * Requests to get this behavior as a [Guild].
     */
    suspend fun getGuild(): Guild = strategy.supply(kord).getGuild(guildId)!!

    /**
     * Requests to add or replace a [PermissionOverwrite] to this entity.
     */
    suspend fun addOverwrite(overwrite: PermissionOverwrite) {
        kord.rest.channel.editChannelPermissions(channelId = id.value, overwriteId = overwrite.target.value, permissions = overwrite.asRequest())
    }

    /**
     * Requests to get the position of this channel in the [guild], as displayed in Discord.
     */
    suspend fun getPosition(): Int = guild.channels.indexOfFirstOrNull { it.id == id }!!

    override fun compareTo(other: Entity): Int {
        if (other !is GuildChannelBehavior) return super.compareTo(other)
        val discordOrder = compareBy<GuildChannelBehavior> { it.guildId }
                .thenBy { (it as? GuildChannel)?.guildId }
                .thenBy { it.id }

        return discordOrder.compare(this, other)
    }

    /**
     * returns a new [GuildChannelBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */

    override fun withStrategy(strategy: EntitySupplyStrategy): GuildChannelBehavior = GuildChannelBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy) = object : GuildChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy
        }
    }

}

/**
 * Requests to add or replace a [PermissionOverwrite] for the [roleId].
 */
suspend inline fun GuildChannelBehavior.editRolePermission(roleId: Snowflake, builder: ChannelPermissionModifyBuilder.() -> Unit) {
    kord.rest.channel.editRolePermission(channelId = id.value, roleId = roleId.value, builder = builder)
}

/**
 * Requests to add or replace a [PermissionOverwrite] for the [memberId].
 */
suspend inline fun GuildChannelBehavior.editMemberPermission(memberId: Snowflake, builder: ChannelPermissionModifyBuilder.() -> Unit) {
    kord.rest.channel.editRolePermission(channelId = id.value, roleId = memberId.value, builder = builder)
}
