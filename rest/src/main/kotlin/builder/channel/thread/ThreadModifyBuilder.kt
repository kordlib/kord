package dev.kord.rest.builder.channel.thread

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.ChannelModifyPatchRequest

class ThreadModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _archived: OptionalBoolean = OptionalBoolean.Missing
    var archived: Boolean? by ::_archived.delegate()

    private var _locked: OptionalBoolean = OptionalBoolean.Missing
    var locked: Boolean? by ::_locked.delegate()

    private var _rateLimitPerUser: OptionalInt? = OptionalInt.Missing
    var rateLimitPerUser: Int? by ::_rateLimitPerUser.delegate()

    private var _autoArchiveDuration: OptionalInt = OptionalInt.Missing
    var autoArchiveDuration: Int? by ::_autoArchiveDuration.delegate()

    private var _invitable: OptionalBoolean = OptionalBoolean.Missing
    var invitable: Boolean? by ::_invitable.delegate()

    override fun toRequest(): ChannelModifyPatchRequest {
        return ChannelModifyPatchRequest(
            name = _name,
            locked = _locked,
            archived = _archived,
            autoArchiveDuration = _autoArchiveDuration,
            rateLimitPerUser = _rateLimitPerUser,
            invitable = _invitable
        )
    }

    override var reason: String? = null
}