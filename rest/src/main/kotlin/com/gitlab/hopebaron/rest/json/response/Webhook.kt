package com.gitlab.hopebaron.rest.json.response

import com.gitlab.hopebaron.common.entity.Snowflake
import com.gitlab.hopebaron.common.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Webhook(
        val id: Snowflake,
        @SerialName("guild_id")
        val guildId: Snowflake? = null,
        val channelId: Snowflake,
        val user: User? = null,
        val name: String? = null,
        val avatar: String? = null,
        val token: String
)