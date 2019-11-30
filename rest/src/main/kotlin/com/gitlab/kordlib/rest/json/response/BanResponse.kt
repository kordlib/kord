package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.entity.DiscordUser
import kotlinx.serialization.Serializable

@Serializable
data class BanResponse(val reason: String?, val user: DiscordUser)