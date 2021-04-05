package dev.kord.rest.builder.channel

import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.ChannelPositionSwapRequest
import dev.kord.rest.json.request.GuildChannelPositionModifyRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

    @OptIn(ExperimentalContracts::class)
    inline fun move(channel: Snowflake, builder: GuildChannelSwapBuilder.() -> Unit){
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        swaps.firstOrNull { it.channelId == channel }?.builder() ?: run {
            swaps.add(GuildChannelSwapBuilder(channel).also(builder))
        }
    }

    override fun toRequest(): GuildChannelPositionModifyRequest =
            GuildChannelPositionModifyRequest(swaps.map { it.toRequest() })
}


class GuildChannelSwapBuilder(var channelId: Snowflake) {


    private var _position: OptionalInt? = OptionalInt.Missing
    /**
     * The new position of this channel
     */
    var position: Int? by ::_position.delegate()

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
            channelId, _position, lockPermissionsToParent, parentId
    )

}
