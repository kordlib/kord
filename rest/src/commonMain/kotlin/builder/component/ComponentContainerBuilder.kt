package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordInternal
import dev.kord.common.entity.SeparatorSpacingSize
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A builder for an object which can contain [multiple components][ContainerComponentBuilder].
 *
 * @see dev.kord.rest.builder.message.MessageBuilder
 * @see ContainerBuilder
 */
public interface ComponentContainerBuilder {
    @KordInternal
    public fun addComponent(component: ContainerComponentBuilder)

    /**
     * Adds a separator with specified [spacing].
     */
    public fun separator(spacing: SeparatorSpacingSize): Unit = separator {
        this.spacing = spacing
    }

    /**
     * Adds a [text].
     */
    public fun textDisplay(text: String): Unit = textDisplay {
        content = text
    }
}

public inline fun ComponentContainerBuilder.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val component = ActionRowBuilder().apply(builder)
    addComponent(component)
}

public inline fun ComponentContainerBuilder.textDisplay(builder: TextDisplayBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val component = TextDisplayBuilder().apply(builder)
    addComponent(component)
}

public inline fun ComponentContainerBuilder.section(builder: SectionBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val component = SectionBuilder().apply(builder)
    addComponent(component)
}

public inline fun ComponentContainerBuilder.mediaGallery(builder: MediaGalleryBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val component = MediaGalleryBuilder().apply(builder)
    addComponent(component)
}

public inline fun ComponentContainerBuilder.separator(builder: SeparatorBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val component = SeparatorBuilder().apply(builder)
    addComponent(component)
}

public inline fun ComponentContainerBuilder.file(builder: FileBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val component = FileBuilder().apply(builder)
    addComponent(component)
}
