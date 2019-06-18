package com.gitlab.hopebaron.rest.json

import kotlinx.serialization.Serializable

@Serializable
data class PruneResult(val pruned: Int)