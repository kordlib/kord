package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.http.ParametersBuilder

class AuditLogService(requestHandler: RequestHandler) : RestService(requestHandler) {
    // TODO add action type
    suspend fun getAuditLogs(guildId: String, userId: String, action: ActionType, before: String, limit: Int = 50) = call(Route.AuditLogGet) {
        keys[Route.GuildId] = guildId
        parameters = with(ParametersBuilder()) {
            append("user_id", userId)
            append("action_type", "${action.type}")
            append("before", before)
            append("limit", "$limit")
            build()
        }
    }
}

enum class ActionType(val type: Int) {
    GuildUpdate(1),
    ChannelCreate(10),
    ChannelUpdate(11),
    ChannelDelete(12),
    ChannelOverwriteCreate(13),
    ChannelOverwriteUpdate(14),
    ChannelOverwriteDelete(15),
    MemberKick(20),
    MemberPrune(21),
    MemberBanAdd(22),
    MemberBanDelete(23),
    MemberUpdate(24),
    MemberRoleUpdate(25),
    RoleCreate(30),
    RoleUpdate(31),
    RoleDelete(32),
    InviteCreate(40),
    InviteUpdate(41),
    InviteDelete(42),
    WebhookCreate(50),
    WebhookUpdate(51),
    WebhookDelete(52),
    EmojiCreate(60),
    EmojiUpdate(61),
    EmojiDelete(62),
    MessageDelete(72)
}