package com.gitlab.kordlib.core.`object`.builder.channel

import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildChannelPositionModifyRequest

class SwapChannelPositionsBuilder (
        private val swaps: MutableList<Pair<String, Int>> = mutableListOf()
) {
    fun move(pair: Pair<Snowflake, Int>) {
        swaps += pair.first.value to pair.second
    }

    fun move(vararg pairs: Pair<Snowflake, Int>) {
        swaps += pairs.map { it.first.value to it.second }
    }

    fun toRequest(): GuildChannelPositionModifyRequest =
            GuildChannelPositionModifyRequest(swaps)
}