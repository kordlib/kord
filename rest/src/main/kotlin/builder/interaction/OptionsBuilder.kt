package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public sealed class OptionsBuilder(
    public var name: String,
    public var description: String,
    public val type: ApplicationCommandOptionType,
) : RequestBuilder<ApplicationCommandOption> {
    internal var _default: OptionalBoolean = OptionalBoolean.Missing
    public var default: Boolean? by ::_default.delegate()

    internal var _required: OptionalBoolean = OptionalBoolean.Missing
    public var required: Boolean? by ::_required.delegate()

    internal var _autocomplete: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Setting this to `true` allows you to dynamically respond with your choices, depending on the user input.
     *
     * This disables all input validation, users can submit values before responding to the AutoComplete request.
     *
     * Enabling this also means that you cannot add any other option.
     */
    public var autocomplete: Boolean? by ::_autocomplete.delegate()

    override fun toRequest(): ApplicationCommandOption = ApplicationCommandOption(
        type,
        name,
        description,
        _default,
        _required,
        autocomplete = _autocomplete
    )
}

@KordDsl
public sealed class BaseChoiceBuilder<T>(
    name: String,
    description: String,
    type: ApplicationCommandOptionType
) : OptionsBuilder(name, description, type) {
    // TODO We can change these types to Optional<MutableList<Choice<T>>> and MutableList<Choice<T>> once
    //  https://youtrack.jetbrains.com/issue/KT-51045 is fixed.
    //  The bug from that issue prevents you from setting BaseChoiceBuilder<*>.choices to `null`.
    protected var _choices: Optional<MutableList<Choice<*>>> = Optional.Missing()
    public var choices: MutableList<Choice<*>>? by ::_choices.delegate()

    public abstract fun choice(name: String, value: T)

    override fun toRequest(): ApplicationCommandOption = ApplicationCommandOption(
        type,
        name,
        description,
        choices = _choices,
        required = _required,
        default = _default,
        autocomplete = _autocomplete,
    )
}

/**
 * Builder for numeric options.
 */
@KordDsl
public sealed class NumericOptionBuilder<T : Number>(
    name: String,
    description: String,
    type: ApplicationCommandOptionType
) : BaseChoiceBuilder<T>(name, description, type) {

    private var _minValue: Optional<T> = Optional.Missing()

    /**
     * The minimum value permitted.
     */
    public var minValue: T? by ::_minValue.delegate()

    private var _maxValue: Optional<T> = Optional.Missing()

    /**
     * The maximum value permitted.
     */
    public var maxValue: T? by ::_maxValue.delegate()

    override fun toRequest(): ApplicationCommandOption = ApplicationCommandOption(
        type,
        name,
        description,
        choices = _choices,
        required = _required,
        default = _default,
        autocomplete = _autocomplete,
        minValue = _minValue.map { JsonPrimitive(it) },
        maxValue = _maxValue.map { JsonPrimitive(it) },
    )
}


@Deprecated("Replaced by IntOptionBuilder", ReplaceWith("IntOptionBuilder"), DeprecationLevel.ERROR)
public typealias IntChoiceBuilder = IntOptionBuilder

@KordDsl
public class IntOptionBuilder(name: String, description: String) :
    NumericOptionBuilder<Long>(name, description, ApplicationCommandOptionType.Integer) {

    override fun choice(name: String, value: Long) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.IntChoice(name, value))
    }
}

@Deprecated("Replaced by IntOptionBuilder", ReplaceWith("NumberOptionBuilder"), DeprecationLevel.ERROR)
public typealias NumberChoiceBuilder = NumberOptionBuilder

@KordDsl
public class NumberOptionBuilder(name: String, description: String) :
    NumericOptionBuilder<Double>(name, description, ApplicationCommandOptionType.Number) {
    override fun choice(name: String, value: Double) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.NumberChoice(name, value))
    }

}

@KordDsl
public class StringChoiceBuilder(name: String, description: String) :
    BaseChoiceBuilder<String>(name, description, ApplicationCommandOptionType.String) {

    override fun choice(name: String, value: String) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.StringChoice(name, value))
    }
}

@KordDsl
public class BooleanBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Boolean)

@KordDsl
public class UserBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.User)

@KordDsl
public class RoleBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Role)

@KordDsl
public class ChannelBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Channel) {
    private var _channelTypes: Optional<List<ChannelType>> = Optional.Missing()
    public var channelTypes: List<ChannelType>? by ::_channelTypes.delegate()
    override fun toRequest(): ApplicationCommandOption = ApplicationCommandOption(
        type,
        name,
        description,
        _default,
        _required,
        autocomplete = _autocomplete,
        channelTypes = _channelTypes
    )
}

@KordDsl
public class MentionableBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Mentionable)

@KordDsl
public class AttachmentBuilder(name: String, description: String) :
    OptionsBuilder(name, description, ApplicationCommandOptionType.Attachment)

@KordDsl
public sealed class BaseCommandOptionBuilder(
    name: String,
    description: String,
    type: ApplicationCommandOptionType,
) : OptionsBuilder(name, description, type) {

    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    public var options: MutableList<OptionsBuilder>? by ::_options.delegate()

    override fun toRequest(): ApplicationCommandOption = ApplicationCommandOption(
        type,
        name,
        description,
        options = _options.mapList { it.toRequest() }
    )
}

@KordDsl
public class SubCommandBuilder(name: String, description: String) :
    BaseCommandOptionBuilder(name, description, ApplicationCommandOptionType.SubCommand), BaseInputChatBuilder

@KordDsl
public class GroupCommandBuilder(name: String, description: String) :
    BaseCommandOptionBuilder(name, description, ApplicationCommandOptionType.SubCommandGroup) {

    public inline fun subCommand(
        name: String,
        description: String,
        builder: SubCommandBuilder.() -> Unit,
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(SubCommandBuilder(name, description).apply(builder))
    }
}
