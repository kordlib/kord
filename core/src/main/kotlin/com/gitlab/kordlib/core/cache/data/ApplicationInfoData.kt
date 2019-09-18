package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.rest.json.response.ApplicationInfoResponse

data class ApplicationInfoData(
        val id: Long,
        val name: String,
        val icon: String?,
        val description: String?,
        val botPublic: Boolean,
        val botRequireCodeGrant: Boolean,
        val ownerId: Long
) {
    companion object {

        fun from(entity: ApplicationInfoResponse) = with(entity) {
            ApplicationInfoData(id.toLong(), name, icon, description, botPublic, botRequireCodeGrant, owner.id.toLong())
        }

    }
}