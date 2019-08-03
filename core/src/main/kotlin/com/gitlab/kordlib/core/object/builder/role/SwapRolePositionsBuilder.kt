package com.gitlab.kordlib.core.`object`.builder.role

import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.ModifyGuildRolePositionRequest

class SwapRolePositionsBuilder(
        private val swaps: MutableList<Pair<String, Int>> = mutableListOf()
) {
    fun move(pair: Pair<Snowflake, Int>) {
        swaps += pair.first.value to pair.second
    }

    fun move(vararg pairs: Pair<Snowflake, Int>) {
        swaps += pairs.map { it.first.value to it.second }
    }

    internal fun toRequest(): ModifyGuildRolePositionRequest =
            ModifyGuildRolePositionRequest(swaps)
}