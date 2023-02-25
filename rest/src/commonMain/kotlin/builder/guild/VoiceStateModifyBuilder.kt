package dev.kord.rest.builder.guild

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.CurrentVoiceStateModifyRequest
import dev.kord.rest.json.request.VoiceStateModifyRequest
import kotlinx.datetime.Instant
import kotlin.DeprecationLevel.HIDDEN

@KordDsl
public class CurrentVoiceStateModifyBuilder() : RequestBuilder<CurrentVoiceStateModifyRequest> {

    /** @suppress */
    @Deprecated(
        "'channelId' is no longer required, use other constructor instead.",
        ReplaceWith("CurrentVoiceStateModifyBuilder().apply { this@apply.channelId = channelId }"),
        level = HIDDEN,
    )
    public constructor(channelId: Snowflake) : this() {
        this.channelId = channelId
    }

    private var _channelId: OptionalSnowflake = OptionalSnowflake.Missing

    /** The ID of the channel the current user is currently in. */
    public var channelId: Snowflake? by ::_channelId.delegate()

    private var _suppress: OptionalBoolean = OptionalBoolean.Missing

    /** Toggles the current user's suppress state. */
    public var suppress: Boolean? by ::_suppress.delegate()

    private var _requestToSpeakTimestamp: Optional<Instant?> = Optional.Missing()

    /**
     * Sets the current user's request to speak.
     * The timestamp is used to sort how users appear on the moderators' request list.
     *
     * e.g: A client who requested to speak at 18:00,
     * will appear above a client who requested to speak at 20:00 in the same timezone.
     *
     * - A date in the past is treated as "now" by Discord.
     * - A null value removes the request to speak.
     */
    public var requestToSpeakTimestamp: Instant? by ::_requestToSpeakTimestamp.delegate()

    override fun toRequest(): CurrentVoiceStateModifyRequest = CurrentVoiceStateModifyRequest(
        channelId = _channelId,
        suppress = _suppress,
        requestToSpeakTimestamp = _requestToSpeakTimestamp,
    )
}


@KordDsl
public class VoiceStateModifyBuilder(
    /** The ID of the channel the user is currently in. */
    public var channelId: Snowflake,
) : RequestBuilder<VoiceStateModifyRequest> {

    private var _suppress: OptionalBoolean = OptionalBoolean.Missing

    /** Toggles the user's suppress state. */
    public var suppress: Boolean? by ::_suppress.delegate()

    override fun toRequest(): VoiceStateModifyRequest = VoiceStateModifyRequest(
        channelId = channelId,
        suppress = _suppress,
    )
}
