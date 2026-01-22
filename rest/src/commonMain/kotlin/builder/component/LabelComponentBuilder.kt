package dev.kord.rest.builder.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class LabelComponentBuilder(public val label: String) : ContainerComponentBuilder {
    private var _description: Optional<String> = Optional.Missing()

    /** An optional description text for the label; max 100 characters. */
    public var description: String? by ::_description.delegate()

    public val component: MutableList<ContainerComponentBuilder> = mutableListOf()

    /**
     * Adds a String select menu to the label, configured by the [builder]
     */
    public inline fun stringSelect(customId: String, builder: StringSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component.add(StringSelectBuilder(customId).apply(builder))
    }

    /**
     * Adds a String select menu to the label, configured by the [builder]
     */
    public inline fun userSelect(customId: String, builder: UserSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component.add(UserSelectBuilder(customId).apply(builder))
    }

    /**
     * Adds a String select menu to the label, configured by the [builder]
     */
    public inline fun roleSelect(customId: String, builder: RoleSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component.add(RoleSelectBuilder(customId).apply(builder))
    }

    /**
     * Adds a String select menu to the label, configured by the [builder]
     */
    public inline fun mentionableSelect(customId: String, builder: MentionableSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component.add(MentionableSelectBuilder(customId).apply(builder))
    }

    /**
     * Adds a String select menu to the label, configured by the [builder]
     */
    public inline fun channelSelect(customId: String, builder: ChannelSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component.add(ChannelSelectBuilder(customId).apply(builder))
    }

    /**
     * Adds a String select menu to the label, configured by the [builder]
     */
    public inline fun textDisplay(builder: TextDisplayBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component.add(TextDisplayBuilder().apply(builder))
    }

    /**
     * Adds a String select menu to the label, configured by the [builder]
     */
    public inline fun fileUpload(customId: String, builder: FileUploadBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        component.add(FileUploadBuilder(customId).apply(builder))
    }


    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.Label,
        label = Optional(label),
        description = _description,
        components = Optional(component.map { it.build() })
    )
}