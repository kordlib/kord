package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
sealed class OptionsBuilder(val name: String, val description: String, val type: ApplicationCommandOptionType) :
    RequestBuilder<ApplicationCommandOption> {
    internal var _default: OptionalBoolean = OptionalBoolean.Missing
    var default: Boolean? by ::_default.delegate()

    internal var _required: OptionalBoolean = OptionalBoolean.Missing
    var required: Boolean? by ::_required.delegate()

    override fun toRequest(): ApplicationCommandOption {
        return ApplicationCommandOption(type, name, description, _default, _required)
    }
}

@KordPreview
sealed class BaseChoiceBuilder<T>(name: String, description: String, type: ApplicationCommandOptionType) :
    OptionsBuilder(name, description, type) {
    private var _choices: Optional<MutableList<Choice<*>>> = Optional.Missing()
    var choices: MutableList<Choice<*>>? by ::_choices.delegate()

    abstract fun choice(name: String, value: () -> T)

    override fun toRequest(): ApplicationCommandOption {
        return ApplicationCommandOption(type,
            name,
            description,
            choices = _choices,
            required = _required,
            default = _default)
    }

}

@KordPreview
class IntChoiceBuilder(name: String, description: String) :
    BaseChoiceBuilder<Int>(name, description, ApplicationCommandOptionType.Integer) {

    override fun choice(name: String, value: () -> Int) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.IntChoice(name, value()))
    }
}

@KordPreview
class StringChoiceBuilder(name: String, description: String) :
    BaseChoiceBuilder<String>(name, description, ApplicationCommandOptionType.String) {

    override fun choice(name: String, value: () -> String) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.StringChoice(name, value()))
    }
}

@KordPreview
class BooleanBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Boolean)

@KordPreview
class UserBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.User)

@KordPreview
class RoleBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Role)

@KordPreview
class ChannelBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Channel)

@KordPreview
sealed class BaseCommandOptionBuilder(name: String, description: String, type: ApplicationCommandOptionType) :
    OptionsBuilder(name, description, type) {
    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    protected var options by ::_options.delegate()

    override fun toRequest(): ApplicationCommandOption {
        return ApplicationCommandOption(type, name, description, options = _options.mapList { it.toRequest() })
    }
}

@KordPreview
class SubCommandBuilder(name: String, description: String) :
    BaseCommandOptionBuilder(name, description, ApplicationCommandOptionType.SubCommand) {
    @OptIn(ExperimentalContracts::class)
    fun boolean(name: String, description: String, builder: BooleanBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(BooleanBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    fun int(name: String, description: String, builder: IntChoiceBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(IntChoiceBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    fun string(name: String, description: String, builder: StringChoiceBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(StringChoiceBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    fun role(name: String, description: String, builder: RoleBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(RoleBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    fun user(name: String, description: String, builder: UserBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(UserBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    fun channel(name: String, description: String, builder: ChannelBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(ChannelBuilder(name, description).apply(builder))
    }
}

@KordPreview
class GroupCommandBuilder(name: String, description: String) :
    BaseCommandOptionBuilder(name, description, ApplicationCommandOptionType.SubCommandGroup) {
    fun subCommand(name: String, description: String, builder: SubCommandBuilder.() -> Unit) {
        if (options == null) options = mutableListOf()
        options!!.add(SubCommandBuilder(name, description).apply(builder))
    }
}