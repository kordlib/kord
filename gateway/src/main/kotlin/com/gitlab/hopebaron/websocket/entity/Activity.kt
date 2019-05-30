package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Activity(
        val name: String,
        val type: Int,
        val url: String?,
        val timestamps: ActivityTimeStamps?,
        @SerialName("application_id")
        val applicationId: String?,
        val details: String?,
        val state: String?,
        val party: ActivityParty?,
        val assets: ActivityAssets?,
        val secrets: ActivitySecrets?,
        val instance: Boolean?,
        val flags: Int?
)

@Serializable
data class ActivityTimeStamps(
        val start: Long?,
        val end: Long?
)

@Serializable
data class ActivityParty(
        val id: String?,
        val size: List<Int>?
)

@Serializable
data class ActivityAssets(
        @SerialName("large_image")
        val largeImage: String?,
        @SerialName("large_text")
        val largeText: String?,
        @SerialName("small_image")
        val smallImage: String?,
        @SerialName("small_text")
        val smallText: String?
)

@Serializable
data class ActivitySecrets(
        val join: String?,
        val spectate: String?,
        val match: String?
)