package dev.kord.core.behavior.channel

import dev.kord.cache.api.query
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.VoiceStateData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.VoiceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public interface BaseVoiceChannelBehavior : CategorizableChannelBehavior {

    /**
     * Requests to retrieve the present voice states of this channel.
     *
     * This property is not resolvable through REST and will always use [Kord.cache] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val voiceStates: Flow<VoiceState>
        get() = kord.cache.query<VoiceStateData> { idEq(VoiceStateData::channelId, id) }
            .asFlow()
            .map { VoiceState(it, kord) }
}
