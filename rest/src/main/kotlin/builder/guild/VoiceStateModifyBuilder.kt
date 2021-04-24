package dev.kord.rest.builder.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.CurrentVoiceStateModifyRequest
import dev.kord.rest.json.request.VoiceStateModifyRequest
import java.time.LocalDateTime

class CurrentVoiceStateModifyBuilder(val channelId: Snowflake) : RequestBuilder<CurrentVoiceStateModifyRequest> {

    private var _requestToSpeakTimestamp: Optional<LocalDateTime> = Optional.Missing()

    private var _suppress: OptionalBoolean = OptionalBoolean.Missing

    var requestToSpeakTimestamp: LocalDateTime? by ::_requestToSpeakTimestamp.delegate()

    var suppress: Boolean? by ::_suppress.delegate()


    override fun toRequest(): CurrentVoiceStateModifyRequest {
        return CurrentVoiceStateModifyRequest(channelId, _suppress, _requestToSpeakTimestamp.map { it.toString() })
    }
}


class VoiceStateModifyBuilder(val channelId: Snowflake) : RequestBuilder<VoiceStateModifyRequest> {

    private var _suppress: OptionalBoolean = OptionalBoolean.Missing

    var suppress: Boolean? by ::_suppress.delegate()

    override fun toRequest(): VoiceStateModifyRequest {
        return VoiceStateModifyRequest(channelId, _suppress)
    }
}
