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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RolePositionsModifyBuilder

        if (reason != other.reason) return false
        if (swaps != other.swaps) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reason?.hashCode() ?: 0
        result = 31 * result + swaps.hashCode()
        return result
    }

}
