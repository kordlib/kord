package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.GuildFeature
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import kotlinx.serialization.Serializable

@Serializable
class PartialGuildData(
        val id: Snowflake,
        val name: String,
        val icon: String?,
        val owner: OptionalBoolean = OptionalBoolean.Missing,
        val permissions: Optional<Permissions> = Optional.Missing(),
        val features: List<GuildFeature>,
) {
    companion object {

        fun from(partialGuild: DiscordPartialGuild) = with(partialGuild) {
            PartialGuildData(id, name, icon, owner, permissions, features)
        }
    }


}
