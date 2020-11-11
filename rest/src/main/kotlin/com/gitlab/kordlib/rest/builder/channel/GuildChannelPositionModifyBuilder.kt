package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildChannelPositionModifyRequest

@KordDsl
class GuildChannelPositionModifyBuilder: AuditRequestBuilder<GuildChannelPositionModifyRequest>  {
    override var reason: String? = null
    private val swaps: MutableList<Pair<String, Int>> = mutableListOf()

    fun move(pair: Pair<Snowflake, Int>) {
        swaps += pair.first.asString to pair.second
    }

    fun move(vararg pairs: Pair<Snowflake, Int>) {
        swaps += pairs.map { it.first.asString to it.second }
    }

    override fun toRequest(): GuildChannelPositionModifyRequest =
            GuildChannelPositionModifyRequest(swaps)
}