package com.gitlab.kordlib.rest.builder.role

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildRolePositionModifyRequest

@KordDsl
class RolePositionsModifyBuilder : AuditRequestBuilder<GuildRolePositionModifyRequest> {
    override var reason: String? = null
    private val swaps: MutableList<Pair<Snowflake, Int>> = mutableListOf()

    fun move(pair: Pair<Snowflake, Int>) {
        swaps += pair.first to pair.second
    }

    fun move(vararg pairs: Pair<Snowflake, Int>) {
        swaps += pairs.map { it.first to it.second }
    }

    override fun toRequest(): GuildRolePositionModifyRequest =
            GuildRolePositionModifyRequest(swaps)
}