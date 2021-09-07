package dev.kord.core.exception

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.entity.channel.Channel

class EntityNotFoundException : Exception {

    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)

    @Suppress("NOTHING_TO_INLINE")
    companion object {

        @PublishedApi
        internal inline fun entityNotFound(entityType: String, id: Snowflake): Nothing =
            throw EntityNotFoundException("$entityType with id ${id.asString} was not found.")

        @PublishedApi
        internal inline fun guildEntityNotFound(entityType: String, guildId: Snowflake, id: Snowflake): Nothing =
            throw EntityNotFoundException("$entityType with id ${id.asString} in guild ${guildId.asString} was not found.")


        inline fun guildNotFound(guildId: Snowflake): Nothing =
            entityNotFound("Guild", guildId)

        inline fun <reified T : Channel> channelNotFound(channelId: Snowflake): Nothing =
            entityNotFound(T::class.simpleName!!, channelId)

        inline fun memberNotFound(guildId: Snowflake, userId: Snowflake): Nothing =
            guildEntityNotFound("Member", guildId = guildId, id = userId)

        inline fun messageNotFound(channelId: Snowflake, messageId: Snowflake): Nothing =
            throw EntityNotFoundException("Message with id ${messageId.asString} in channel ${channelId.asString} was not found")

        inline fun userNotFound(userId: Snowflake): Nothing =
            entityNotFound("User", userId)

        inline fun selfNotFound(): Nothing =
            throw EntityNotFoundException("Self user not found")

        inline fun roleNotFound(guildId: Snowflake, roleId: Snowflake): Nothing =
            guildEntityNotFound("Role", guildId = guildId, id = roleId)

        inline fun banNotFound(guildId: Snowflake, userId: Snowflake): Nothing =
            guildEntityNotFound("Ban", guildId = guildId, id = userId)

        inline fun emojiNotFound(guildId: Snowflake, emojiId: Snowflake): Nothing =
            guildEntityNotFound("GuildEmoji", guildId = guildId, id = emojiId)

        inline fun webhookNotFound(webhookId: Snowflake): Nothing =
            entityNotFound("Webhook", webhookId)

        inline fun inviteNotFound(code: String): Nothing =
            throw EntityNotFoundException("Invite with code $code was not found.")

        inline fun widgetNotFound(id: Snowflake): Nothing =
            throw EntityNotFoundException("Widget for guild ${id.value} was not found.")

        inline fun templateNotFound(code: String): Nothing =
            throw EntityNotFoundException("Template $code was not found.")

        inline fun welcomeScreenNotFound(guildId: Snowflake): Nothing =
            throw EntityNotFoundException("Welcome screen for guild $guildId was not found.")

        inline fun stageInstanceNotFound(channelId: Snowflake): Nothing =
            throw EntityNotFoundException("Stage instance for channel $channelId was not found.")

        inline fun applicationCommandPermissionsNotFound(commandId: Snowflake): Nothing =
            entityNotFound("ApplicationCommand", commandId)

        inline fun <reified T : ApplicationCommand> applicationCommandNotFound(commandId: Snowflake): Nothing =
            entityNotFound(T::class.simpleName!!, commandId)

        inline fun interactionNotFound(token: String): Nothing =
            throw EntityNotFoundException("Interaction with token $token was not found.")
        }
    }
