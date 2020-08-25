package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class VoiceRegion(val id: String, val name: String, val vip: Boolean, val optimal: Boolean, val deprecated: Boolean, val custom: Boolean)
