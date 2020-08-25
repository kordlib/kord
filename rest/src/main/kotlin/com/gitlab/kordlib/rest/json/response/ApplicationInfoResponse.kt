@file:Suppress("ArrayInDataClass")

package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.DiscordTeam
import com.gitlab.kordlib.common.entity.DiscordUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payload gotten from [GET /oauth2/applications/@me](https://discord.com/developers/docs/topics/oauth2#get-current-application-information)
 */
@Serializable
@KordUnstableApi
data class ApplicationInfoResponse(
        val id: String,
        val name: String,
        val icon: String? = null,
        val description: String? = null,
        @SerialName("rpc_origins")
        val rpcOrigins: Array<String>? = null,
        @SerialName("bot_public")
        val botPublic: Boolean,
        @SerialName("bot_require_code_grant")
        val botRequireCodeGrant: Boolean,
        val owner: DiscordUser,
        val summary: String,
        @SerialName("verify_key")
        val verifyKey: String,
        val team: DiscordTeam?,
        @SerialName("guild_id")
        val guildId: String? = null,
        @SerialName("primary_sku_id")
        val primarySkuId: String? = null,
        val slug: String? = null,
        @SerialName("cover_image")
        val coverImage: String? = null
)