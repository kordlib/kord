package com.gitlab.kordlib.core.`object`.builder.member

import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.ModifyGuildMemberRequest

class UpdateMemberBuilder (
        var voiceChannelId: Snowflake? = null,
        var muted: Boolean? = null,
        var deafened: Boolean? = null,
        var nickname: String? = null,
        var roles: MutableSet<Snowflake> = mutableSetOf()
) {
    fun toRequest(): ModifyGuildMemberRequest = ModifyGuildMemberRequest(
            nick = nickname,
            channelId = voiceChannelId?.value,
            mute = muted,
            deaf = deafened,
            roles = roles.map { it.value }
    )
}