package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.Activity
import com.gitlab.kordlib.common.entity.ActivityType
import com.gitlab.kordlib.common.entity.Emoji
import com.gitlab.kordlib.common.entity.PartialEmoji
import kotlinx.serialization.Serializable

@Serializable
data class ActivityData(
        val name: String,
        val type: ActivityType,
        val url: String? = null,
        val start: Long? = null,
        val stop: Long? = null,
        val applicationId: String? = null,
        val details: String? = null,
        val emoji: PartialEmoji? = null,
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
                    emoji,
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