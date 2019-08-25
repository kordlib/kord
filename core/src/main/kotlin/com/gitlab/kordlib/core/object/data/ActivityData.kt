package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.common.entity.Activity
import com.gitlab.kordlib.common.entity.ActivityType
import kotlinx.serialization.Serializable

@Serializable
data class ActivityData(
        val name: String,
        val type: ActivityType,
        val url: String? = null,
        val start: Int? = null,
        val stop: Int? = null,
        val applicationId: String? = null,
        val details: String? = null,
        val state: String? = null,
        val partyId: String? = null,
        val partyCurrentSize: Int? = null,
        val partyMaxSize: Int? = null,
        val largeImage: String? = null,
        val largeText: String? = null,
        val smallImage: String? = null,
        val smallText: String? = null,
        val secretsJoin: String? = null,
        val secretsSpectate: String? = null,
        val secretsMatch: String? = null,
        val instance: Boolean? = null,
        val flags: Int? = null
) {
    companion object {
        fun from(entity: Activity) = with(entity) {
            ActivityData(
                    name,
                    type,
                    url,
                    timestamps?.start,
                    timestamps?.end,
                    applicationId,
                    details,
                    state,
                    party?.id,
                    party?.size?.first(),
                    party?.size?.last(),
                    assets?.largeImage,
                    assets?.largeText,
                    assets?.smallImage,
                    assets?.smallText,
                    secrets?.join,
                    secrets?.spectate,
                    secrets?.match,
                    instance,
                    flags
            )
        }
    }


}