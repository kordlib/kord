package com.gitlab.hopebaron.websocket

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Event {}

@Serializable
data class Hello(
        @SerialName("heartbeat_interval")
        val interval: Long,
        @SerialName("_trace")
        val trace: String) : Event()

@Serializable
data class Resume(val token: String,
                  @SerialName("session_id")
                  val id: String,
                  @SerialName("seq")
                  val sequance:Int) : Event()

