package dev.kord.rest.builder.scheduled_events

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.ScheduledEventModifyRequest
import kotlinx.datetime.Instant

public class ScheduledEventModifyBuilder : AuditRequestBuilder<ScheduledEventModifyRequest> {
    override var reason: String? = null

    private var _channelId: OptionalSnowflake? = OptionalSnowflake.Missing

    /**
     * The channel id of the scheduled event, set to `null` if changing [entityType] to
     * [External][ScheduledEntityType.External].
     */
    public var channelId: Snowflake? by ::_channelId.delegate()

    private var _name: Optional<String> = Optional.Missing()

    /** The name of the scheduled event. */
    public var name: String? by ::_name.delegate()

    private var _privacyLevel: Optional<GuildScheduledEventPrivacyLevel> = Optional.Missing()

    /** The [privacy level][GuildScheduledEventPrivacyLevel] of the scheduled event. */
    public var privacyLevel: GuildScheduledEventPrivacyLevel? by ::_privacyLevel.delegate()

    private var _scheduledStartTime: Optional<Instant> = Optional.Missing()

    /** The [Instant] to schedule the scheduled event. */
    public var scheduledStartTime: Instant? by ::_scheduledStartTime.delegate()

    private var _description: Optional<String?> = Optional.Missing()

    /** The description of the scheduled event. */
    public var description: String? by ::_description.delegate()

    private var _entityType: Optional<ScheduledEntityType> = Optional.Missing()

    /** The [entity type][ScheduledEntityType] of the scheduled event. */
    public var entityType: ScheduledEntityType? by ::_entityType.delegate()

    private var _entityMetadata: Optional<GuildScheduledEventEntityMetadata?> = Optional.Missing()

    /** The [entity metadata][GuildScheduledEventEntityMetadata] of the scheduled event. */
    public var entityMetadata: GuildScheduledEventEntityMetadata? by ::_entityMetadata.delegate()

    private var _scheduledEndTime: Optional<Instant> = Optional.Missing()

    /** The [Instant] when the scheduled event is scheduled to end. */
    public var scheduledEndTime: Instant? by ::_scheduledEndTime.delegate()

    private var _status: Optional<GuildScheduledEventStatus> = Optional.Missing()

    /** The [status][GuildScheduledEventStatus] of the scheduled event. */
    public var status: GuildScheduledEventStatus? by ::_status.delegate()

    private var _image: Optional<Image> = Optional.Missing()

    /** The cover image of the scheduled event. */
    public var image: Image? by ::_image.delegate()

    override fun toRequest(): ScheduledEventModifyRequest = ScheduledEventModifyRequest(
        channelId = _channelId,
        entityMetadata = _entityMetadata,
        name = _name,
        privacyLevel = _privacyLevel,
        scheduledStartTime = _scheduledStartTime,
        scheduledEndTime = _scheduledEndTime,
        description = _description,
        entityType = _entityType,
        status = _status,
        image = _image.map { it.dataUri },
    )
}
