package dev.kord.rest.builder.guild

import dev.kord.common.entity.GuildScheduledEventEntityMetadata
import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildScheduledEventCreateRequest
import kotlinx.datetime.Instant

public class ScheduledEventCreateBuilder(
    public val name: String,
    public val privacyLevel: StageInstancePrivacyLevel,
    public val scheduledStartTime: Instant,
    public val entityType: ScheduledEntityType,
) : RequestBuilder<GuildScheduledEventCreateRequest> {
    private var _channelId: OptionalSnowflake = OptionalSnowflake.Missing
    public var channelId: Snowflake? by ::_channelId.delegate()

    private var _description: Optional<String> = Optional.Missing()
    public var description: String? by ::_description.delegate()

    private var _entityMetadata: Optional<GuildScheduledEventEntityMetadata> = Optional.Missing()
    public var entityMetadata: GuildScheduledEventEntityMetadata? by ::_entityMetadata.delegate()

    private var _scheduledEndTime: Optional<Instant> = Optional.Missing()
    public var scheduledEndTime: Instant? by ::_scheduledEndTime.delegate()

    override fun toRequest(): GuildScheduledEventCreateRequest = GuildScheduledEventCreateRequest(
        _channelId,
        _entityMetadata,
        name,
        privacyLevel,
        scheduledStartTime,
        _scheduledEndTime,
        _description,
        entityType
    )
}
