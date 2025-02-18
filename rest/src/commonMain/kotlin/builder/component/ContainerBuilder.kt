package dev.kord.rest.builder.component

import dev.kord.common.Color
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate

public class ContainerBuilder : MessageComponentBuilder {
    private var _spoiler: OptionalBoolean = OptionalBoolean.Missing

    private var _accentColor: Optional<Color> = Optional.Missing()

    public val components: MutableList<ContainerComponentBuilder> = mutableListOf()

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

    public fun actionRow(builder: ActionRowBuilder.() -> Unit) {
        components.add(ActionRowBuilder().apply(builder))
    }

    public fun textDisplay(builder: TextDisplayBuilder.() -> Unit) {
        components.add(TextDisplayBuilder().apply(builder))
    }

    public fun section(builder: SectionBuilder.() -> Unit) {
        components.add(SectionBuilder().apply(builder))
    }

    public fun mediaGallery(builder: MediaGalleryBuilder.() -> Unit) {
        components.add(MediaGalleryBuilder().apply(builder))
    }

    public fun separator(builder: SeparatorBuilder.() -> Unit) {
        components.add(SeparatorBuilder().apply(builder))
    }

    public fun file(builder: FileBuilder.() -> Unit) {
        components.add(FileBuilder().apply(builder))
    }

    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.Container,
        components = Optional(components.map { it.build() }),
        spoiler = _spoiler,
        accentColor = _accentColor,
    )
}
