package dev.kord.rest.builder.scheduled_events

import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ScheduledEventModifyRequest
import kotlinx.datetime.Instant

class ScheduledEventModifyBuilder : RequestBuilder<ScheduledEventModifyRequest> {
    private var _channelId: OptionalSnowflake = OptionalSnowflake.Missing
    var channelId by ::_channelId.delegate()

    private var _name: Optional<String> = Optional.Missing()
    var name by ::_name.delegate()

    private var _privacyLevel: Optional<StageInstancePrivacyLevel> = Optional.Missing()
    var privacyLevel by ::_privacyLevel.delegate()

    private var _scheduledStartTime: Optional<Instant> = Optional.Missing()
    var scheduledStartTime by ::_scheduledStartTime.delegate()

    private var _description: Optional<String> = Optional.Missing()
    var description by ::_description.delegate()

    private var _entityType: Optional<ScheduledEntityType> = Optional.Missing()
    var entityType by ::_entityType.delegate()

    override fun toRequest(): ScheduledEventModifyRequest = ScheduledEventModifyRequest(
        _channelId,
        _name,
        _privacyLevel,
        _scheduledStartTime,
        _description,
        _entityType
    )
}
