package dev.kord.rest.builder.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.MediaGalleryItem
import dev.kord.common.entity.SeparatorSpacingSize
import dev.kord.common.entity.UnfurledMediaItem
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map

public class ThumbnailBuilder : AccessoryComponentBuilder {
    private var _url: Optional<String> = Optional.Missing()

    private var _description: Optional<String> = Optional.Missing()

    private var _spoiler: OptionalBoolean = OptionalBoolean.Missing

    public var url: String? by ::_url.delegate()

    public var description: String? by ::_description.delegate()

    public var spoiler: Boolean? by ::_spoiler.delegate()

    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.Thumbnail,
        media = _url.map { UnfurledMediaItem(it) },
        description = _description,
        spoiler = _spoiler,
    )
}

public class MediaGalleryItemBuilder(public var url: String) {
    private var _description: Optional<String> = Optional.Missing()

    private var _spoiler: OptionalBoolean = OptionalBoolean.Missing

    public var description: String? by ::_description.delegate()

    public var spoiler: Boolean? by ::_spoiler.delegate()

    public fun build(): MediaGalleryItem = MediaGalleryItem(
        UnfurledMediaItem(url),
        _description,
        _spoiler,
    )
}

public class MediaGalleryBuilder : ContainerComponentBuilder {
    public val items: MutableList<MediaGalleryItemBuilder> = mutableListOf()

    public fun item(url: String, builder: MediaGalleryItemBuilder.() -> Unit = {}) {
        items.add(MediaGalleryItemBuilder(url).apply(builder))
    }

    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.MediaGallery,
        items = Optional(items.map { it.build() }),
    )
}

public class SeparatorBuilder : ContainerComponentBuilder {
    private var _spacing: Optional<SeparatorSpacingSize> = Optional.Missing()
private var _divider: OptionalBoolean = OptionalBoolean.Missing

public var divider: Boolean? by ::_divider.delegate()
    public var spacing: SeparatorSpacingSize? by ::_spacing.delegate()

    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.Separator,
        spacing = _spacing,
        divider = _divider,
    )
}

public class FileBuilder : ContainerComponentBuilder {
    private var _url: Optional<String> = Optional.Missing()

    private var _spoiler: OptionalBoolean = OptionalBoolean.Missing

    public var url: String? by ::_url.delegate()

    public var spoiler: Boolean? by ::_spoiler.delegate()

    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.File,
        media = _url.map { UnfurledMediaItem(it) },
        spoiler = _spoiler
    )
}

