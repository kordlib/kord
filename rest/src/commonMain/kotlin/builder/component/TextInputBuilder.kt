package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordTextInputComponent
import dev.kord.common.entity.TextInputStyle
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate

/**
 * A builder for a [Discord Text Input](https://discord.com/developers/docs/interactions/message-components#text-inputs).
 *
 * @param style The [style][TextInputStyle] of the input.
 * @param customId The identifier for the input, max 100 characters.
 * @param label The label for this component, max 45 characters.
 */
@KordDsl
public class TextInputBuilder(
    public var style: TextInputStyle,
    public var customId: String,
    public var label: String,
) : ActionRowComponentBuilder() {
    /**
     * The range of lengths that can be accepted. Accepts any range between [0,4000].
     */
    public var allowedLength: ClosedRange<Int>? = null

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
     * Whether this component is required to be filled, default true.
     */
    public var required: Boolean? by ::_required.delegate()

    override fun build(): DiscordTextInputComponent {
        return DiscordTextInputComponent(
            type = ComponentType.TextInput,
            style = Optional(style),
            customId = Optional(customId),
            label = Optional(label),
            minLength = allowedLength?.let { OptionalInt.Value(it.start) } ?: OptionalInt.Missing,
            maxLength = allowedLength?.let { OptionalInt.Value(it.endInclusive) } ?: OptionalInt.Missing,
            placeholder = _placeholder,
            value = _value,
            required = _required,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false

        other as TextInputBuilder

        if (style != other.style) return false
        if (customId != other.customId) return false
        if (label != other.label) return false
        if (allowedLength != other.allowedLength) return false
        if (placeholder != other.placeholder) return false
        if (value != other.value) return false
        if (required != other.required) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + style.hashCode()
        result = 31 * result + customId.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + (allowedLength?.hashCode() ?: 0)
        result = 31 * result + (placeholder?.hashCode() ?: 0)
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (required?.hashCode() ?: 0)
        return result
    }

}
