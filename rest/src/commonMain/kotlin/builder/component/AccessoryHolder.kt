package dev.kord.rest.builder.component

import dev.kord.common.entity.ButtonStyle
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * An object which can have an [accessory][AccessoryComponentBuilder].
 *
 * @see SectionBuilder
 */
public interface AccessoryHolder {
    public var accessory: AccessoryComponentBuilder?
}

/**
 * Adds an interaction button as an accessory to this section.
 * This is mutually exclusive with other accessory components.
 *
 * @param style the style of this button, use [linkButtonAccessory] for [ButtonStyle.Link].
 * @param customId the ID of this button, used to identify component interactions.
 */
public inline fun AccessoryHolder.interactionButtonAccessory(
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
public inline fun AccessoryHolder.linkButtonAccessory(url: String, builder: ButtonBuilder.() -> Unit = {}) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    accessory = ButtonBuilder.LinkButtonBuilder(url).apply(builder)
}