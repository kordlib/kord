package dev.kord.core.entity.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.MediaGalleryItem
import dev.kord.common.entity.SeparatorSpacingSize
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.value
import dev.kord.core.cache.data.ChatComponentData
import dev.kord.common.entity.UnfurledMediaItem as UnfurledMediaItemData

/**
 * A component which can be an [accessory][SectionComponent.accessory].
 */
public sealed interface AccessoryComponent : Component

internal fun AccessoryComponent(data: ChatComponentData): AccessoryComponent = when (data.type) {
    is ComponentType.Button -> ButtonComponent(data)
    is ComponentType.Thumbnail -> ThumbnailComponent(data)
    else -> error("Unknown accessory component: ${data.type}")
}

/**
 * Representation of an unfurled media item.
 */
public class UnfurledMediaItem(private val data: UnfurledMediaItemData) {
    public val url: String get() = data.url
    public val proxyUrl: String? get() = data.proxyUrl.value
    public val height: Int? get() = data.height.value
    public val width: Int? get() = data.width.value
    public val contentType: String? get() = data.contentType.value
}

/**
 * 	Container to display text alongside an accessory component.
 */
public class SectionComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.Section

    /**
     * One to three [text components][TextDisplayComponent].
     */
    public val components: List<TextDisplayComponent>
        get() = data.components.mapList { TextDisplayComponent(it as ChatComponentData) }.orEmpty()

    /**
     * 	A thumbnail or a button component, with a future possibility of adding more compatible components.
     */
    public val accessory: AccessoryComponent?
        get() = data.accessory.map { AccessoryComponent(it) }.value

}

/**
 * A Text Display is a top-level content component that allows you to add text to your message formatted with markdown and mention users and roles.
 * This is similar to the content field of a message, but allows you to add multiple text components, controlling the layout of your message.
 */
public class TextDisplayComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.TextDisplay

    /**
     * Text that will be displayed similar to a message (supports Markdown)
     */
    public val content: String
        get() = data.content.value!!

}

/**
 * A Thumbnail is a content component that is a small image only usable as an accessory in a [section][SectionComponent].
 */
public class ThumbnailComponent(override val data: ChatComponentData) : AccessoryComponent {

    override val type: ComponentType
        get() = ComponentType.Thumbnail

    /**
     * The url of the image
     */
    public val url: UnfurledMediaItem = UnfurledMediaItem(data.image.value!!)

    /**
     * 	Alt text for the media.
     */
    public val description: String?
        get() = data.description.value

    /**
     * Whether the thumbnail should be a spoiler (or blurred out).
     */
    public val spoiler: Boolean
        get() = data.spoiler.discordBoolean

}

/**
 * A Media Gallery is a top-level content component that allows you to display 1-10 media attachments in an organized gallery format.
 * Each item can have optional descriptions and can be marked as spoilers.
 */
public class MediaGalleryComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.MediaGallery

    /**
     * 	1 to 10 media gallery items.
     */
    public val items: List<MediaGalleryItem>
        get() = data.items.orEmpty()

}

/**
 * A File is a top-level component that allows you to display an uploaded file as an attachment to the message and reference it in the component.
 * Each file component can only display 1 attached file, but you can upload multiple files and add them to different file components within your payload.
 * This is similar to the embeds field of a message but allows you to control the layout of your message by using this anywhere as a component
 */
public class FileComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.File

    /**
     * The file.
     */
    public val url: UnfurledMediaItem
        get() = UnfurledMediaItem(data.image.value!!)

    /**
     * Whether the media should be a spoiler (or blurred out).
     */
    public val spoiler: Boolean
        get() = data.spoiler.discordBoolean

}

/**
 * A Container is a top-level layout component that holds up to 10 components.
 * Containers are visually distinct from surrounding components and have an optional customizable color bar.
 */
public class ContainerComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.Container

    /**
     * Up to 10 components.
     */
    public val components: List<Component>
        get() = data.components.mapList { Component(it) }.orEmpty()

}

/**
 * A Separator is a top-level layout component that adds vertical padding and visual division between other components.
 */
public class SeparatorComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.Separator

    /**
     * 	Whether a visual divider should be displayed in the component.
     */
    public val divider: Boolean
        get() = data.divider.discordBoolean

    /**
     * Size of separator padding.
     */
    public val spacing: SeparatorSpacingSize?
        get() = data.spacing.value

}
