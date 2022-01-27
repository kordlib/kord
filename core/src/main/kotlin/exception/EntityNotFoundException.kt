package dev.kord.core.exception

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.entity.channel.Channel

public class EntityNotFoundException : Exception {

    public constructor(message: String) : super(message)
    public constructor(cause: Throwable) : super(cause)
    public constructor(message: String, cause: Throwable) : super(message, cause)

    @Suppress("NOTHING_TO_INLINE")
    public companion object {

        @PublishedApi
        internal inline fun entityNotFound(entityType: String, id: Snowflake): Nothing =
            throw EntityNotFoundException("$entityType with id $id was not found.")

        @PublishedApi
        internal inline fun guildEntityNotFound(entityType: String, guildId: Snowflake, id: Snowflake): Nothing =
            throw EntityNotFoundException("$entityType with id $id in guild $guildId was not found.")


        public inline fun guildNotFound(guildId: Snowflake): Nothing =
            entityNotFound("Guild", guildId)

        public inline fun <reified T : Channel> channelNotFound(channelId: Snowflake): Nothing =
            entityNotFound(T::class.simpleName!!, channelId)

        public inline fun memberNotFound(guildId: Snowflake, userId: Snowflake): Nothing =
            guildEntityNotFound("Member", guildId = guildId, id = userId)

        public inline fun messageNotFound(channelId: Snowflake, messageId: Snowflake): Nothing =
            throw EntityNotFoundException("Message with id $messageId in channel $channelId was not found.")

        public inline fun userNotFound(userId: Snowflake): Nothing =
            entityNotFound("User", userId)

        public inline fun selfNotFound(): Nothing =
            throw EntityNotFoundException("Self user was not found.")

        public inline fun roleNotFound(guildId: Snowflake, roleId: Snowflake): Nothing =
            guildEntityNotFound("Role", guildId = guildId, id = roleId)

        public inline fun banNotFound(guildId: Snowflake, userId: Snowflake): Nothing =
            guildEntityNotFound("Ban", guildId = guildId, id = userId)

        public inline fun emojiNotFound(guildId: Snowflake, emojiId: Snowflake): Nothing =
            guildEntityNotFound("GuildEmoji", guildId = guildId, id = emojiId)

        public inline fun webhookNotFound(webhookId: Snowflake): Nothing =
            entityNotFound("Webhook", webhookId)

        public inline fun webhookMessageNotFound(
            webhookId: Snowflake,
            token: String,
            messageId: Snowflake,
            threadId: Snowflake? = null,
        ): Nothing = throw EntityNotFoundException(
            "Message with id $messageId ${
                if (threadId != null) "in thread $threadId " else ""
            }from webhook $webhookId with token $token was not found."
        )

        public inline fun inviteNotFound(code: String): Nothing =
            throw EntityNotFoundException("Invite with code $code was not found.")

        public inline fun widgetNotFound(id: Snowflake): Nothing =
            throw EntityNotFoundException("Widget for guild ${id.value} was not found.")

        public inline fun templateNotFound(code: String): Nothing =
            throw EntityNotFoundException("Template $code was not found.")

        public inline fun welcomeScreenNotFound(guildId: Snowflake): Nothing =
            throw EntityNotFoundException("Welcome screen for guild $guildId was not found.")

        public inline fun stageInstanceNotFound(channelId: Snowflake): Nothing =
            throw EntityNotFoundException("Stage instance for channel $channelId was not found.")

        public inline fun stickerNotFound(stickerId: Snowflake): Nothing =
            entityNotFound("Sticker", stickerId)

        public inline fun applicationCommandPermissionsNotFound(commandId: Snowflake): Nothing =
            entityNotFound("ApplicationCommand", commandId)

        public inline fun guildScheduledEventNotFound(eventId: Snowflake): Nothing =
            entityNotFound("GuildScheduledEvent", eventId)

        public inline fun <reified T : ApplicationCommand> applicationCommandNotFound(commandId: Snowflake): Nothing =
            entityNotFound(T::class.simpleName!!, commandId)

        public inline fun interactionNotFound(token: String): Nothing = throw EntityNotFoundException(
            "Initial interaction response for interaction with token $token was not found."
        )

        public inline fun followupMessageNotFound(token: String, messageId: Snowflake): Nothing =
            throw EntityNotFoundException(
                "Followup message with id $messageId for interaction with token $token was not found."
            )
    }
}
