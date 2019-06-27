package com.gitlab.hopebaron.rest.json.response

import com.gitlab.hopebaron.common.entity.GuildIntegrations
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Connection(val id: String,
                      val name: String,
                      val type: String,
                      val revoked: Boolean,
                      val integrations: List<GuildIntegrations>,
                      val verified: Boolean,
                      @SerialName("friend_sync")
                      val friendSync: Boolean,
                      @SerialName("show_activity")
                      val showActivity: Boolean,
                      val visibility: Int)
//TODO add a visibility enum class