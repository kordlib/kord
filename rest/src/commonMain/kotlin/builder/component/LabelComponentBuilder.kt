package dev.kord.rest.builder.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.DiscordModalComponent
import dev.kord.common.entity.TextInputStyle
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class LabelComponentBuilder(public val label: String) : ContainerComponentBuilder {
    private var _description: Optional<String> = Optional.Missing()

    /** An optional description text for the label; max 100 characters. */
    public var description: String? by ::_description.delegate()

    private var _component: Optional<ContainerComponentBuilder> = Optional.Missing()

    public var component: ContainerComponentBuilder? by ::_component.delegate()

    /**
     * Adds a Text input to the label, configured by the [builder]
     */
    public inline fun textInput(style: TextInputStyle, customId: String, builder: TextInputBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = TextInputBuilder(style, customId).apply(builder)
    }

    /**
     * Adds a String select menu to the label, configured by the [builder]
     */
    public inline fun stringSelect(customId: String, builder: StringSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = StringSelectBuilder(customId).apply(builder)
    }

    /**
     * Adds a User select menu to the label, configured by the [builder]
     */
    public inline fun userSelect(customId: String, builder: UserSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = UserSelectBuilder(customId).apply(builder)
    }

    /**
     * Adds a Role select menu to the label, configured by the [builder]
     */
    public inline fun roleSelect(customId: String, builder: RoleSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = RoleSelectBuilder(customId).apply(builder)
    }

    /**
     * Adds a Mentionable select menu to the label, configured by the [builder]
     */
    public inline fun mentionableSelect(customId: String, builder: MentionableSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = MentionableSelectBuilder(customId).apply(builder)
    }

    /**
     * Adds a Channel select menu to the label, configured by the [builder]
     */
    public inline fun channelSelect(customId: String, builder: ChannelSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = ChannelSelectBuilder(customId).apply(builder)
    }

    /**
     * Adds a file upload to the label, configured by the [builder]
     */
    public inline fun fileUpload(customId: String, builder: FileUploadBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = FileUploadBuilder(customId).apply(builder)
    }

    /**
     * Adds a radio group to the label, configured by the [builder]
     */
    public inline fun radioGroup(customId: String, builder: RadioGroupBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = RadioGroupBuilder(customId).apply(builder)
    }

    /**
     * Adds a checkbox group to the label, configured by the [builder]
     */
    public inline fun checkboxGroup(customId: String, builder: CheckboxGroupBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = CheckboxGroupBuilder(customId).apply(builder)
    }

    /**
     * Adds a checkbox to the label, configured by the [builder]
     */
    public inline fun checkbox(customId: String, builder: CheckboxBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component = CheckboxBuilder(customId).apply(builder)
    }

    override fun build(): DiscordComponent = DiscordModalComponent(
        type = ComponentType.Label,
        label = Optional(label),
        description = _description,
        component = _component.map { it.build() }
    )
}