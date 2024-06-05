package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Choice
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Suppress("PropertyName")
@KordDsl
public sealed class OptionsBuilder(
    override var name: String,
    override var description: String,
    public val type: ApplicationCommandOptionType,
) : LocalizedNameCreateBuilder, LocalizedDescriptionCreateBuilder, RequestBuilder<ApplicationCommandOption> {
    internal var _default: OptionalBoolean = OptionalBoolean.Missing
    public var default: Boolean? by ::_default.delegate()
    internal var _nameLocalizations: Optional<MutableMap<Locale, String>?> = Optional.Missing()
    override var nameLocalizations: MutableMap<Locale, String>? by ::_nameLocalizations.delegate()
    internal var _descriptionLocalizations: Optional<MutableMap<Locale, String>?> = Optional.Missing()
    override var descriptionLocalizations: MutableMap<Locale, String>? by ::_descriptionLocalizations.delegate()

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
        _nameLocalizations,
        description,
        _descriptionLocalizations,
        _default,
        _required,
        autocomplete = _autocomplete
    )
}

@KordDsl
public sealed class BaseChoiceBuilder<T, C : Choice>(
    name: String,
    description: String,
    type: ApplicationCommandOptionType
) : OptionsBuilder(name, description, type) {
    @Suppress("PropertyName")
    internal var _choices: Optional<MutableList<C>> = Optional.Missing()
    public var choices: MutableList<C>? by ::_choices.delegate()

    public abstract fun choice(name: String, value: T, nameLocalizations: Optional<Map<Locale, String>?> = Optional.Missing())

    /**
     * Registers a new choice with [name] representing value and applies [localizationsBuilder] to it
     *
     * @see ChoiceLocalizationsBuilder
     */
    public inline fun choice(name: String, value: T, localizationsBuilder: ChoiceLocalizationsBuilder.() -> Unit) {
        val builder = ChoiceLocalizationsBuilder(name).apply(localizationsBuilder)
        choice(builder.name, value, builder._nameLocalizations)
    }

    override fun toRequest(): ApplicationCommandOption = ApplicationCommandOption(
        type,
        name,
        _nameLocalizations,
        description,
        _descriptionLocalizations,
        choices = _choices,
        required = _required,
        default = _default,
        autocomplete = _autocomplete,
    )
}

/**
 * Builder to register name localizations for a choice.
 *
 * @see LocalizedNameCreateBuilder
 */
@KordDsl
public class ChoiceLocalizationsBuilder(override var name: String) : LocalizedNameCreateBuilder {
    @Suppress("PropertyName")
    @PublishedApi
    internal var _nameLocalizations: Optional<MutableMap<Locale, String>?> = Optional.Missing()
    override var nameLocalizations: MutableMap<Locale, String>? by ::_nameLocalizations.delegate()
}

/**
 * Builder for numeric options.
 */
@KordDsl
public sealed class NumericOptionBuilder<T : Number, C : Choice>(
    name: String,
    description: String,
    type: ApplicationCommandOptionType
) : BaseChoiceBuilder<T, C>(name, description, type) {

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
        type = type,
        name = name,
        nameLocalizations = _nameLocalizations,
        description = description,
        descriptionLocalizations = _descriptionLocalizations,
        choices = _choices,
        required = _required,
        default = _default,
        autocomplete = _autocomplete,
        minValue = _minValue.map { JsonPrimitive(it) },
        maxValue = _maxValue.map { JsonPrimitive(it) },
    )
}

@KordDsl
public class IntegerOptionBuilder(name: String, description: String) :
    NumericOptionBuilder<Long, Choice.IntegerChoice>(name, description, ApplicationCommandOptionType.Integer) {

    override fun choice(name: String, value: Long, nameLocalizations: Optional<Map<Locale, String>?>) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.IntegerChoice(name, nameLocalizations, value))
    }
}

@KordDsl
public class NumberOptionBuilder(name: String, description: String) :
    NumericOptionBuilder<Double, Choice.NumberChoice>(name, description, ApplicationCommandOptionType.Number) {

    override fun choice(name: String, value: Double, nameLocalizations: Optional<Map<Locale, String>?>) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.NumberChoice(name, nameLocalizations, value))
    }

}

@KordDsl
public class StringChoiceBuilder(name: String, description: String) :
    BaseChoiceBuilder<String, Choice.StringChoice>(name, description, ApplicationCommandOptionType.String) {

    private var _minLength: OptionalInt = OptionalInt.Missing

    /**
     * The minimum allowed string length (minimum of `0`, maximum of `6000`).
     */
    public var minLength: Int? by ::_minLength.delegate()

    private var _maxLength: OptionalInt = OptionalInt.Missing

    /**
     * The maximum allowed string length (minimum of `1`, maximum of `6000`).
     */
    public var maxLength: Int? by ::_maxLength.delegate()

    override fun choice(name: String, value: String, nameLocalizations: Optional<Map<Locale, String>?>) {
        if (choices == null) choices = mutableListOf()
        choices!!.add(Choice.StringChoice(name, nameLocalizations, value))
    }

    override fun toRequest(): ApplicationCommandOption = ApplicationCommandOption(
        type = type,
        name = name,
        nameLocalizations = _nameLocalizations,
        description = description,
        descriptionLocalizations = _descriptionLocalizations,
        choices = _choices,
        required = _required,
        default = _default,
        autocomplete = _autocomplete,
        minLength = _minLength,
        maxLength = _maxLength
    )
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
        _nameLocalizations,
        description,
        _descriptionLocalizations,
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
        _nameLocalizations,
        description,
        _descriptionLocalizations,
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
