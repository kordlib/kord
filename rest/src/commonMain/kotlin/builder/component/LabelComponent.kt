package dev.kord.rest.builder.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate

public class LabelComponent(public val label: String) : ContainerComponentBuilder {
    private var _description: Optional<String> = Optional.Missing()

    /** An optional description text for the label; max 100 characters. */
    public var description: String? by ::_description.delegate()

    public val component: MutableList<ContainerComponentBuilder> = mutableListOf()


    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.Label,
        label = Optional(label),
        description = _description,
        components = Optional(component.map { it.build() })
    )
}