package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.ClientStatus
import com.gitlab.kordlib.common.entity.Status
import kotlinx.serialization.Serializable

@Serializable
data class ClientStatusData(
        val desktop: Status?,
        val mobile: Status?,
        val web: Status?
) {
    companion object {
        fun from(entity: ClientStatus) = with(entity) {
            ClientStatusData(desktop, mobile, web)
        }
    }
}