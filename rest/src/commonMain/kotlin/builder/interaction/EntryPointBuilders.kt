package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.ApplicationIntegrationType
import dev.kord.common.entity.EntryPointCommandHandlerType
import dev.kord.common.entity.InteractionContextType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.Optional.Companion.missingOnEmpty
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest

@KordDsl
public interface EntryPointCreateBuilder : ApplicationCommandCreateBuilder, LocalizedDescriptionCreateBuilder {
    public val handler: EntryPointCommandHandlerType?
}

@KordDsl
public interface GlobalEntryPointCreateBuilder : GlobalApplicationCommandCreateBuilder, EntryPointCreateBuilder

@KordDsl
public interface EntryPointModifyBuilder : ApplicationCommandModifyBuilder, LocalizedDescriptionModifyBuilder {
    public val handler: EntryPointCommandHandlerType?
}

@KordDsl
public interface GlobalEntryPointModifyBuilder : GlobalApplicationCommandModifyBuilder, EntryPointModifyBuilder

@PublishedApi
internal class EntryPointCreateBuilderImpl(
    override var name: String,
    override var description: String,
    override val handler: EntryPointCommandHandlerType
) : GlobalEntryPointCreateBuilder {
    private val state = ApplicationCommandModifyStateHolder()

    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()
    override var descriptionLocalizations: MutableMap<Locale, String>? by state::descriptionLocalizations.delegate()

    override val type: ApplicationCommandType
        get() = ApplicationCommandType.PrimaryEntryPoint

    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()

    @Suppress("OVERRIDE_DEPRECATION")
    override var dmPermission: Boolean? by state::dmPermission.delegate()
    override var integrationTypes: MutableList<ApplicationIntegrationType>? by state::integrationTypes.delegate()
    override var contexts: MutableList<InteractionContextType>? by state::contexts.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    override var defaultPermission: Boolean? by @Suppress("DEPRECATION") state::defaultPermission.delegate()

    override var nsfw: Boolean? by state::nsfw.delegate()

    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(
            name,
            state.nameLocalizations,
            type,
            Optional.Value(description),
            state.descriptionLocalizations,
            state.options.mapList { it.toRequest() },
            state.defaultMemberPermissions,
            state.dmPermission,
            @Suppress("DEPRECATION") state.defaultPermission,
            nsfw = state.nsfw,
            integrationTypes = state.integrationTypes.missingOnEmpty(),
            contexts = state.contexts.missingOnEmpty(),
            handler = Optional.Value(handler)
        )
    }
}

@PublishedApi
internal class EntryPointModifyBuilderImpl : GlobalEntryPointModifyBuilder, EntryPointModifyBuilder {
    private val state = ApplicationCommandModifyStateHolder()
    override var name: String? by state::name.delegate()
    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()

    override var description: String? by state::description.delegate()
    override var descriptionLocalizations: MutableMap<Locale, String>? by state::descriptionLocalizations.delegate()

    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()

    @Suppress("OVERRIDE_DEPRECATION")
    override var dmPermission: Boolean? by state::dmPermission.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    override var defaultPermission: Boolean? by @Suppress("DEPRECATION") state::defaultPermission.delegate()

    override var nsfw: Boolean? by state::nsfw.delegate()

    override var integrationTypes: MutableList<ApplicationIntegrationType>? by state::integrationTypes.delegate()
    override var contexts: MutableList<InteractionContextType>? by state::contexts.delegate()
    override val handler: EntryPointCommandHandlerType? by state::handler.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest = ApplicationCommandModifyRequest(
        state.name,
        state.nameLocalizations,
        state.description,
        state.descriptionLocalizations,
        defaultMemberPermissions = state.defaultMemberPermissions,
        dmPermission = state.dmPermission,
        nsfw = state.nsfw,
        integrationTypes = state.integrationTypes.missingOnEmpty(),
        contexts = state.contexts.missingOnEmpty(),
        handler = state.handler
    )
}
