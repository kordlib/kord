package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdentifyData(
        internal val token: String,
        val properties: IdentifyProperties,
        val compress: Boolean?,
        @SerialName("large_threshold")
        val largeThreshold: Int = 50,
        val shard: List<Int>?,
        val presence: Presence?
) {
    override fun toString(): String = "IdentifyData(token=hunter2,properties=$properties,compress=$compress,largeThreshold=$largeThreshold," +
            "shard=$shard,presence=$presence"

}

@Serializable
data class IdentifyProperties(
        @SerialName("\$os")
        val os: String,
        @SerialName("\$browser")
        val browser: String,
        @SerialName("\$device")
        val device: String
)

@Serializable
data class Presence(
        val status: String,
        val afk: Boolean,
        val since: Int?,
        val game: Activity?
)