package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationFlag
import dev.kord.common.entity.InviteTargetType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.InviteCreateRequest
import kotlin.time.Duration
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.toDuration

@KordDsl
public class InviteCreateBuilder : AuditRequestBuilder<InviteCreateRequest> {
    override var reason: String? = null

    private var _maxAge: Optional<Duration> = Optional.Missing()

    /**
     * The duration of invite in seconds before expiry, or 0 for never. 86400 (24 hours) by default.
     */
    @Deprecated("'age' was renamed to 'maxAge'", ReplaceWith("this.maxAge"))
    public var age: Int?
        get() = _maxAge.value?.inWholeSeconds?.toInt()
        set(value) {
            _maxAge = value?.toDuration(unit = SECONDS)?.optional() ?: Optional.Missing()
        }

    /**
     * The duration before invite expiry, or 0 for never. Between 0 and 604800 seconds (7 days). 24 hours by default.
     */
    public var maxAge: Duration? by ::_maxAge.delegate()

    private var _maxUses: OptionalInt = OptionalInt.Missing

    /**
     * The maximum number of uses, or 0 for unlimited. 0 by default.
     */
    @Deprecated("'uses' was renamed to 'maxUses'", ReplaceWith("this.maxUses"))
    public var uses: Int? by ::_maxUses.delegate()

    /** The maximum number of uses, or 0 for unlimited. Between 0 and 100. 0 by default. */
    public var maxUses: Int? by ::_maxUses.delegate()

    private var _temporary: OptionalBoolean = OptionalBoolean.Missing

    /**
     * 	Whether this invite only grants temporary membership. False by default.
     */
    public var temporary: Boolean? by ::_temporary.delegate()

    private var _unique: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Whether to reuse a similar invite (useful for creating many unique one time use invites). False by default.
     */
    public var unique: Boolean? by ::_unique.delegate()

    private var _targetUser: OptionalSnowflake = OptionalSnowflake.Missing

    /**
     * The target user id for this invite.
     */
    @Deprecated("This is no longer documented. Use 'targetUserId' instead.", ReplaceWith("this.targetUserId"))
    public var targetUser: Snowflake? by ::_targetUser.delegate()

    private var _targetType: Optional<InviteTargetType> = Optional.Missing()

    /**
     * The [type of target][InviteTargetType] for this voice channel invite.
     *
     * Will default to [Stream][InviteTargetType.Stream] if only [targetUserId] but not [targetApplicationId] is
     * provided.
     *
     * Will default to [EmbeddedApplication][InviteTargetType.EmbeddedApplication] if only [targetApplicationId] but not
     * [targetUserId] is provided.
     */
    public var targetType: InviteTargetType? by ::_targetType.delegate()

    private var _targetUserId: OptionalSnowflake = OptionalSnowflake.Missing

    /**
     * The id of the user whose stream to display for this invite, the user must be streaming in the channel.
     */
    public var targetUserId: Snowflake? by ::_targetUserId.delegate()

    private var _targetApplicationId: OptionalSnowflake = OptionalSnowflake.Missing

    /**
     * The id of the embedded application to open for this invite, the application must have the
     * [Embedded][ApplicationFlag.Embedded] flag.
     */
    public var targetApplicationId: Snowflake? by ::_targetApplicationId.delegate()

    override fun toRequest(): InviteCreateRequest {
        val target = _targetType.switchOnMissing(
            when {
                _targetUserId.isPresent && _targetApplicationId.isMissing ->
                    InviteTargetType.Stream.optional()
                _targetUserId.isMissing && _targetApplicationId.isPresent ->
                    InviteTargetType.EmbeddedApplication.optional()
                else -> Optional.Missing() // both missing or both present, we should not decide in this case
            }
        )

        return InviteCreateRequest(
            maxAge = _maxAge,
            maxUses = _maxUses,
            temporary = _temporary,
            unique = _unique,
            targetUser = _targetUser,
            targetUserType = _targetUser.map { @Suppress("DEPRECATION") dev.kord.common.entity.TargetUserType.Stream },
            targetType = target,
            targetUserId = _targetUserId,
            targetApplicationId = _targetApplicationId,
        )
    }
}

private inline val OptionalSnowflake.isMissing get() = this == OptionalSnowflake.Missing
private inline val OptionalSnowflake.isPresent get() = this != OptionalSnowflake.Missing
