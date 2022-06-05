package dev.kord.core.entity.interaction

import dev.kord.common.entity.CommandArgument
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.GlobalInteractionBehavior
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.ResolvedChannel

public sealed interface OptionValue<out T> {
    public val value: T
    public val focused: Boolean
}

public sealed interface ResolvableOptionValue<out T : Entity> : OptionValue<Snowflake> {
    public val resolvedObject: T?
}
public class IntegerOptionValue(override val value: Long, override val focused: Boolean) : OptionValue<Long> {
    override fun toString(): String = "IntegerOptionValue(value=$value)"
}

public class NumberOptionValue(override val value: Double, override val focused: Boolean) : OptionValue<Double> {
    override fun toString(): String = "NumberOptionValue(value=$value)"
}

public class StringOptionValue(override val value: String, override val focused: Boolean) : OptionValue<String> {
    override fun toString(): String = "StringOptionValue(value=$value)"
}

public class BooleanOptionValue(override val value: Boolean, override val focused: Boolean) : OptionValue<Boolean> {
    override fun toString(): String = "BooleanOptionValue(value=$value)"
}


public class RoleOptionValue(override val value: Snowflake, override val focused: Boolean, override val resolvedObject: Role?) : ResolvableOptionValue<Role> {
    override fun toString(): String = "RoleOptionValue(value=$value)"
}

public open class UserOptionValue(override val value: Snowflake, override val focused: Boolean, override val resolvedObject: User?) : ResolvableOptionValue<User> {
    override fun toString(): String = "UserOptionValue(value=$value)"
}

public class MemberOptionValue(value: Snowflake, focused: Boolean, override val resolvedObject: Member?) : UserOptionValue(value, focused, resolvedObject) {
    override fun toString(): String = "MemberOptionValue(value=$value)"
}

public class ChannelOptionValue(override val value: Snowflake, override val focused: Boolean, override val resolvedObject: ResolvedChannel?) :
    ResolvableOptionValue<ResolvedChannel> {
    override fun toString(): String = "ChannelOptionValue(value=$value)"
}
public class MentionableOptionValue(override val value: Snowflake, override val focused: Boolean, override val resolvedObject: Entity?) : ResolvableOptionValue<Entity> {
    override fun toString(): String = "MentionableOptionValue(value=$value)"
}

public class AttachmentOptionValue(override val value: Snowflake, override val focused: Boolean, override val resolvedObject: Attachment?) : ResolvableOptionValue<Attachment> {
    override fun toString(): String = "AttachmentOptionValue(value=$value)"
}

public fun OptionValue(value: CommandArgument<*>, resolvedObjects: ResolvedObjects?): OptionValue<*> {
    val focused = value.focused.orElse(false)
    return when (value) {
        is CommandArgument.NumberArgument -> NumberOptionValue(value.value, focused)
        is CommandArgument.BooleanArgument -> BooleanOptionValue(value.value, focused)
        is CommandArgument.IntegerArgument -> IntegerOptionValue(value.value, focused)
        is CommandArgument.StringArgument, is CommandArgument.AutoCompleteArgument ->
            StringOptionValue(value.value as String, focused)
        is CommandArgument.ChannelArgument -> {
            val channel = resolvedObjects?.channels.orEmpty()[value.value]
            ChannelOptionValue(value.value, focused, channel)
        }

        is CommandArgument.MentionableArgument -> {
            val channel = resolvedObjects?.channels.orEmpty()[value.value]
            val user = resolvedObjects?.users.orEmpty()[value.value]
            val member = resolvedObjects?.members.orEmpty()[value.value]
            val role = resolvedObjects?.roles.orEmpty()[value.value]

            val entity = channel ?: member ?: user ?: role
            MentionableOptionValue(value.value, focused, entity)
        }

        is CommandArgument.RoleArgument -> {
            val role = resolvedObjects?.roles.orEmpty()[value.value]
            RoleOptionValue(value.value, focused, role)
        }

        is CommandArgument.UserArgument -> {
            val member = resolvedObjects?.members.orEmpty()[value.value]
            if (member != null) return MemberOptionValue(value.value, focused, member)
            val user = resolvedObjects?.users.orEmpty()[value.value]
            UserOptionValue(value.value, focused, user)
        }

        is CommandArgument.AttachmentArgument -> {
            val attachment = resolvedObjects?.attachments.orEmpty()[value.value]
            AttachmentOptionValue(value.value, focused, attachment)

        }
    }
}

