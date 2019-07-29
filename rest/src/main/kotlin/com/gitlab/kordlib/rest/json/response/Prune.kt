package com.gitlab.kordlib.rest.json.response

import kotlinx.serialization.Serializable

@Serializable
data class PruneResponse(val pruned: Int)