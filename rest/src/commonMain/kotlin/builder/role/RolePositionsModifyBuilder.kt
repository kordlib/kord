package dev.kord.rest.builder.role

import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.GuildRolePositionModifyRequest

@KordDsl
public class RolePositionsModifyBuilder : AuditRequestBuilder<GuildRolePositionModifyRequest> {
    override var reason: String? = null
    private val swaps: MutableList<Pair<Snowflake, Int>> = mutableListOf()

    public fun move(pair: Pair<Snowflake, Int>) {
        swaps += pair.first to pair.second
    }

    public fun move(vararg pairs: Pair<Snowflake, Int>) {
        swaps += pairs.map { it.first to it.second }
    }

    override fun toRequest(): GuildRolePositionModifyRequest =
        GuildRolePositionModifyRequest(swaps)
}
