package dev.kord.rest.json.response

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordThreadMember
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartialChannelResponse(val name: String, val type: ChannelType)


@Serializable
data class ListThreadsResponse(
    val threads: List<DiscordChannel>,
    val members: List<DiscordThreadMember>,
    @SerialName("has_more")
    val hasMore: Boolean
)