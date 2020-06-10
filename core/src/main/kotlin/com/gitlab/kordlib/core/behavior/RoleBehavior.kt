package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.RoleData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.indexOfFirstOrNull
import com.gitlab.kordlib.core.sorted
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.rest.builder.role.RoleModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

/**
 * The behavior of a [Discord Role](https://discordapp.com/developers/docs/topics/permissions#role-object) associated to a [guild].
 */
interface RoleBehavior : Entity, Strategizable {
    /**
     * The id of the guild this channel is associated to.
     */
    val guildId: Snowflake

    /**
     * The guild behavior this channel is associated to.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * The raw mention of this entity.
     */
    val mention: String get() = "<@&${id.value}>"

    /**
     * Requests to change the [position] of this role.
     *
     * This request will execute regardless of the consumption of the return value.
     *
     * @return The roles in of this [guild] in updated order.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun changePosition(position: Int): Flow<Role> {
        val response = kord.rest.guild.modifyGuildRolePosition(guildId.value) {
            move(id to position)
        }
        return response.asFlow().map { RoleData.from(guildId.value, it) }.map { Role(it, kord) }.sorted()
    }

    /**
     * Requests to get the position of this role in the role list of this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun getPosition(): Int = supplier.getGuildRoles(guildId).sorted().indexOfFirstOrNull { it.id == id }!!

    /**
     * Requests to delete this role.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete() {
        kord.rest.guild.deleteGuildRole(guildId = guildId.value, roleId = id.value)
    }

    /**
     * Returns a new [RoleBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): RoleBehavior = RoleBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy): RoleBehavior = object : RoleBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)
        }
    }
}

/**
 * Requests to edit this role.
 *
 * @return The edited [Role].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun RoleBehavior.edit(builder: RoleModifyBuilder.() -> Unit): Role {
    val response = kord.rest.guild.modifyGuildRole(guildId = guildId.value, roleId = id.value, builder = builder)
    val data = RoleData.from(id.value, response)

    return Role(data, kord)
}