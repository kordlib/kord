package com.gitlab.kordlib.rest.builder.role

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildRolePositionModifyRequest

@KordDsl
class RolePositionsModifyBuilder : AuditRequestBuilder<GuildRolePositionModifyRequest> {
    override var reason: String? = null
    private val swaps: MutableList<Pair<String, Int>> = mutableListOf()

    fun move(pair: Pair<Snowflake, Int>) {
        swaps += pair.first.asString to pair.second
    }

    fun move(vararg pairs: Pair<Snowflake, Int>) {
        swaps += pairs.map { it.first.asString to it.second }
    }

    override fun toRequest(): GuildRolePositionModifyRequest =
            GuildRolePositionModifyRequest(swaps)
}