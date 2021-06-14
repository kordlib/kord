package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordGuildApplicationCommandPermission
import dev.kord.common.entity.PartialDiscordGuildApplicationCommandPermissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest
import dev.kord.rest.json.request.ApplicationCommandPermissionsEditRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
@KordDsl
sealed class BaseApplicationBuilder {
    abstract var options: MutableList<OptionsBuilder>?

    @OptIn(ExperimentalContracts::class)
    inline fun boolean(name: String, description: String, builder: BooleanBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(BooleanBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun int(name: String, description: String, builder: IntChoiceBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(IntChoiceBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun string(
        name: String,
        description: String,
        builder: StringChoiceBuilder.() -> Unit = {},
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(StringChoiceBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun group(name: String, description: String, builder: GroupCommandBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(GroupCommandBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun subCommand(
        name: String,
        description: String,
        builder: SubCommandBuilder.() -> Unit = {},
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(SubCommandBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun role(name: String, description: String, builder: RoleBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(RoleBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun user(name: String, description: String, builder: UserBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(UserBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun channel(name: String, description: String, builder: ChannelBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(ChannelBuilder(name, description).apply(builder))
    }
}


@KordPreview
@KordDsl
class ApplicationCommandCreateBuilder(
    var name: String,
    var description: String,
) : RequestBuilder<ApplicationCommandCreateRequest>, BaseApplicationBuilder() {

    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    override var options: MutableList<OptionsBuilder>? by ::_options.delegate()

    private var _defaultPermission: OptionalBoolean = OptionalBoolean.Missing
    var defaultPermission: Boolean? by ::_defaultPermission.delegate()

    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(name,
            description,
            _options.mapList { it.toRequest() }, _defaultPermission)

    }

}


@KordPreview
@KordDsl
class ApplicationCommandsCreateBuilder : RequestBuilder<List<ApplicationCommandCreateRequest>> {
    val commands: MutableList<ApplicationCommandCreateBuilder> = mutableListOf()

    @OptIn(ExperimentalContracts::class)
    inline fun command(
        name: String,
        description: String,
        builder: ApplicationCommandCreateBuilder.() -> Unit
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += ApplicationCommandCreateBuilder(name, description).apply(builder)
    }

    override fun toRequest(): List<ApplicationCommandCreateRequest> {
        return commands.map { it.toRequest() }
    }

}

@KordPreview
@KordDsl
class ApplicationCommandModifyBuilder : BaseApplicationBuilder(),
    RequestBuilder<ApplicationCommandModifyRequest> {

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _description: Optional<String> = Optional.Missing()
    var description: String? by ::_name.delegate()

    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    override var options: MutableList<OptionsBuilder>? by ::_options.delegate()

    private var _defaultPermission: OptionalBoolean = OptionalBoolean.Missing
    var defaultPermission: Boolean? by ::_defaultPermission.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest {
        return ApplicationCommandModifyRequest(_name,
            _description,
            _options.mapList { it.toRequest() }, _defaultPermission)

    }

}


@KordDsl
@KordPreview
class ApplicationCommandPermissionsBulkModifyBuilder :
        RequestBuilder<List<PartialDiscordGuildApplicationCommandPermissions>> {

    @PublishedApi
    internal val permissions = mutableMapOf<Snowflake, ApplicationCommandPermissionsModifyBuilder>()

    @OptIn(ExperimentalContracts::class)
    inline fun command(
            commandId: Snowflake,
            builder: ApplicationCommandPermissionsModifyBuilder.() -> Unit,
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        permissions[commandId] = ApplicationCommandPermissionsModifyBuilder().apply(builder)
    }

    override fun toRequest(): List<PartialDiscordGuildApplicationCommandPermissions> {
        return permissions.map { (id, builder) ->
            PartialDiscordGuildApplicationCommandPermissions(
                    id, builder.permissions.toList()
            )
        }
    }
}

@KordDsl
@KordPreview
class ApplicationCommandPermissionsModifyBuilder :
        RequestBuilder<ApplicationCommandPermissionsEditRequest> {

    var permissions = mutableListOf<DiscordGuildApplicationCommandPermission>()

    fun role(id: Snowflake, allow: Boolean = true) {
        permissions.add(
                DiscordGuildApplicationCommandPermission(
                        id,
                        DiscordGuildApplicationCommandPermission.Type.Role,
                        allow
                )
        )
    }

    fun user(id: Snowflake, allow: Boolean = true) {
        permissions.add(
                DiscordGuildApplicationCommandPermission(
                        id,
                        DiscordGuildApplicationCommandPermission.Type.User,
                        allow
                )
        )
    }

    override fun toRequest(): ApplicationCommandPermissionsEditRequest =
            ApplicationCommandPermissionsEditRequest(permissions)

}
