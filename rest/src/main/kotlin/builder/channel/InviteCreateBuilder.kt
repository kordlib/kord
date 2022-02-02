package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TargetUserType
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.InviteCreateRequest

@KordDsl
public class InviteCreateBuilder : AuditRequestBuilder<InviteCreateRequest> {

    private var _age: OptionalInt = OptionalInt.Missing

    /**
     * The duration of invite in seconds before expiry, or 0 for never. 86400 (24 hours) by default.
     */
    public var age: Int? by ::_age.delegate()

    private var _uses: OptionalInt = OptionalInt.Missing

    /**
     * The maximum number of uses, or 0 for unlimited. 0 by default.
     */
    public var uses: Int? by ::_uses.delegate()

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
    public var targetUser: Snowflake? by ::_targetUser.delegate()

    private var _reason: Optional<String> = Optional.Missing()
    override var reason: String? by ::_reason.delegate()

    override fun toRequest(): InviteCreateRequest = InviteCreateRequest(
        temporary = _temporary,
        age = _age,
        unique = _unique,
        uses = _uses,
        targetUser = _targetUser,
        targetUserType = _targetUser.map { TargetUserType.Stream }
    )
}
