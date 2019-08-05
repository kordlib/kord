package com.gitlab.kordlib.core.behavior.role

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.role.UpdateRoleBuilder
import com.gitlab.kordlib.core.behavior.guild.GuildBehavior
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.flow.Flow


/**
 * The behavior of a [Discord Role](https://discordapp.com/developers/docs/topics/permissions#role-object) associated to a [guild].
 */
interface RoleBehavior : Entity {
    val guildId: Snowflake
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to change the [position] of this role.
     */
    suspend fun changePosition(position: Int) : Flow<Nothing /*Role*/> = TODO()

    /**
     * Requests to get the position of this role in the role list of this guild.
     */
    suspend fun getPosition() : Int = TODO()

    /**
     *Requests to delete this role.
     */
    suspend fun delete() {
        kord.rest.guild.deleteGuildRole(guildId = guildId.value, roleId = id.value)
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord): RoleBehavior = object: RoleBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }
}

/**
 * Requests to edit this role.
 *
 * @return The edited [Role].
 */
suspend inline fun RoleBehavior.edit(builder: UpdateRoleBuilder.() -> Unit): Nothing /*Role*/  = TODO()