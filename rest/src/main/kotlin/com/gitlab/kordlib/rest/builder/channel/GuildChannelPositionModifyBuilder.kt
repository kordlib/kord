package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.annotation.KordExperimental
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.ChannelPositionSwapRequest
import com.gitlab.kordlib.rest.json.request.GuildChannelPositionModifyRequest

@KordDsl
class GuildChannelPositionModifyBuilder: AuditRequestBuilder<GuildChannelPositionModifyRequest>  {
    override var reason: String? = null
    var swaps: MutableList<GuildChannelSwapBuilder> = mutableListOf()

    fun move(pair: Pair<Snowflake, Int>) {
        move(pair.first) { position = pair.second }
    }

    fun move(vararg pairs: Pair<Snowflake, Int>) {
        pairs.forEach { move(it) }
    }

    inline fun move(channel: Snowflake, builder: GuildChannelSwapBuilder.() -> Unit){
        swaps.firstOrNull { it.channelId == channel }?.builder() ?: run {
            swaps.add(GuildChannelSwapBuilder(channel).also(builder))
        }
    }

    override fun toRequest(): GuildChannelPositionModifyRequest =
            GuildChannelPositionModifyRequest(swaps.map { it.toRequest() })
}


class GuildChannelSwapBuilder(var channelId: Snowflake) {

    /**
     * The new position of this channel
     */
    var position: Int? = null

    /**
     * The new parent of this channel, has to be a category.
     *
     * **Only one channel can have a parent id modified per request**.
     *
     * This field is not officially supported by the Discord API, and might change/be removed in the future.
     */
    @KordExperimental
    var parentId: Snowflake? = null

    /**
     * Locks the permissions of this channel to the new category it is moved to.
     * Only works if [parentId] is set.
     *
     * This field is not officially supported by the Discord API, and might change/be removed in the future.
     */
    @KordExperimental
    var lockPermissionsToParent: Boolean? = null

    @OptIn(KordExperimental::class)
    fun toRequest(): ChannelPositionSwapRequest = ChannelPositionSwapRequest(
            channelId, position, lockPermissionsToParent, parentId
    )

}
