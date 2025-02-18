package dev.kord.rest.builder.component

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class SectionBuilder : ContainerComponentBuilder {
    private var _accessory: Optional<AccessoryComponentBuilder> = Optional.Missing()

    /**
     *
     */
    public var accessory: AccessoryComponentBuilder? by ::_accessory.delegate()

    /**
     * A list of [text displays][TextDisplayBuilder] in this section.
     */
    public val components: MutableList<TextDisplayBuilder> = mutableListOf()

    /**
     * Adds a thumbnail as an accessory to this section.
     * This is mutually exclusive with other accessory components.
     */
    public inline fun thumbnailAccessory(builder: ThumbnailBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        accessory = ThumbnailBuilder().apply(builder)
    }

    /**
     * Adds an interaction button as an accessory to this section.
     * This is mutually exclusive with other accessory components.
     *
     * @param style the style of this button, use [linkButtonAccessory] for [ButtonStyle.Link].
     * @param customId the ID of this button, used to identify component interactions.
     */
    public inline fun interactionButtonAccessory(
        style: ButtonStyle,
        customId: String,
        builder: ButtonBuilder.() -> Unit = {},
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        accessory = ButtonBuilder.InteractionButtonBuilder(style, customId).apply(builder)
    }

    /**
     * Adds a link button as an accessory to this section.
     * This is mutually exclusive with other accessory components.
     *
     * @param url The url to open.
     */
    public inline fun linkButtonAccessory(url: String, builder: ButtonBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        accessory = ButtonBuilder.LinkButtonBuilder(url).apply(builder)
    }

    /**
     * Adds a text display as a component to this section.
     *
     *
     */
    public inline fun textDisplay(builder: TextDisplayBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(TextDisplayBuilder().apply(builder))
    }

    override fun build(): DiscordComponent = DiscordChatComponent(
        type = ComponentType.Section,
        components = Optional(components.map { it.build() }),
        accessory = _accessory.map { it.build() },
    )
}
