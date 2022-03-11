package dev.kord.rest.builder.guild

import dev.kord.common.entity.GuildScheduledEventEntityMetadata
import dev.kord.common.entity.GuildScheduledEventPrivacyLevel
import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildScheduledEventCreateRequest
import kotlinx.datetime.Instant

public class ScheduledEventCreateBuilder(
    /** The name of the scheduled event. */
    public var name: String,
    /** The [privacy level][GuildScheduledEventPrivacyLevel] of the scheduled event. */
    public var privacyLevel: GuildScheduledEventPrivacyLevel,
    /** The [Instant] to schedule the scheduled event. */
    public var scheduledStartTime: Instant,
    /** The [entity type][ScheduledEntityType] of the scheduled event. */
    public var entityType: ScheduledEntityType,
) : AuditRequestBuilder<GuildScheduledEventCreateRequest> {
    override var reason: String? = null

    private var _channelId: OptionalSnowflake = OptionalSnowflake.Missing

    /** The channel id of the scheduled event. */
    public var channelId: Snowflake? by ::_channelId.delegate()

    private var _description: Optional<String> = Optional.Missing()

    /** The description of the scheduled event. */
    public var description: String? by ::_description.delegate()

    private var _entityMetadata: Optional<GuildScheduledEventEntityMetadata> = Optional.Missing()

    /** The [entity metadata][GuildScheduledEventEntityMetadata] of the scheduled event. */
    public var entityMetadata: GuildScheduledEventEntityMetadata? by ::_entityMetadata.delegate()

    private var _scheduledEndTime: Optional<Instant> = Optional.Missing()

    /** The [Instant] when the scheduled event is scheduled to end. */
    public var scheduledEndTime: Instant? by ::_scheduledEndTime.delegate()

    private var _image: Optional<Image> = Optional.Missing()

    /** The cover image of the scheduled event. */
    public var image: Image? by ::_image.delegate()

    override fun toRequest(): GuildScheduledEventCreateRequest = GuildScheduledEventCreateRequest(
        channelId = _channelId,
        entityMetadata = _entityMetadata,
        name = name,
        privacyLevel = privacyLevel,
        scheduledStartTime = scheduledStartTime,
        scheduledEndTime = _scheduledEndTime,
        description = _description,
        entityType = entityType,
        image = _image.map { it.dataUri },
    )
}
