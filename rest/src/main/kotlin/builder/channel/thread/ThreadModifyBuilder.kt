package dev.kord.rest.builder.channel.thread

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.ThreadModifyPatchRequest

class ThreadModifyBuilder : AuditRequestBuilder<ThreadModifyPatchRequest> {

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _archived: OptionalBoolean = OptionalBoolean.Missing
    var archived: Boolean? by ::_archived.delegate()

    private var _locked: OptionalBoolean = OptionalBoolean.Missing
    var locked: Boolean? = null

    private var _rateLimitPerUser: OptionalInt = OptionalInt.Missing
    var rateLimitPerUser: Int? by ::_rateLimitPerUser.delegate()

    private var _autoArchiveDuration: OptionalInt = OptionalInt.Missing
    var autoArchiveDuration: Int? by ::_autoArchiveDuration.delegate()

    override fun toRequest(): ThreadModifyPatchRequest {
        return ThreadModifyPatchRequest(
            name = _name,
            locked = _locked,
            archived = _archived,
            autoArchiveDuration = _autoArchiveDuration,
            ratelimitPerUser = _rateLimitPerUser
        )
    }

    override var reason: String? = null
}