package dev.kord.rest.json.response

import kotlinx.serialization.Serializable

@Serializable
data class CurrentUserNicknameModifyResponse(val nick: String)
