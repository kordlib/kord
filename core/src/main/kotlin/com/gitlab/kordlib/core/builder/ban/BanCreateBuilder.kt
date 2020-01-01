package com.gitlab.kordlib.core.builder.ban

import com.gitlab.kordlib.core.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.builder.KordDsl
import com.gitlab.kordlib.rest.json.request.GuildBanAddRequest

@KordDsl
class BanCreateBuilder : AuditRequestBuilder<GuildBanAddRequest> {
    /**
     * The reason for banning this member.
     */
    override var reason: String? = null

    /**
     * The number of days to delete messages for (0-7).
     */
    var deleteMessagesDays: Int? = null

    override fun toRequest() = GuildBanAddRequest(reason, deleteMessagesDays)
}