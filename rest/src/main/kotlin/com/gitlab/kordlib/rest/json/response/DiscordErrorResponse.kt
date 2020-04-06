package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.rest.json.JsonErrorCode
import kotlinx.serialization.Serializable

@Serializable
class DiscordErrorResponse(val code: JsonErrorCode, val message: String)