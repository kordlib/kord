package com.gitlab.kordlib.core.behavior.role

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.role.UpdateRoleBuilder
import com.gitlab.kordlib.core.behavior.guild.GuildBehavior
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.flow.Flow

interface RoleBehavior : Entity {
    val guildId: Snowflake
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun changePosition(position: Int) : Flow<Nothing /*Role*/> = TODO()

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

suspend inline fun RoleBehavior.edit(builder: UpdateRoleBuilder.() -> Unit): Nothing /*Role*/  = TODO()