package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.Permissions
import kotlinx.serialization.Serializable

@Serializable
class PartialGuildData(
        val id: Long,
        val name: String,
        val icon: String? = null,
        val owner: Boolean? = null,
        val permissions: Permissions? = null
) {
    companion object {

        fun from(partialGuild: DiscordPartialGuild) = with(partialGuild) {
            PartialGuildData(id.toLong(), name, icon, owner, permissions)
        }
    }


}

val InviteData.guildId get() = guild?.id