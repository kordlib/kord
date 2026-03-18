package dev.kord.core.entity.component

import dev.kord.common.entity.ComponentType
import dev.kord.core.cache.data.ChatComponentData
import dev.kord.core.cache.data.CheckboxComponentData
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.cache.data.LabelComponentData
import dev.kord.core.cache.data.SelectComponentData
import dev.kord.core.cache.data.TextInputComponentData
import dev.kord.core.entity.Message

/**
 * An interactive element inside a [Message].
 */

public sealed interface Component {

    /**
     * The type of component.
     * @see ButtonComponent
     * @see ActionRowComponent
     * @see SelectMenuComponent
     * @see UnknownComponent
     */
    public val type: ComponentType get() = data.type

    public val data: ComponentData
}

/**
 * Creates a [Component] from the [data].
 * @see ActionRowComponent
 * @see ButtonComponent
 * @see SelectMenuComponent
 * @see TextInputComponent
 * @see LabelComponent
 * @see CheckboxComponent
 * @see UnknownComponent
 */
public fun Component(data: ComponentData): Component = when (data.type) {
    ComponentType.ActionRow -> ActionRowComponent(data)
    ComponentType.Button -> ButtonComponent(data as ChatComponentData)
    ComponentType.StringSelect -> StringSelectComponent(data as SelectComponentData)
    ComponentType.UserSelect -> UserSelectComponent(data as SelectComponentData)
    ComponentType.RoleSelect -> RoleSelectComponent(data as SelectComponentData)
    ComponentType.MentionableSelect -> MentionableSelectComponent(data as SelectComponentData)
    ComponentType.ChannelSelect -> ChannelSelectComponent(data as SelectComponentData)
    ComponentType.TextInput -> TextInputComponent(data as TextInputComponentData)
    ComponentType.Container -> ContainerComponent(data as ChatComponentData)
    ComponentType.File -> FileComponent(data as ChatComponentData)
    ComponentType.MediaGallery -> MediaGalleryComponent(data as ChatComponentData)
    ComponentType.Section -> SectionComponent(data as ChatComponentData)
    ComponentType.Separator -> SeparatorComponent(data as ChatComponentData)
    ComponentType.TextDisplay -> TextDisplayComponent(data as ChatComponentData)
    ComponentType.Thumbnail -> ThumbnailComponent(data as ChatComponentData)
    ComponentType.Label -> LabelComponent(data as LabelComponentData)
    ComponentType.FileUpload -> FileUploadComponent(data as SelectComponentData)
    ComponentType.RadioGroup -> RadioGroupComponent(data as ChatComponentData)
    ComponentType.CheckboxGroup -> CheckboxGroupComponent(data as SelectComponentData)
    ComponentType.Checkbox -> CheckboxComponent(data as CheckboxComponentData)
    is ComponentType.Unknown -> UnknownComponent(data)
}
