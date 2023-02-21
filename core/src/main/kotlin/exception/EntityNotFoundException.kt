package dev.kord.core.exception

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.entity.channel.Channel

/**
 * Thrown when an Entity cannot be found.
 */
public class EntityNotFoundException : Exception {

    public constructor(message: String) : super(message)
    public constructor(cause: Throwable) : super(cause)
    public constructor(message: String, cause: Throwable) : super(message, cause)

    @Suppress("NOTHING_TO_INLINE")
    public companion object {

        /**
         * Indicates an Entity cannot be found
         *
         * @param entityType The type of entity that cannot be found
         * @param id The ID of the entity that was not found
         * @throws EntityNotFoundException
         */
        @PublishedApi
        internal inline fun entityNotFound(entityType: String, id: Snowflake): Nothing =
            throw EntityNotFoundException("$entityType with id $id was not found.")

        /**
         * Indicates a Guild Entity cannot be found
         *
         * @param entityType The type of entity that cannot be found
         * @param guildId The ID of the guild that cannot be found
         * @param id The ID of the entity that was not found
         * @throws EntityNotFoundException
         */
        @PublishedApi
        internal inline fun guildEntityNotFound(entityType: String, guildId: Snowflake, id: Snowflake): Nothing =
            throw EntityNotFoundException("$entityType with id $id in guild $guildId was not found.")

        /**
         * Indicates a guild was not found.
         *
         * @param guildId The ID of the guild that was not found
         * @throws EntityNotFoundException
         */
        public inline fun guildNotFound(guildId: Snowflake): Nothing =
            entityNotFound("Guild", guildId)

        /**
         * Indicates a channel was not found.
         *
         * @param channelId The ID of the channel that was not found
         * @throws EntityNotFoundException
         */
        public inline fun <reified T : Channel> channelNotFound(channelId: Snowflake): Nothing =
            entityNotFound(T::class.simpleName!!, channelId)

        /**
         * Indicates a member was not found.
         *
         * @param guildId The ID of the guild
         * @param userId The ID of the member that was not found
         * @throws EntityNotFoundException
         */
        public inline fun memberNotFound(guildId: Snowflake, userId: Snowflake): Nothing =
            guildEntityNotFound("Member", guildId = guildId, id = userId)

        /**
         * Indicates a message was not found.
         *
         * @param channelId The ID of the channel
         * @param messageId The ID of the message that was not found
         * @throws EntityNotFoundException
         */
        public inline fun messageNotFound(channelId: Snowflake, messageId: Snowflake): Nothing =
            throw EntityNotFoundException("Message with id $messageId in channel $channelId was not found.")

        /**
         * Indicates a user was not found.
         *
         * @param userId The ID of the user that was not found
         * @throws EntityNotFoundException
         */
        public inline fun userNotFound(userId: Snowflake): Nothing =
            entityNotFound("User", userId)

        /**
         * Indicates the self user was not found.
         *
         * @throws EntityNotFoundException
         */
        public inline fun selfNotFound(): Nothing =
            throw EntityNotFoundException("Self user was not found.")

        /**
         * Indicates a role was not found.
         *
         * @param guildId The ID of the guild
         * @param roleId The ID of the role that was not found
         * @throws EntityNotFoundException
         */
        public inline fun roleNotFound(guildId: Snowflake, roleId: Snowflake): Nothing =
            guildEntityNotFound("Role", guildId = guildId, id = roleId)

        /**
         * Indicates a ban was not found.
         *
         * @param guildId The ID of the guild
         * @param userId The ID of the user
         * @throws EntityNotFoundException
         */
        public inline fun banNotFound(guildId: Snowflake, userId: Snowflake): Nothing =
            guildEntityNotFound("Ban", guildId = guildId, id = userId)

        /**
         * Indicates an emoji was not found.
         *
         * @param guildId The ID of the guild
         * @param emojiId The ID of the emoji that was not found
         * @throws EntityNotFoundException
         */
        public inline fun emojiNotFound(guildId: Snowflake, emojiId: Snowflake): Nothing =
            guildEntityNotFound("GuildEmoji", guildId = guildId, id = emojiId)

        /**
         * Indicates a webhook was not found.
         *
         * @param webhookId The ID of the webhook that was not found
         * @throws EntityNotFoundException
         */
        public inline fun webhookNotFound(webhookId: Snowflake): Nothing =
            entityNotFound("Webhook", webhookId)

        /**
         * Indicates a webhook was not found.
         *
         * @param webhookId The ID of the webhook that was not found
         * @param token The token for the interaction
         * @param messageId The ID of the message that was not found
         * @param threadId The ID of the thread the webhook was not found in, or null if this is a channel
         * @throws EntityNotFoundException
         */
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

        /**
         * Indicates an invite was not found.
         *
         * @param code The code of the invite that was not found
         * @throws EntityNotFoundException
         */
        public inline fun inviteNotFound(code: String): Nothing =
            throw EntityNotFoundException("Invite with code $code was not found.")

        /**
         * Indicates a widget was not found.
         *
         * @param id The ID of the widget that was not found
         * @throws EntityNotFoundException
         */
        public inline fun widgetNotFound(id: Snowflake): Nothing =
            throw EntityNotFoundException("Widget for guild ${id.value} was not found.")

        /**
         * Indicates a template was not found.
         *
         * @param code The code of the template that was not found
         * @throws EntityNotFoundException
         */
        public inline fun templateNotFound(code: String): Nothing =
            throw EntityNotFoundException("Template $code was not found.")

        /**
         * Indicates a welcome screen was not found.
         *
         * @param guildId The ID of the guild the screen was not found
         * @throws EntityNotFoundException
         */
        public inline fun welcomeScreenNotFound(guildId: Snowflake): Nothing =
            throw EntityNotFoundException("Welcome screen for guild $guildId was not found.")

        /**
         * Indicates a state instance was not found.
         *
         * @param channelId The ID of the channel that was not found
         * @throws EntityNotFoundException
         */
        public inline fun stageInstanceNotFound(channelId: Snowflake): Nothing =
            throw EntityNotFoundException("Stage instance for channel $channelId was not found.")

        /**
         * Indicates a sticker was not found.
         *
         * @param stickerId The ID of the sticker that was not found
         * @throws EntityNotFoundException
         */
        public inline fun stickerNotFound(stickerId: Snowflake): Nothing =
            entityNotFound("Sticker", stickerId)

        /**
         * Indicates an application command permission was not found.
         *
         * @param commandId The ID of the command that was not found
         * @throws EntityNotFoundException
         */
        public inline fun applicationCommandPermissionsNotFound(commandId: Snowflake): Nothing =
            entityNotFound("ApplicationCommand", commandId)

        /**
         * Indicates an event was not found.
         *
         * @param eventId The ID of the event that was not found
         * @throws EntityNotFoundException
         */
        public inline fun guildScheduledEventNotFound(eventId: Snowflake): Nothing =
            entityNotFound("GuildScheduledEvent", eventId)

        /**
         * Indicates a guild was not found.
         *
         * @param guildId The ID of the guild that was not found
         * @throws EntityNotFoundException
         */
        public inline fun <reified T : ApplicationCommand> applicationCommandNotFound(commandId: Snowflake): Nothing =
            entityNotFound(T::class.simpleName!!, commandId)

        /**
         * Indicates an interaction was not found.
         *
         * @param token The ID of the interaction that was not found
         * @throws EntityNotFoundException
         */
        public inline fun interactionNotFound(token: String): Nothing = throw EntityNotFoundException(
            "Initial interaction response for interaction with token $token was not found."
        )

        /**
         * Indicates a followup message was not found.
         *
         * @param token The ID of the interaction
         * @param messageId The ID of the message that was not found
         * @throws EntityNotFoundException
         */
        public inline fun followupMessageNotFound(token: String, messageId: Snowflake): Nothing =
            throw EntityNotFoundException(
                "Followup message with id $messageId for interaction with token $token was not found."
            )

        /**
         * Indicates an auto-moderation rule was not found.
         *
         * @param guildId The ID of the guild
         * @param ruleId The ID rule that was not found
         * @throws EntityNotFoundException
         */
        public inline fun autoModerationRuleNotFound(guildId: Snowflake, ruleId: Snowflake): Nothing =
            guildEntityNotFound("Auto Moderation Rule", guildId, ruleId)
    }
}
