package dev.kord.rest.builder.ban

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildBanCreateRequest

@KordDsl
public class BanCreateBuilder : AuditRequestBuilder<GuildBanCreateRequest> {

    private var _reason: Optional<String> = Optional.Missing()

    /**
     * The reason for banning this member.
     */
    override var reason: String? by ::_reason.delegate()

    private var _deleteMessagesDays: OptionalInt = OptionalInt.Missing

    /**
     * The number of days to delete messages for (0-7).
     */
    public var deleteMessagesDays: Int? by ::_deleteMessagesDays.delegate()

    override fun toRequest(): GuildBanCreateRequest = GuildBanCreateRequest(_reason, _deleteMessagesDays)
}
