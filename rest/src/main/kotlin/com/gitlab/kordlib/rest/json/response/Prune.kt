package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class GetPruneResponse(val pruned: Int)

@Serializable
@KordUnstableApi
data class PruneResponse(val pruned: Int?)