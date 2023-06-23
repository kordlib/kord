package dev.kord.rest.json.response

import kotlinx.serialization.Serializable

@Serializable
public data class CurrentUserNicknameModifyResponse(val nick: String)
