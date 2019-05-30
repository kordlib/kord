package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Activity(
        val name: String,
        val type: Int,
        val url: String? = null,
        val timestamps: ActivityTimeStamps? = null,
        @SerialName("application_id")
        val applicationId: String? = null,
        val details: String? = null,
        val state: String? = null,
        val party: ActivityParty? = null,
        val assets: ActivityAssets? = null,
        val secrets: ActivitySecrets? = null,
        val instance: Boolean? = null,
        val flags: Int? = null
)

@Serializable
data class ActivityTimeStamps(
        val start: Long? = null,
        val end: Long? = null
)

@Serializable
data class ActivityParty(
        val id: String? = null,
        val size: List<Int>? = null
)

@Serializable
data class ActivityAssets(
        @SerialName("large_image")
        val largeImage: String? = null,
        @SerialName("large_text")
        val largeText: String? = null,
        @SerialName("small_image")
        val smallImage: String? = null,
        @SerialName("small_text")
        val smallText: String? = null
)

@Serializable
data class ActivitySecrets(
        val join: String? = null,
        val spectate: String? = null,
        val match: String? = null
)