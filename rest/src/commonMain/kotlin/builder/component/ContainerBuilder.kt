package dev.kord.rest.builder.component

import dev.kord.common.Color
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate

public class ContainerBuilder : ComponentContainerBuilder, MessageComponentBuilder {
    private var _spoiler: OptionalBoolean = OptionalBoolean.Missing

    private var _accentColor: Optional<Color> = Optional.Missing()

    public var components: MutableList<MessageComponentBuilder>? = mutableListOf()

    /**
     * Whether the component is a spoiler. Defaults to `false`.
     */
    public var spoiler: Boolean? by ::_spoiler.delegate()

    /**
     * The accent color of the container.
     *
     * If provided, the container will be rendered with a left-hand border of the specified color.
     */
    public var accentColor: Color? by ::_accentColor.delegate()

    override fun addComponent(component: ContainerComponentBuilder) {
        components?.add(component) ?: run { components = mutableListOf(component) }
    }

    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.Container,
        components = Optional((components ?: emptyList()).map { it.build() }),
        spoiler = _spoiler,
        accentColor = _accentColor,
    )
}
