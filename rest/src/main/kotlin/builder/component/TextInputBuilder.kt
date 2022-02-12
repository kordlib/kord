package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.DiscordTextInputComponent
import dev.kord.common.entity.TextInputStyle
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate

/**
 * A builder for a [Discord Text Input](https://discord.com/developers/docs/interactions/message-components#text-inputs).
 *
 * @param customId The identifier for the menu, max 100 characters.
 */
@KordDsl
public class TextInputBuilder(
    public var style: TextInputStyle,
    public var customId: String,
    public var label: String,
) : ActionRowComponentBuilder() {
    /**
     * The range of values that can be accepted. Accepts any range between [0,4000].
     */
    public var allowedLength: IntRange? = null

    private var _placeholder: Optional<String> = Optional.Missing()

    /**
     * Custom placeholder if no value is selected, max 100 characters.
     */
    public var placeholder: String? by ::_placeholder.delegate()

    private var _value: Optional<String> = Optional.Missing()

    /**
     * A pre-filled value for this component, max 4000 characters.
     */
    public var value: String? by ::_value.delegate()

    private var _required: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Whether this component is required to be filled, default false.
     */
    public var required: Boolean? by ::_required.delegate()

    override fun build(): DiscordComponent {
        return DiscordTextInputComponent(
            type = ComponentType.TextInput,
            style = Optional(style),
            customId = Optional(customId),
            label = Optional(label),
            minLength = allowedLength?.let { OptionalInt.Value(it.first) } ?: OptionalInt.Missing,
            maxLength = allowedLength?.let { OptionalInt.Value(it.last) } ?: OptionalInt.Missing,
            placeholder = _placeholder,
            value = _value,
            required = _required,
        )
    }
}