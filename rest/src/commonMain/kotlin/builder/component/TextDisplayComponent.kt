package dev.kord.rest.builder.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate

public class TextDisplayBuilder : ContainerComponentBuilder {
    private var _content: Optional<String> = Optional.Missing()

    /**
     * The text content of the component.
     */
    public var content: String? by ::_content.delegate()

    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.TextDisplay,
        content = _content,
    )
}
