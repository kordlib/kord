package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordRoleTags
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.Serializable

@Serializable
data class RoleTagsData(
        val botId: OptionalSnowflake,
        val integrationId: OptionalSnowflake,
        val premiumSubscriber: Boolean,
) {

    companion object {
        fun from(entity: DiscordRoleTags): RoleTagsData = with(entity) {
            RoleTagsData(botId, integrationId, premiumSubscriber is Optional.Null)
        }
    }

}
