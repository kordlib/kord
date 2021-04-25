package dev.kord.rest.builder.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.CurrentVoiceStateModifyRequest
import dev.kord.rest.json.request.VoiceStateModifyRequest
import java.time.OffsetDateTime

class CurrentVoiceStateModifyBuilder(val channelId: Snowflake) : RequestBuilder<CurrentVoiceStateModifyRequest> {

    private var _requestToSpeakTimestamp: Optional<OffsetDateTime> = Optional.Missing()

    private var _suppress: OptionalBoolean = OptionalBoolean.Missing

    /**
     * sets the user's request to speak.
     */
    var requestToSpeakTimestamp: OffsetDateTime? by ::_requestToSpeakTimestamp.delegate()

    /**
     *  whether this user is muted by the current user.
     */
    var suppress: Boolean? by ::_suppress.delegate()


    override fun toRequest(): CurrentVoiceStateModifyRequest {
        return CurrentVoiceStateModifyRequest(channelId, _suppress, _requestToSpeakTimestamp.map { it.toString() })
    }
}


class VoiceStateModifyBuilder(val channelId: Snowflake) : RequestBuilder<VoiceStateModifyRequest> {

    private var _suppress: OptionalBoolean = OptionalBoolean.Missing

    /**
     *  whether this user is muted by the current user.
     */
    var suppress: Boolean? by ::_suppress.delegate()

    override fun toRequest(): VoiceStateModifyRequest {
        return VoiceStateModifyRequest(channelId, _suppress)
    }
}
