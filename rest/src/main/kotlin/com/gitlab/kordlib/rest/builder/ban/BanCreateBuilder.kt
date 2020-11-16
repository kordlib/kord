package com.gitlab.kordlib.rest.builder.ban

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalInt
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.rest.json.request.GuildBanCreateRequest

@KordDsl
class BanCreateBuilder : AuditRequestBuilder<GuildBanCreateRequest> {

    private var _reason: Optional<String> = Optional.Missing()

    /**
     * The reason for banning this member.
     */
    override var reason: String? by ::_reason.delegate()

    private var _deleteMessagesDays: OptionalInt = OptionalInt.Missing

    /**
     * The number of days to delete messages for (0-7).
     */
    var deleteMessagesDays: Int? by ::_deleteMessagesDays.delegate()

    override fun toRequest() = GuildBanCreateRequest(_reason, _deleteMessagesDays)
}