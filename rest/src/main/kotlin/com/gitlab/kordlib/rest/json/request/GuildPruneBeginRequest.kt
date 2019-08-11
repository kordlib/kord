package com.gitlab.kordlib.rest.json.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuildPruneGetRequest(
        val days: Int = 7
)

@Serializable
data class GuildPruneBeginRequest(
        val days: Int = 7,
        @SerialName("compute_prune_count")
        val computePruneCount: Boolean = true
)