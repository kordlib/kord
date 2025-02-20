package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TextInputStyle
import dev.kord.common.entity.optional.Optional
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public class ActionRowBuilder : ContainerComponentBuilder {
    public val components: MutableList<ActionRowComponentBuilder> = mutableListOf()

    public inline fun interactionButton(
        style: ButtonStyle,
        customId: String,
        builder: ButtonBuilder.InteractionButtonBuilder.() -> Unit,
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(
            ButtonBuilder.InteractionButtonBuilder(style, customId).apply(builder)
        )
    }

    public inline fun linkButton(
        url: String,
        builder: ButtonBuilder.LinkButtonBuilder.() -> Unit,
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(
            ButtonBuilder.LinkButtonBuilder(url).apply(builder)
        )
    }

    public inline fun premiumButton(
        skuId: Snowflake,
        builder: PremiumButtonBuilder.() -> Unit = {},
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(
            PremiumButtonBuilder(skuId).apply(builder)
        )
    }

    /**
     * Creates and adds a string select menu with the [customId] and configured by the [builder].
     * An ActionRow with a select menu cannot have any other select menus or buttons.
     */
    public inline fun stringSelect(customId: String, builder: StringSelectBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(StringSelectBuilder(customId).apply(builder))
    }

    /**
     * Creates and adds a user select menu with the [customId] and configured by the [builder].
     * An ActionRow with a select menu cannot have any other select menus or buttons.
     */
    public inline fun userSelect(customId: String, builder: UserSelectBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(UserSelectBuilder(customId).apply(builder))
    }

    /**
     * Creates and adds a role select menu with the [customId] and configured by the [builder].
     * An ActionRow with a select menu cannot have any other select menus or buttons.
     */
    public inline fun roleSelect(customId: String, builder: RoleSelectBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(RoleSelectBuilder(customId).apply(builder))
    }

    /**
     * Creates and adds a mentionable select menu with the [customId] and configured by the [builder].
     * An ActionRow with a select menu cannot have any other select menus or buttons.
     */
    public inline fun mentionableSelect(customId: String, builder: MentionableSelectBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(MentionableSelectBuilder(customId).apply(builder))
    }

    /**
     * Creates and adds a channel select menu with the [customId] and configured by the [builder].
     * An ActionRow with a select menu cannot have any other select menus or buttons.
     */
    public inline fun channelSelect(customId: String, builder: ChannelSelectBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ChannelSelectBuilder(customId).apply(builder))
    }

    /**
     * Creates and adds a text input with the [customId] and configured by the [builder].
     * Text Inputs can only be used within modals.
     */
    public inline fun textInput(
        style: TextInputStyle,
        customId: String,
        label: String,
        builder: TextInputBuilder.() -> Unit = {},
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(TextInputBuilder(style, customId, label).apply(builder))
    }

    override fun build(): DiscordChatComponent =
        DiscordChatComponent(
            ComponentType.ActionRow,
            components = Optional.missingOnEmpty(components.map(ActionRowComponentBuilder::build))
        )
}
