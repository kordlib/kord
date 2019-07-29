package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Webhook(
        val id: String,
        @SerialName("guild_id")
        val guildId: String? = null,
        val channelId: String,
        val user: User? = null,
        val name: String? = null,
        val avatar: String? = null,
        val token: String
)