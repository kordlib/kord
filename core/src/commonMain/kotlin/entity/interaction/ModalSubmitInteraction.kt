package dev.kord.core.entity.interaction

import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.ComponentInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.data.LabelComponentData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.component.*
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [ActionInteraction] created when a user submits a modal.
 *
 * Can contain a [message].
 */
public sealed interface ModalSubmitInteraction : ActionInteraction, ComponentInteractionBehavior {

    /**
     * The [custom id](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-modal)
     * of the modal.
     */
    public val modalId: String get() = data.data.customId.value!!

    /** The [ActionRowComponent]s of the modal containing the values submitted by the user. */
    public val actionRows: List<ActionRowComponent>
        get() = data.data.components.orEmpty().map { ActionRowComponent(it) }

    /** The [ResolvedObjects] for the interaction event. */
    public val resolvedObjects: ResolvedObjects?
        get() = data.data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }

    /**
     * The [Component]s of the modal, containing the values submitted by the user in their respective subclasses, indexed by their customId (if present).
     */
    public val responseComponents: Map<String, Component>
        get() = actionRows.mapNotNull { actionRow ->
            if (actionRow.data is LabelComponentData) {
                val component = Component(actionRow.data.component.value!!)
                val customId = component.data.customId.value
                customId?.let { it to component }
            } else {
                val component = actionRow.components.firstOrNull() ?: return@mapNotNull null
                val customId = component.data.customId.value
                customId?.let { it to component }
            }
        }.associate { it }

    /**
     * The [TextInputComponent]s of the modal, indexed by their [customId][TextInputComponent.customId]. They contain
     * the [value][TextInputComponent.value]s submitted by the user.
     *
     * @see textInputs
     */
    @Deprecated("Action Rows with text inputs in modals are now deprecated. Kept for binary compatibility." +
            " The deprecation level will be raised to ERROR in 0.18.0, to HIDDEN in 0.19.0 and this declaration will" +
            " be removed in 0.20.0",
        ReplaceWith("textInputs"), DeprecationLevel.WARNING)
    public val textInputs0: Map<String, TextInputComponent>
        get() = actionRows
            .flatMap { it.components }
            .filterIsInstance<TextInputComponent>()
            .associateBy { it.customId }

    /**
     * The [TextInputComponent]s of the modal, indexed by their [customId][TextInputComponent.customId]. They contain
     * the [value][TextInputComponent.value]s submitted by the user.
     */
    public val textInputs: Map<String, TextInputComponent>
        get() = responseComponents.values
            .filterIsInstance<TextInputComponent>()
            .associateBy { it.customId }

    /**
     * The [StringSelectComponent]s of the modal, indexed by their [customId][StringSelectComponent.customId]. They
     * contain the [values][StringSelectComponent.values] submitted by the user.
     */
    public val stringSelects: Map<String, StringSelectComponent>
        get() = responseComponents.values
            .filterIsInstance<StringSelectComponent>()
            .associateBy { it.customId }

    /**
     * The [UserSelectComponent]s of the modal, indexed by their [customId][UserSelectComponent.customId]. They
     * contain the [values][UserSelectComponent.values] submitted by the user.
     */
    public val userSelects: Map<String, UserSelectComponent>
        get() = responseComponents.values
            .filterIsInstance<UserSelectComponent>()
            .associateBy { it.customId }

    /**
     * The [RoleSelectComponent]s of the modal, indexed by their [customId][RoleSelectComponent.customId]. They contain
     * the [values][RoleSelectComponent.values] submitted by the user.
     */
    public val roleSelects: Map<String, RoleSelectComponent>
        get() = responseComponents.values
            .filterIsInstance<RoleSelectComponent>()
            .associateBy { it.customId }

    /**
     * The [MentionableSelectComponent]s of the modal, indexed by their [customId][MentionableSelectComponent.customId].
     * They contain the [values][MentionableSelectComponent.values] submitted by the user.
     */
    public val mentionableSelects: Map<String, MentionableSelectComponent>
        get() = responseComponents.values
            .filterIsInstance<MentionableSelectComponent>()
            .associateBy { it.customId }

    /**
     * The [ChannelSelectComponent]s of the modal, indexed by their [customId][ChannelSelectComponent.customId]. They
     * contain the [values][ChannelSelectComponent.values] submitted by the user.
     */
    public val channelSelects: Map<String, ChannelSelectComponent>
        get() = responseComponents.values
            .filterIsInstance<ChannelSelectComponent>()
            .associateBy { it.customId }

    /**
     * The [FileUploadComponent]s of the modal, indexed by their [customId][FileUploadComponent.customId]. They
     * contain the [value ids][FileUploadComponent.valueIds] submitted by the user.
     */
    public val fileUploads: Map<String, FileUploadComponent>
        get() = responseComponents.values
            .filterIsInstance<FileUploadComponent>()
            .associateBy { it.customId }

    /**
     * The message the [Component], to which the modal was the response for, is attached to.
     *
     * This is only present if the interaction was created by submitting a modal that was the response for a
     * [ComponentInteraction].
     */
    public val message: Message? get() = data.message.unwrap { Message(it, kord) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ModalSubmitInteraction
}

/** A [ModalSubmitInteraction] that took place in the context of a [Guild]. */
public class GuildModalSubmitInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ModalSubmitInteraction, GuildInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildModalSubmitInteraction =
        GuildModalSubmitInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GuildModalSubmitInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildModalSubmitInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

/** A [ModalSubmitInteraction] that took place in a global context (e.g. a DM). */
public class GlobalModalSubmitInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ModalSubmitInteraction, GlobalInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalModalSubmitInteraction =
        GlobalModalSubmitInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalModalSubmitInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalModalSubmitInteraction(data=$data, kord=$kord, supplier=$supplier)"
}


public fun ModalSubmitInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): ModalSubmitInteraction = when (data.guildId) {
    is OptionalSnowflake.Missing -> GlobalModalSubmitInteraction(data, kord, supplier)
    is OptionalSnowflake.Value -> GuildModalSubmitInteraction(data, kord, supplier)
}
