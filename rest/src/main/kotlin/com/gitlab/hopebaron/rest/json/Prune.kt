package com.gitlab.hopebaron.rest.json

import kotlinx.serialization.Serializable

@Serializable
data class PruneResponse(val pruned: Int)