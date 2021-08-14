package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
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

@KordDsl

sealed class OptionsBuilder(
    var name: String,
    var description: String,
    val type: ApplicationCommandOptionType,
) :
    RequestBuilder<ApplicationCommandOption> {
    internal var _default: OptionalBoolean = OptionalBoolean.Missing
    var default: Boolean? by ::_default.delegate()

    internal var _required: OptionalBoolean = OptionalBoolean.Missing
    var required: Boolean? by ::_required.delegate()

    override fun toRequest() = ApplicationCommandOption(
        type,
        name,
        description,
        _default,
        _required
    )
}

@KordDsl

sealed class BaseChoiceBuilder<T>(
    name: String,
    description: String,
    type: ApplicationCommandOptionType
) :
    OptionsBuilder(name, description, type) {
    private var _choices: Optional<MutableList<Choice<*>>> = Optional.Missing()
    var choices: MutableList<Choice<*>>? by ::_choices.delegate()

    abstract fun choice(name: String, value: T)

    override fun toRequest() = ApplicationCommandOption(
        type,
        name,
        description,
        choices = _choices,
        required = _required,
        default = _default
    )
}

@KordDsl

class IntChoiceBuilder(name: String, description: String) :
    BaseChoiceBuilder<Int>(name, description, ApplicationCommandOptionType.Integer) {

    override fun choice(name: String, value: Int) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.IntChoice(name, value))
    }
}


@KordDsl
class NumberChoiceBuilder(name: String, description: String) :
    BaseChoiceBuilder<Double>(name, description, ApplicationCommandOptionType.Number) {
    override fun choice(name: String, value: Double) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.NumberChoice(name, value))
    }

}

@KordDsl

class StringChoiceBuilder(name: String, description: String) :
    BaseChoiceBuilder<String>(name, description, ApplicationCommandOptionType.String) {

    override fun choice(name: String, value: String) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.StringChoice(name, value))
    }
}

@KordDsl

class BooleanBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Boolean)

@KordDsl

class UserBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.User)

@KordDsl

class RoleBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Role)

@KordDsl

class ChannelBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Channel)


class MentionableBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Mentionable)

@KordDsl

sealed class BaseCommandOptionBuilder(
    name: String,
    description: String,
    type: ApplicationCommandOptionType,
) :
    OptionsBuilder(name, description, type) {
    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    var options by ::_options.delegate()

    override fun toRequest() = ApplicationCommandOption(
        type,
        name,
        description,
        options = _options.mapList { it.toRequest() }
    )
}

@KordDsl

class SubCommandBuilder(name: String, description: String) :
    BaseCommandOptionBuilder(name, description, ApplicationCommandOptionType.SubCommand), BaseInputChatBuilder

@KordDsl

class GroupCommandBuilder(name: String, description: String) :
    BaseCommandOptionBuilder(name, description, ApplicationCommandOptionType.SubCommandGroup) {
    inline fun subCommand(
        name: String,
        description: String,
        builder: SubCommandBuilder.() -> Unit,
    ) {
        if (options == null) options = mutableListOf()
        options!!.add(SubCommandBuilder(name, description).apply(builder))
    }
}