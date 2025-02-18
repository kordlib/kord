package dev.kord.core.entity.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.MediaGalleryItem
import dev.kord.common.entity.SeparatorSpacingSize
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.ChatComponentData

public class SectionComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.Section

    /**
     *
     */
    public val components: List<Component>
        get() = data.components.mapList { Component(it) }.orEmpty()

    /**
     *
     */
    public val accessory: Component?
        get() = data.accessory.map { Component(it) }.value

}

public class TextDisplayComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.TextDisplay

    /**
     *
     */
    public val content: String
        get() = data.content.value!!

}

public class ThumbnailComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.Thumbnail

    /**
     *
     */
    public val url: String
        get() = data.image.value?.url!!

    /**
     *
     */
    public val description: String?
        get() = data.description.value

    /**
     *
     */
    public val spoiler: Boolean
        get() = data.spoiler.discordBoolean

}

public class MediaGalleryComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.MediaGallery

    /**
     *
     */
    public val items: List<MediaGalleryItem>
        get() = data.items.orEmpty()

}

public class FileComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.File

    /**
     *
     */
    public val url: String
        get() = data.image.value?.url!!

    /**
     *
     */
    public val spoiler: Boolean
        get() = data.spoiler.discordBoolean

}

public class ContainerComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.Container

    /**
     *
     */
    public val components: List<Component>
        get() = data.components.mapList { Component(it) }.orEmpty()

}

public class SeparatorComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.Separator

    public val divider: Boolean
        get() = data.divider.discordBoolean

    public val spacing: SeparatorSpacingSize?
        get() = data.spacing.value

}
