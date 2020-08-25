package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.rest.json.response.ApplicationInfoResponse

@KordUnstableApi
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
        val teamId: Long?,
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
                    team?.id?.toLong(),
                    guildId?.toLong(),
                    primarySkuId?.toLong(),
                    slug,
                    coverImage
            )
        }

    }
}