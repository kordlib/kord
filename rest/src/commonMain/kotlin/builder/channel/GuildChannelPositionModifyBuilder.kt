package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ChannelPositionSwapRequest
import dev.kord.rest.json.request.GuildChannelPositionModifyRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public class GuildChannelPositionModifyBuilder : RequestBuilder<GuildChannelPositionModifyRequest> {
    public var swaps: MutableList<GuildChannelSwapBuilder> = mutableListOf()

    public fun move(pair: Pair<Snowflake, Int>) {
        move(pair.first) { position = pair.second }
    }

    public fun move(vararg pairs: Pair<Snowflake, Int>) {
        pairs.forEach { move(it) }
    }

    public inline fun move(channel: Snowflake, builder: GuildChannelSwapBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val swap = swaps.firstOrNull { it.channelId == channel }
        if (swap != null) {
            swap.builder()
        } else {
            swaps.add(GuildChannelSwapBuilder(channel).apply(builder))
        }
    }

    override fun toRequest(): GuildChannelPositionModifyRequest =
        GuildChannelPositionModifyRequest(swaps.map { it.toRequest() })
}

@KordDsl
public class GuildChannelSwapBuilder(public var channelId: Snowflake) {


    private var _position: OptionalInt? = OptionalInt.Missing
    private var _parentId: OptionalSnowflake? = OptionalSnowflake.Missing
    private var _lockPermissionsToParent: OptionalBoolean? = OptionalBoolean.Missing

    /**
     * The new position of this channel
     */
    public var position: Int? by ::_position.delegate()

    /**
     * The new parent of this channel, has to be a category.
     *
     * **Only one channel can have a parent id modified per request**.
     *
     * This field is not officially supported by the Discord API, and might change/be removed in the future.
     */
    @KordExperimental
    public var parentId: Snowflake? by ::_parentId.delegate()

    /**
     * Locks the permissions of this channel to the new category it is moved to.
     * Only works if [parentId] is set.
     *
     * This field is not officially supported by the Discord API, and might change/be removed in the future.
     */
    @KordExperimental
    public var lockPermissionsToParent: Boolean? by ::_lockPermissionsToParent.delegate()

    public fun toRequest(): ChannelPositionSwapRequest = ChannelPositionSwapRequest(
        channelId, _position, _lockPermissionsToParent, _parentId
    )

}
