package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.DiscordUser
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class BanResponse(val reason: String?, val user: DiscordUser)