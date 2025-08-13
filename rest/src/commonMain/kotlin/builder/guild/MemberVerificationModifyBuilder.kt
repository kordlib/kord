package dev.kord.rest.builder.guild

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordMemberVerificationFormField
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildMemberVerificationModifyRequest

@KordDsl
public class MemberVerificationModifyBuilder : AuditRequestBuilder<GuildMemberVerificationModifyRequest>{
    override var reason: String? = null

    private var _enabled: OptionalBoolean = OptionalBoolean.Missing
    public var enabled: Boolean? by ::_enabled.delegate()

    private var _formFields: Optional<List<DiscordMemberVerificationFormField>> = Optional.Missing()
    public var formFields: List<DiscordMemberVerificationFormField>? by ::_formFields.delegate()

    private var _description: Optional<String?> = Optional.Missing()
    public var description: String? by ::_description.delegate()

    override fun toRequest(): GuildMemberVerificationModifyRequest =
        GuildMemberVerificationModifyRequest(_enabled, _formFields, _description)
}