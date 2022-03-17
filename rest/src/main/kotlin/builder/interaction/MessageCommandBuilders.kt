package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest


@KordDsl
public class MessageCommandModifyBuilder : ApplicationCommandModifyBuilder {

    private val state = ApplicationCommandModifyStateHolder()

    override var name: String? by state::name.delegate()
    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()

    override var defaultPermission: Boolean? by state::defaultPermission.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest {
        return ApplicationCommandModifyRequest(
            name = state.name,
            defaultPermission = state.defaultPermission
        )

    }

}

@KordDsl
public class MessageCommandCreateBuilder(override var name: String) : ApplicationCommandCreateBuilder {
    override val type: ApplicationCommandType
        get() = ApplicationCommandType.Message


    private val state = ApplicationCommandModifyStateHolder()

    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()

    override var defaultPermission: Boolean? by state::defaultPermission.delegate()

    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(
            name = name,
            type = type,
            defaultPermission = state.defaultPermission
        )
    }
}
