package dev.kord.rest.builder.message

import dev.kord.common.entity.AllowedMentionType
import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder

/**
 * The mentions that should trigger a ping. See the [Discord documentation](https://discord.com/developers/docs/resources/channel#allowed-mentions-object).
 */
public class AllowedMentionsBuilder {
    /**
     * The roles that should be mentioned in this message, any id that is mentioned in this list but not present in the
     * [MessageCreateBuilder] will be ignored.
     */
    public val roles: MutableSet<Snowflake> = mutableSetOf()

    /**
     * The users that should be mentioned in this message, any id that is mentioned in this list but not present in the
     * [MessageCreateBuilder] will be ignored.
     */
    public val users: MutableSet<Snowflake> = mutableSetOf()

    /**
     * The types of pings that should trigger in this message. Selecting [AllowedMentionType.UserMentions] or [AllowedMentionType.RoleMentions]
     * together with any value in [users] or [roles] respectively will result in an error.
     */
    public val types: MutableSet<AllowedMentionType> = mutableSetOf()

    private var _repliedUser: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Whether to mention the user being replied to.
     *
     * Only set this if [UserMessageCreateBuilder.messageReference] is not `null`.
     */
    public var repliedUser: Boolean? by ::_repliedUser.delegate()

    /**
     * Adds the type to the list of types that should receive a ping.
     */
    public operator fun AllowedMentionType.unaryPlus() {
        types.add(this)
    }

    /**
     * Adds the type to the list of types that should receive a ping.
     */
    public fun add(type: AllowedMentionType) {
        type.unaryPlus()
    }

    public fun build(): AllowedMentions = AllowedMentions(
        parse = types.toList(),
        users = users.map { it.toString() },
        roles = roles.map { it.toString() },
        repliedUser = _repliedUser
    )

}
