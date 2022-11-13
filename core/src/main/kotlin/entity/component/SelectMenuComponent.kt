@file:Suppress("DEPRECATION")

package dev.kord.core.entity.component

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.cache.data.SelectOptionData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.SelectMenuInteraction

/**
 * An interactive dropdown menu rendered on a [Message] that consists of multiple [options].
 */

public open class SelectMenuComponent
@Deprecated(
    "This will be made a sealed class in the future, please stop using this constructor. You can instead use the " +
            "constructor of one of the subtypes.",
    ReplaceWith("StringSelectComponent(data)", "dev.kord.core.entity.component.StringSelectComponent"),
    level = DeprecationLevel.WARNING,
)
public constructor(override val data: ComponentData) : Component {

    @Suppress("DEPRECATION", "INAPPLICABLE_JVM_NAME")
    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    @get:JvmName("getType")
    public open val type0: ComponentType.SelectMenu get() = ComponentType.SelectMenu

    /**
     * The custom identifier for any [ComponentInteractions][SelectMenuInteraction]
     * this select menu will trigger.
     */
    public val customId: String get() = data.customId.value!!

    /**
     * The placeholder value if no value has been selected, null if not set.
     */
    public val placeholder: String? get() = data.placeholder.value

    /**
     * The possible options to choose from.
     */
    @Deprecated(
        "This is only available for 'ComponentType.StringSelect' (in the 'StringSelectComponent' subclass).",
        ReplaceWith(
            "(this as? StringSelectComponent)?.options ?: emptyList()",
            "dev.kord.core.entity.component.StringSelectComponent",
        ),
        level = DeprecationLevel.WARNING,
    )
    public open val options: List<SelectOption> get() = data.options.orEmpty().map { SelectOption(it) }

    /**
     * The minimum amount of options that can be chosen, default `1`.
     */
    public val minValues: Int get() = data.minValues.orElse(1)

    /**
     * The maximum amount of options that can be chosen, default `1`.
     */
    public val maxValues: Int get() = data.maxValues.orElse(1)

    /**
     * Whether this select menu can be used.
     */
    public val disabled: Boolean get() = data.disabled.discordBoolean

    override fun equals(other: Any?): Boolean {
        if (other !is SelectMenuComponent) return false

        return customId == other.customId
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String = "SelectMenuComponent(data=$data)"
}

public class StringSelectComponent(data: ComponentData) : SelectMenuComponent(data) {
    override val type: ComponentType.StringSelect get() = ComponentType.StringSelect

    /**
     * The possible options to choose from.
     */
    @Suppress("OVERRIDE_DEPRECATION")
    public override val options: List<SelectOption> get() = data.options.orEmpty().map { SelectOption(it) }
}

public class UserSelectComponent(data: ComponentData) : SelectMenuComponent(data) {
    override val type: ComponentType.UserSelect get() = ComponentType.UserSelect
}

public class RoleSelectComponent(data: ComponentData) : SelectMenuComponent(data) {
    override val type: ComponentType.RoleSelect get() = ComponentType.RoleSelect
}

public class MentionableSelectComponent(data: ComponentData) : SelectMenuComponent(data) {
    override val type: ComponentType.MentionableSelect get() = ComponentType.MentionableSelect
}

public class ChannelSelectComponent(data: ComponentData) : SelectMenuComponent(data) {
    override val type: ComponentType.StringSelect get() = ComponentType.StringSelect

    public val channelTypes: List<ChannelType>? get() = data.channelTypes.value
}

/**
 * An option in a [SelectMenuComponent].
 */
public class SelectOption(public val data: SelectOptionData) {

    /**
     * The user-facing name of the option.
     */
    public val label: String get() = data.label

    /**
     * The dev-defined value of the option.
     */
    public val value: String get() = data.value

    /**
     * An additional description of the option. Null if not set.
     */
    public val description: String? get() = data.description.value

    /**
     * The emoji to show in the option. Null if not set.
     */
    public val emoji: DiscordPartialEmoji? = data.emoji.value

    /**
     * Whether this option is selected by default.
     */
    public val default: Boolean = data.default.discordBoolean

    override fun equals(other: Any?): Boolean {
        if (other !is SelectOption) return false

        return data == other.data
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String = "SelectOption(data=$data)"

}
