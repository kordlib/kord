package dev.kord.core.entity.component

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.cache.data.SelectOptionData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.SelectMenuInteraction

/**
 * An interactive dropdown menu rendered on a [Message].
 */
public sealed class SelectMenuComponent(override val data: ComponentData) : Component {

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

    /** The possible options to choose from. */
    public val options: List<SelectOption> get() = data.options.orEmpty().map { SelectOption(it) }
}

/** The possible options to choose from. */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated("Replaced by member in StringSelectComponent.", ReplaceWith("this.options"), DeprecationLevel.ERROR)
public val StringSelectComponent.options: List<SelectOption> get() = options

public class UserSelectComponent(data: ComponentData) : SelectMenuComponent(data) {
    override val type: ComponentType.UserSelect get() = ComponentType.UserSelect

    /** The list of [default values][SelectDefaultValue.User] for this select menu. */
    public val defaultValues: List<SelectDefaultValue.User>
        get() = data.defaultValues.orEmpty().map {
            when (val type = it.type) {
                SelectDefaultValueType.User -> SelectDefaultValue.User(it.id)
                else -> error("Did not expect $type")
            }
        }
}

public class RoleSelectComponent(data: ComponentData) : SelectMenuComponent(data) {
    override val type: ComponentType.RoleSelect get() = ComponentType.RoleSelect

    /** The list of [default values][SelectDefaultValue.Role] for this select menu. */
    public val defaultValues: List<SelectDefaultValue.Role>
        get() = data.defaultValues.orEmpty().map {
            when (val type = it.type) {
                SelectDefaultValueType.Role -> SelectDefaultValue.Role(it.id)
                else -> error("Did not expect $type")
            }
        }
}

public class MentionableSelectComponent(data: ComponentData) : SelectMenuComponent(data) {
    override val type: ComponentType.MentionableSelect get() = ComponentType.MentionableSelect

    /** The list of [default values][SelectDefaultValue.Mentionable] for this select menu. */
    public val defaultValues: List<SelectDefaultValue.Mentionable>
        get() = data.defaultValues.orEmpty().map {
            when (val type = it.type) {
                SelectDefaultValueType.User -> SelectDefaultValue.User(it.id)
                SelectDefaultValueType.Role -> SelectDefaultValue.Role(it.id)
                else -> error("Did not expect $type")
            }
        }
}

public class ChannelSelectComponent(data: ComponentData) : SelectMenuComponent(data) {
    override val type: ComponentType.StringSelect get() = ComponentType.StringSelect

    public val channelTypes: List<ChannelType>? get() = data.channelTypes.value

    /** The list of [default values][SelectDefaultValue.Channel] for this select menu. */
    public val defaultValues: List<SelectDefaultValue.Channel>
        get() = data.defaultValues.orEmpty().map {
            when (val type = it.type) {
                SelectDefaultValueType.Channel -> SelectDefaultValue.Channel(it.id)
                else -> error("Did not expect $type")
            }
        }
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

/**
 * A default value for auto-populated select menu components like [UserSelectComponent], [RoleSelectComponent],
 * [MentionableSelectComponent] or [ChannelSelectComponent].
 */
public sealed interface SelectDefaultValue {
    /**
     * The ID of a [User][dev.kord.core.entity.User], [Role][dev.kord.core.entity.Role] or
     * [Channel][dev.kord.core.entity.channel.Channel].
     */
    public val id: Snowflake

    /** The type of value [id] represents. */
    public val type: SelectDefaultValueType

    /**
     * A [SelectDefaultValue] for [MentionableSelectComponent].
     *
     * This is a common supertype for [User] and [Role].
     */
    public sealed interface Mentionable : SelectDefaultValue

    /** A [SelectDefaultValue] for [UserSelectComponent]. */
    public class User(override val id: Snowflake) : Mentionable {
        override val type: SelectDefaultValueType.User get() = SelectDefaultValueType.User
        override fun equals(other: Any?): Boolean = this === other || other is User && this.id == other.id
        override fun hashCode(): Int = id.hashCode()
        override fun toString(): String = "SelectDefaultValue.User(id=$id)"
    }

    /** A [SelectDefaultValue] for [RoleSelectComponent]. */
    public class Role(override val id: Snowflake) : Mentionable {
        override val type: SelectDefaultValueType.Role get() = SelectDefaultValueType.Role
        override fun equals(other: Any?): Boolean = this === other || other is Role && this.id == other.id
        override fun hashCode(): Int = id.hashCode()
        override fun toString(): String = "SelectDefaultValue.Role(id=$id)"
    }

    /** A [SelectDefaultValue] for [ChannelSelectComponent]. */
    public class Channel(override val id: Snowflake) : SelectDefaultValue {
        override val type: SelectDefaultValueType.Channel get() = SelectDefaultValueType.Channel
        override fun equals(other: Any?): Boolean = this === other || other is Channel && this.id == other.id
        override fun hashCode(): Int = id.hashCode()
        override fun toString(): String = "SelectDefaultValue.Channel(id=$id)"
    }
}
