package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordTeam
import com.gitlab.kordlib.rest.json.response.ApplicationInfoResponse

data class ApplicationInfoData(
        val id: Long,
        val name: String,
        val icon: String?,
        val description: String?,
        val botPublic: Boolean,
        val botRequireCodeGrant: Boolean,
        val ownerId: Long,
        val summary: String,
        val verifyKey: String,
        val team: DiscordTeam?,
        val guildId: Long? = null,
        val primarySkuId: Long? = null,
        val slug: String? = null,
        val coverImage: String? = null
) {
    companion object {

        fun from(entity: ApplicationInfoResponse) = with(entity) {
            ApplicationInfoData(
                    id.toLong(),
                    name,
                    icon,
                    description,
                    botPublic,
                    botRequireCodeGrant,
                    owner.id.toLong(),
                    summary,
                    verifyKey,
                    team,
                    guildId?.toLong(),
                    primarySkuId?.toLong(),
                    slug,
                    coverImage
            )
        }

    }
}