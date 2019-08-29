package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.rest.json.response.BanResponse
import kotlinx.serialization.Serializable

@Serializable
data class BanData(val reason: String?, val userId: String) {
    companion object {
        fun from(entity: BanResponse) = with(entity) {
            BanData(reason, user.id)
        }
    }
}